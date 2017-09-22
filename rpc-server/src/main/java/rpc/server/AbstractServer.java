package rpc.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import rpc.common.InetAddressUtil;
import rpc.server.registry.ServiceRegistry;

public abstract class AbstractServer implements ApplicationContextAware {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractServer.class);

	protected String serverAddress;
	protected ServiceRegistry serviceRegistry;
	protected int port;

	/**
	 * @param serverAddress
	 *            the serverAddress to set
	 */
	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	/**
	 * @param serviceRegistry
	 *            the serviceRegistry to set
	 */
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}


	public AbstractServer() {
		this.serverAddress = InetAddressUtil.getIpAddress();
		this.port = getPort();
		LOGGER.debug("serverAddress:" + this.serverAddress +"\t port:"+port);
	}

	private static int getPort() {
		try {
			ServerSocket serverSocket = new ServerSocket(0);
			int port = serverSocket.getLocalPort();
			serverSocket.close();
			return port;
		} catch (IOException e) {
			return 7999;
		}
	}

	@Override
	final public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		this.initalHandlerMap(ctx);
	}

	abstract protected void initalHandlerMap(ApplicationContext ctx);
	
	abstract protected Map<String, Object> getHandlerMaps();
	
	abstract protected void startServer();
}
