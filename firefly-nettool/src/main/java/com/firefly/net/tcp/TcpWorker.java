package com.firefly.net.tcp;

import com.firefly.net.*;
import com.firefly.net.buffer.SocketReceiveBufferPool;
import com.firefly.net.buffer.SocketSendBufferPool;
import com.firefly.net.buffer.SocketSendBufferPool.SendBuffer;
import com.firefly.net.event.CurrentThreadEventManager;
import com.firefly.net.event.ThreadPoolEventManager;
import com.firefly.net.exception.NetException;
import com.firefly.utils.timer.TimeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class TcpWorker implements Worker {

    private static Logger log = LoggerFactory.getLogger(TcpWorker.class);
    private Config config;
    private final Queue<Runnable> registerTaskQueue = new ConcurrentLinkedQueue<Runnable>();
    private final Queue<Runnable> writeTaskQueue = new ConcurrentLinkedQueue<Runnable>();
    private final AtomicBoolean wakenUp = new AtomicBoolean();
    private final ReceiveBufferPool receiveBufferPool = new SocketReceiveBufferPool();
    private final SendBufferPool sendBufferPool = new SocketSendBufferPool();
    private final Selector selector;
    private volatile int cancelledKeys;
    static final TimeProvider timeProvider = new TimeProvider(100);
    private Thread thread;
    private final int workerId;
    private EventManager eventManager;

    static {
        timeProvider.start();
    }

    public TcpWorker(Config config, int workerId) {
        try {
            this.workerId = workerId;
            this.config = config;
            selector = Selector.open();
            if (config.getHandleThreads() >= 0) {
                log.debug("new ThreadPoolEventManager");
                eventManager = new ThreadPoolEventManager(config);
            } else {
                log.debug("new CurrentThreadEventManager");
                eventManager = new CurrentThreadEventManager(config);
            }
            new Thread(this, "Tcp-worker: " + workerId).start();
        } catch (IOException e) {
            log.error("worker init error", e);
            throw new NetException("worker init error");
        }
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public int getWorkerId() {
        return workerId;
    }

    @Override
    public void registerSocketChannel(SocketChannel socketChannel, int sessionId) {
        registerTaskQueue.offer(new RegisterTask(socketChannel, sessionId));
        if (wakenUp.compareAndSet(false, true))
            selector.wakeup();
    }

    @Override
    public void run() {
        thread = Thread.currentThread();

        while (true) {
            wakenUp.set(false);
            try {
                select(selector);
                if (wakenUp.get())
                    selector.wakeup();

                cancelledKeys = 0;
                processRegisterTaskQueue();
                processWriteTaskQueue();
                processSelectedKeys(selector.selectedKeys());
            } catch (Throwable t) {
                log.error("Unexpected exception in the selector loop.", t);

                // Prevent possible consecutive immediate failures that lead to
                // excessive CPU consumption.
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // Ignore.
                }
            }
        }

    }

    private void processWriteTaskQueue() throws IOException {
        while (true) {
            Runnable task = writeTaskQueue.poll();
            if (task == null)
                break;
            task.run();
            cleanUpCancelledKeys();
        }

    }

    private void processSelectedKeys(Set<SelectionKey> selectedKeys)
            throws IOException {
        for (Iterator<SelectionKey> i = selectedKeys.iterator(); i.hasNext(); ) {
            SelectionKey k = i.next();
            i.remove();
            try {
                int readyOps = k.readyOps();
                if ((readyOps & SelectionKey.OP_READ) != 0 || readyOps == 0) {
                    if (!read(k)) {
                        // Connection already closed - no need to handle write.
                        continue;
                    }
                }
                if ((readyOps & SelectionKey.OP_WRITE) != 0) {
                    writeFromSelectorLoop(k);
                }
            } catch (CancelledKeyException e) {
                close(k);
            }

            if (cleanUpCancelledKeys())
                break;
        }

    }

    void writeFromUserCode(final TcpSession session) {
        if (!session.isOpen()) {
            cleanUpWriteBuffer(session);
            return;
        }

        if (scheduleWriteIfNecessary(session)) {
            return;
        }

        // From here, we are sure Thread.currentThread() == workerThread.
        if (session.isWriteSuspended() || session.isInWriteNowLoop())
            return;

        log.debug("worker thread write");
        write0(session);
    }

    private boolean scheduleWriteIfNecessary(final TcpSession session) {
        log.debug("worker thread {} | current thread {}", thread.toString(), Thread.currentThread().toString());
        if (Thread.currentThread() != thread) {
            if (session.getWriteTaskInTaskQueue().compareAndSet(false, true)) {
                boolean offered = writeTaskQueue.offer(session.getWriteTask());
                assert offered;
            }
            if (wakenUp.compareAndSet(false, true))
                selector.wakeup();
            return true;
        }
        return false;
    }

    void writeFromTaskLoop(final TcpSession session) {
        if (!session.isWriteSuspended())
            write0(session);
    }

    private void writeFromSelectorLoop(SelectionKey k) {
        final TcpSession session = (TcpSession) k.attachment();
        session.setWriteSuspended(false);
        write0(session);
    }

    private void write0(TcpSession session) {
        boolean open = true;
        boolean addOpWrite = false;
        boolean removeOpWrite = false;
        long writtenBytes = 0;

        final SocketChannel ch = (SocketChannel) session.getSelectionKey()
                .channel();
        final Queue<Object> writeBuffer = session.getWriteBuffer();
        final int writeSpinCount = config.getWriteSpinCount();
        synchronized (session.getWriteLock()) {
            session.setInWriteNowLoop(true);
            while (true) {
                Object obj = session.getCurrentWrite();
                SendBuffer buf;
                if (obj == null) {
                    if ((obj = writeBuffer.poll()) == null) {
                        session.setCurrentWrite(obj);
                        removeOpWrite = true;
                        session.setWriteSuspended(false);
                        break;
                    }
                    if (obj == Session.CLOSE_FLAG) {
                        close(session.getSelectionKey());
                        break;
                    }
                    buf = sendBufferPool.acquire(obj);
                    session.setCurrentWriteBuffer(buf);
                } else {
                    if (obj == Session.CLOSE_FLAG) {
                        close(session.getSelectionKey());
                        break;
                    }
                    buf = session.getCurrentWriteBuffer();
                }

                try {
                    long localWrittenBytes;
                    for (int i = writeSpinCount; i > 0; i--) {
                        localWrittenBytes = buf.transferTo(ch);
                        if (localWrittenBytes != 0) {
                            writtenBytes += localWrittenBytes;
                            break;
                        }
                        if (buf.finished()) {
                            break;
                        }
                    }

                    if (buf.finished()) {
                        // Successful write - proceed to the next message.
                        buf.release();
                        session.setCurrentWrite(null);
                        session.setCurrentWriteBuffer(null);
                        obj = null;
                        buf = null;
                    } else {
                        // Not written fully - perhaps the kernel buffer is
                        // full.
                        addOpWrite = true;
                        session.setWriteSuspended(true);
                        break;
                    }
                } catch (AsynchronousCloseException e) {
                    // Doesn't need a user attention - ignore.
                } catch (Throwable t) {
                    buf.release();
                    session.setCurrentWrite(null);
                    session.setCurrentWriteBuffer(null);
                    buf = null;
                    obj = null;
                    eventManager.executeExceptionTask(session, t);
                    if (t instanceof IOException) {
                        open = false;
                        close(session.getSelectionKey());
                    }
                }
            }
            session.setInWriteNowLoop(false);
        }

        log.debug("write complete size: {}", writtenBytes);

        if (open) {
            if (addOpWrite) {
                setOpWrite(session);
            } else if (removeOpWrite) {
                clearOpWrite(session);
            }
        }
    }

    private void cleanUpWriteBuffer(TcpSession session) {
        Exception cause = null;
        boolean fireExceptionCaught = false;

        // Clean up the stale messages in the write buffer.
        synchronized (session.getWriteLock()) {
            Object obj = session.getCurrentWrite();
            if (obj != null) {
                cause = new NetException("cleanUpWriteBuffer error");
                session.getCurrentWriteBuffer().release();
                session.setCurrentWriteBuffer(null);
                session.setCurrentWrite(null);
                obj = null;
                fireExceptionCaught = true;
            }

            Queue<Object> writeBuffer = session.getWriteBuffer();
            if (!writeBuffer.isEmpty()) {
                // Create the exception only once to avoid the excessive
                // overhead
                // caused by fillStackTrace.
                if (cause == null) {
                    cause = new NetException("cleanUpWriteBuffer error");
                }

                while (true) {
                    obj = writeBuffer.poll();
                    if (obj == null) {
                        break;
                    }
                    fireExceptionCaught = true;
                }
            }
        }

        if (fireExceptionCaught)
            eventManager.executeExceptionTask(session, cause);

    }

    private boolean read(SelectionKey k) {
        final SocketChannel ch = (SocketChannel) k.channel();
        final TcpSession session = (TcpSession) k.attachment();
        final ReceiveBufferSizePredictor predictor = session.getReceiveBufferSizePredictor();
        final int predictedRecvBufSize = predictor.nextReceiveBufferSize();

        int ret = 0;
        int readBytes = 0;
        boolean failure = true;

        ByteBuffer bb = receiveBufferPool.acquire(predictedRecvBufSize);
        try {
            while ((ret = ch.read(bb)) > 0) {
                readBytes += ret;
                if (!bb.hasRemaining())
                    break;
            }
            failure = false;
        } catch (ClosedChannelException e) {
            // Can happen, and does not need a user attention.
        } catch (Throwable t) {
            eventManager.executeExceptionTask(session, t);
        }

        if (readBytes > 0) {
            bb.flip();

            receiveBufferPool.release(bb);

            // Update the predictor.
            predictor.previousReceiveBufferSize(readBytes);

            // Decode
            config.getDecoder().decode(bb, session);
        } else {
            receiveBufferPool.release(bb);
        }

        if (ret < 0 || failure) {
            close(k);
            return false;
        }

        return true;
    }

    private void processRegisterTaskQueue() throws IOException {
        while (true) {
            Runnable task = registerTaskQueue.poll();
            if (task == null)
                break;
            task.run();
            cleanUpCancelledKeys();
        }
    }

    private final class RegisterTask implements Runnable {

        private SocketChannel socketChannel;
        private int sessionId;

        public RegisterTask(SocketChannel socketChannel, int sessionId) {
            this.socketChannel = socketChannel;
            this.sessionId = sessionId;
        }

        @Override
        public void run() {

            SelectionKey key = null;
            try {
                socketChannel.configureBlocking(false);
                socketChannel.socket().setReuseAddress(true);
                socketChannel.socket().setTcpNoDelay(true);
                socketChannel.socket().setKeepAlive(true);
                if (config.getReceiveBufferSize() > 0)
                    socketChannel.socket().setReceiveBufferSize(
                            config.getReceiveBufferSize());
                if (config.getSendBufferSize() > 0)
                    socketChannel.socket().setSendBufferSize(
                            config.getSendBufferSize());

                key = socketChannel.register(selector, SelectionKey.OP_READ);
                Session session = new TcpSession(sessionId, TcpWorker.this,
                        config, timeProvider.currentTimeMillis(), key);
                key.attach(session);

                SocketAddress localAddress = session.getLocalAddress();
                SocketAddress remoteAddress = session.getRemoteAddress();
                if (localAddress == null || remoteAddress == null) {
                    TcpWorker.this.close(key);
                }

                eventManager.executeOpenTask(session);
            } catch (IOException e) {
                log.error("socketChannel register error", e);
                close(key);
            }

        }

    }

    public void close(SelectionKey key) {
        try {
            key.channel().close();
            increaseCancelledKey();
            TcpSession session = (TcpSession) key.attachment();
            session.setState(Session.CLOSE);
            cleanUpWriteBuffer(session);
            eventManager.executeCloseTask(session);

        } catch (IOException e) {
            log.error("channel close error", e);
        }
    }

    static void select(Selector selector) throws IOException {
        try {
            selector.select(500);
        } catch (CancelledKeyException e) {
            // Harmless exception - log anyway
            log.debug(CancelledKeyException.class.getSimpleName()
                    + " raised by a Selector - JDK bug?", e);
        }
    }

    private boolean cleanUpCancelledKeys() throws IOException {
        if (cancelledKeys >= config.getCleanupInterval()) {
            cancelledKeys = 0;
            selector.selectNow();
            return true;
        }
        return false;
    }

    private void increaseCancelledKey() {
        int temp = cancelledKeys;
        temp++;
        cancelledKeys = temp;
    }

    private void setOpWrite(TcpSession session) {
        SelectionKey key = session.getSelectionKey();
        if (key == null) {
            return;
        }
        if (!key.isValid()) {
            close(key);
            return;
        }

        // interestOps can change at any time and at any thread.
        // Acquire a lock to avoid possible race condition.
        synchronized (session.getInterestOpsLock()) {
            int interestOps = session.getRawInterestOps();
            if ((interestOps & SelectionKey.OP_WRITE) == 0) {
                interestOps |= SelectionKey.OP_WRITE;
                key.interestOps(interestOps);
                session.setInterestOpsNow(interestOps);
            }
        }
    }

    private void clearOpWrite(TcpSession session) {
        SelectionKey key = session.getSelectionKey();
        if (key == null) {
            return;
        }
        if (!key.isValid()) {
            close(key);
            return;
        }

        // interestOps can change at any time and at any thread.
        // Acquire a lock to avoid possible race condition.
        synchronized (session.getInterestOpsLock()) {
            int interestOps = session.getRawInterestOps();
            if ((interestOps & SelectionKey.OP_WRITE) != 0) {
                interestOps &= ~SelectionKey.OP_WRITE;
                key.interestOps(interestOps);
                session.setInterestOpsNow(interestOps);
            }
        }
    }

    void setInterestOps(TcpSession session, int interestOps) {
        boolean changed = false;
        try {
            // interestOps can change at any time and at any thread.
            // Acquire a lock to avoid possible race condition.
            synchronized (session.getInterestOpsLock()) {
                SelectionKey key = session.getSelectionKey();

                if (key == null || selector == null) {
                    // Not registered to the worker yet.
                    // Set the rawInterestOps immediately; RegisterTask will
                    // pick it up.
                    session.setInterestOpsNow(interestOps);
                    return;
                }

                // Override OP_WRITE flag - a user cannot change this flag.
                interestOps &= ~SelectionKey.OP_WRITE;
                interestOps |= session.getRawInterestOps()
                        & SelectionKey.OP_WRITE;

                /**
                 * 0 - no need to wake up to get / set interestOps (most cases)
                 * 1 - no need to wake up to get interestOps, but need to wake
                 * up to set. 2 - need to wake up to get / set interestOps (old
                 * providers)
                 */
                if (session.getRawInterestOps() != interestOps) {
                    key.interestOps(interestOps);
                    if (Thread.currentThread() != thread
                            && wakenUp.compareAndSet(false, true)) {
                        selector.wakeup();
                    }
                    changed = true;
                }

                if (changed) {
                    session.setInterestOpsNow(interestOps);
                }
            }

            if (changed) {
                log.debug("interestOps change [{}]", interestOps);
                setInterestOps(session, SelectionKey.OP_READ);
            }
        } catch (CancelledKeyException e) {
            // setInterestOps() was called on a closed channel.
            ClosedChannelException cce = new ClosedChannelException();
            eventManager.executeExceptionTask(session, cce);
        } catch (Throwable t) {
            eventManager.executeExceptionTask(session, t);
        }
    }
}
