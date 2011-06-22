package com.firefly.net.tcp;

import com.firefly.net.*;
import com.firefly.net.exception.NetException;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;

public class TcpServer implements Server {

	private static Log log = LogFactory.getInstance().getLog("firefly-system");
    private Config config;
    private Worker[] workers;
    private Thread bossThread;
    private boolean start;

    public TcpServer() {

    }

    public TcpServer(Decoder decoder, Encoder encoder,
                     Handler handler) {
        config = new Config();
        config.setDecoder(decoder);
        config.setEncoder(encoder);
        config.setHandler(handler);
    }

    @Override
    public void setConfig(Config config) {
        this.config = config;
    }

    @Override
    public void start(String host, int port) {
        if (config == null)
            throw new NetException("server config is null");
        log.debug(config.toString());
        listen(bind(host, port));
    }

    private ServerSocketChannel bind(String host, int port) {
        ServerSocketChannel serverSocketChannel = null;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().setReuseAddress(true);
            serverSocketChannel.socket().setPerformancePreferences(
                    config.getConnectionTime(), config.getLatency(),
                    config.getBandwidth());
            log.debug("ServerSocket receiveBufferSize: [{}]",
                    serverSocketChannel.socket().getReceiveBufferSize());
            if (config.getReceiveBufferSize() > 0)
                serverSocketChannel.socket().setReceiveBufferSize(
                        config.getReceiveBufferSize());
            serverSocketChannel.socket().bind(
                    new InetSocketAddress(host, port),
                    config.getBacklog());

        } catch (Exception e) {
            log.error("ServerSocket bind error", e);
        }
        return serverSocketChannel;
    }

    private void listen(ServerSocketChannel serverSocketChannel) {
        workers = new Worker[config.getWorkerThreads()];
        for (int i = 0; i < config.getWorkerThreads(); i++) {
            workers[i] = new TcpWorker(config, i);
        }

        Boss boss = null;
        try {
            boss = new Boss(serverSocketChannel);
        } catch (IOException e) {
            log.error("Boss create error", e);
        }
        bossThread = new Thread(boss, config.getServerName());
        start = true;
        bossThread.start();
    }

    private final class Boss implements Runnable {
        private final Selector selector;
        private final ServerSocketChannel serverSocketChannel;

        public Boss(ServerSocketChannel serverSocketChannel) throws IOException {
            selector = Selector.open();
            this.serverSocketChannel = serverSocketChannel;
            this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        }

        @Override
        public void run() {

            int sessionId = 0;
            try {
                while (start) {
                    try {
                        if (selector.select(1000) > 0)
                            selector.selectedKeys().clear();

                        SocketChannel socketChannel = serverSocketChannel
                                .accept();
                        if (socketChannel != null) {
                            accept(socketChannel, sessionId);
                            sessionId++;
                        }
                    } catch (ClosedChannelException e) {
                        // Closed as requested.
                        break;
                    } catch (Throwable e) {
                        log.error("Failed to accept a connection.", e);
                    }
                }
            } finally {
                try {
                    selector.close();
                } catch (Exception e) {
                    log.error("Failed to close a selector.", e);
                }
            }

        }

        public void accept(SocketChannel socketChannel, int sessionId) {
            try {
                int workerIndex = Math.abs(sessionId) % workers.length;
                log.debug("accept sessionId [{}] and worker index [{}]",
                        sessionId, workerIndex);
                workers[workerIndex].registerSelectableChannel(socketChannel,
                        sessionId);
            } catch (Exception e) {
                log.error("Failed to initialize an accepted socket.", e);
                try {
                    socketChannel.close();
                } catch (IOException e1) {
                    log.error("Failed to close a partially accepted socket.",
                            e1);
                }
            }
        }

    }

	@Override
	public void shutdown() {
		for(Worker worker : workers) {
			worker.shutdown();
		}
		start = false;
		log.debug("thread {} is shutdown: {}", bossThread.getName(), bossThread.isInterrupted());
	}

}
