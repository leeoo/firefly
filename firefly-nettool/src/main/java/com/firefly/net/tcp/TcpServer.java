package com.firefly.net.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.firefly.net.Config;
import com.firefly.net.Decoder;
import com.firefly.net.Encoder;
import com.firefly.net.Handler;
import com.firefly.net.Server;
import com.firefly.net.Worker;
import com.firefly.net.exception.NetException;

public class TcpServer implements Server {

	private static Logger log = LoggerFactory.getLogger(TcpServer.class);
	private Config config;
	private Worker[] workers;

	public TcpServer() {

	}

	/**
	 * 使用默认配置构造服务
	 * 
	 * @param host
	 *            主机名
	 * @param port
	 *            端口
	 * @param decoder
	 *            解码器
	 * @param encoder
	 *            编码器
	 * @param handler
	 *            业务处理钩子
	 */
	public TcpServer(String host, int port, Decoder decoder, Encoder encoder,
			Handler handler) {
		config = new Config();
		config.setHost(host);
		config.setPort(port);
		config.setDecoder(decoder);
		config.setEncoder(encoder);
		config.setHandler(handler);
	}

	@Override
	public void setConfig(Config config) {
		this.config = config;
	}

	@Override
	public void start() {
		if (config == null)
			throw new NetException("server config is null");
		log.debug(config.toString());
		listen(bind());
	}

	/**
	 * 绑定服务端ip和端口
	 * 
	 * @return ServerSocketChannel
	 */
	private ServerSocketChannel bind() {
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
					new InetSocketAddress(config.getHost(), config.getPort()),
					config.getBacklog());

		} catch (Exception e) {
			log.error("ServerSocket bind error", e);
		}
		return serverSocketChannel;
	}

	/**
	 * 监听已经绑定的服务
	 * 
	 * @param serverSocketChannel
	 */
	private void listen(ServerSocketChannel serverSocketChannel) {
		workers = new Worker[config.getWorkerThreads()];
		for (int i = 0; i < config.getWorkerThreads(); i++) {
			workers[i] = new TcpWorker(config);
		}

		Boss boss = null;
		try {
			boss = new Boss(serverSocketChannel);
		} catch (IOException e) {
			log.error("Boss create error", e);
		}
		new Thread(boss, config.getServerName()).start();
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
				while (true) {
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
				workers[workerIndex].registerSocketChannel(socketChannel,
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

}
