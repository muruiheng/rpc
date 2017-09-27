package rpc.server;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import io.netty.util.internal.StringUtil;
import rpc.common.RpcException;
import rpc.server.registry.ServiceRegistry;

/**
 * rpc server domain <br/>
 * this class to start rpc server and excute register method 
 * 2016-3-6
 * @author mrh
 *
 */
public class ExtRpcServer implements ApplicationContextAware, InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExtRpcServer.class);

	private Set<AbstractServer> servers = new HashSet<AbstractServer>();
	protected String serverAddress;
	protected ServiceRegistry serviceRegistry;
	private int tcpPort;
	private int httpPort;
	
	/**
	 * @param tcpPort the tcpPort to set
	 */
	public void setTcpPort(int tcpPort) {
		this.tcpPort = tcpPort;
	}
	/**
	 * @param httpPort the httpPort to set
	 */
	public void setHttpPort(int httpPort) {
		this.httpPort = httpPort;
	}
	/**
	 * @param servers the servers to set
	 */
	public void setServers(Set<AbstractServer> servers) {
		if (!this.servers.isEmpty() && servers != null) {
			this.servers.clear();
			this.servers = null;
		}
		this.servers = servers;
	}
	/**
	 * @param serverAddress the serverAddress to set
	 */
	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}
	/**
	 * @param serviceRegistry the serviceRegistry to set
	 */
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
	
	protected void initalServers() {
		
		
		this.servers.add(this.createHttServer());
		LOGGER.debug("initaled  HttServer !");
		this.servers.add(this.createTCPServer());
		LOGGER.debug("initaled  TcpServer !");
	}
	
	/**
	 * 创建HTTPServer
	 * @return AbstractServer
	 */
	private AbstractServer createHttServer() {
		AbstractServer server = new HttpServer();
		if (this.httpPort > 0)
			server.setPort(httpPort);
		if (!StringUtil.isNullOrEmpty(serverAddress)) {
			server.setServerAddress(this.serverAddress);
		}
		
		if (this.serviceRegistry == null ) {
			throw new RpcException("the serviceRegistry can not be null!");
		}
		server.setServiceRegistry(serviceRegistry);
		return server;
	}
	
	/**
	 * 创建TcpServer
	 * @return AbstractServer
	 */
	private AbstractServer createTCPServer() {
		AbstractServer server = new TcpServer();
		if (this.tcpPort > 0)
			server.setPort(tcpPort);
		if (!StringUtil.isNullOrEmpty(serverAddress)) {
			server.setServerAddress(this.serverAddress);
		}
		if (this.serviceRegistry == null ) {
			throw new RpcException("the serviceRegistry can not be null!");
		}
		server.setServiceRegistry(serviceRegistry);
		return server;
	}
	
	/**
	 * 启动rpc服务器
	 */
	private void startServers() {
		for (AbstractServer server : servers) {
			server.startServer();
			LOGGER.debug(server.getClass().getName() + " started!");
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
		startServers();
		LOGGER.debug("startServers started!");
	}
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (this.servers.isEmpty()) {
			initalServers();
		}
		for (AbstractServer server : servers) {
			server.setApplicationContext(applicationContext);
		}
	}
	
	
}