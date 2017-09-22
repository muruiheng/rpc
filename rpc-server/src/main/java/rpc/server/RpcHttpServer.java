package rpc.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import rpc.common.Constant;
import rpc.common.InetAddressUtil;
import rpc.common.annotation.HttpService;
import rpc.server.handler.RpcHttpHandler;
import rpc.server.registry.ServiceRegistry;

/**
 * 改用{@HttpServer}
 * {@ExtRpcServer} 改为使用ExtRpcServer 进行统一的启动， 新的启动方式默认提供端口号，不需要用户再自行指定端口号；
 * @author mrh
 *
 */
@Deprecated
public class RpcHttpServer implements ApplicationContextAware, InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

	private String serverAddress;
	private ServiceRegistry serviceRegistry;
	private int port;
	
	private static final Map<String, Object> handlerMap = new HashMap<String, Object>(); 

	
	public RpcHttpServer() {
		this.serverAddress = InetAddressUtil.getIpAddress();
		this.port = getPort();
		LOGGER.debug("RpcHttpServer....server address = http://"+this.serverAddress + ":" + this.port);
	}

	public RpcHttpServer(ServiceRegistry serviceRegistry, int port) {
		this.serverAddress = InetAddressUtil.getIpAddress();
		//读取空闲的可用端口
		this.port = port;
		this.serviceRegistry = serviceRegistry;
		LOGGER.debug("RpcHttpServer....server address = http://"+this.serverAddress + ":" + this.port);
	}
	
	public RpcHttpServer(ServiceRegistry serviceRegistry, String serverAddress, int port) {
		this.serverAddress = serverAddress;
		//读取空闲的可用端口
		this.port = port;
		this.serviceRegistry = serviceRegistry;
		LOGGER.debug("RpcHttpServer....server address = http://"+this.serverAddress + ":" + this.port);
	}

	private static int getPort() {
		for (int i = 9000; i< 10000; i++) {
			try {
				ServerSocket serverSocket =  new ServerSocket(9000);
				serverSocket.close();
				return serverSocket.getLocalPort();
			} catch (IOException e) {
				continue;
			} 
		}
		return 7999;
	}
	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		LOGGER.info("RpcServer.setApplicationContext() -- to set ApplicationContext for RPC SERVER");
		Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(HttpService.class); // get the rpc serice in spring context
		if (MapUtils.isNotEmpty(serviceBeanMap) && serviceBeanMap.values() != null) {
			for (Object serviceBean : serviceBeanMap.values()) {
				String interfaceName = serviceBean.getClass().getAnnotation(HttpService.class).value().getName();
				LOGGER.info("interfaceName = " + interfaceName);
				handlerMap.put(interfaceName, serviceBean);
			}
		} else {
			LOGGER.warn("non http service!");
		}
		LOGGER.info("RpcServer.setApplicationContext() -- to set ApplicationContext for RPC SERVER COMPLETED!");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		LOGGER.info("RpcServer.afterPropertiesSet() -- begin!");
		Runnable runner = new ChannelStartRunner();
		new Thread(runner).start();
	}
	
	private class ChannelStartRunner implements Runnable {

		@Override
		public void run() {
			EventLoopGroup bossGroup = new NioEventLoopGroup();
			EventLoopGroup workerGroup = new NioEventLoopGroup();
			try {
				ServerBootstrap bootstrap = new ServerBootstrap();
				bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
						.childHandler(new ChannelInitializer<SocketChannel>() {
							@Override
							public void initChannel(SocketChannel channel) throws Exception {
								channel.pipeline().addLast(new HttpRequestDecoder()) 
										.addLast(new HttpResponseEncoder())
										.addLast(new RpcHttpHandler(handlerMap));
							}
						})
						.option(ChannelOption.SO_BACKLOG, 128)
						.childOption(ChannelOption.SO_KEEPALIVE, true);
				

				ChannelFuture future = bootstrap.bind(port).sync();
				LOGGER.info("RpcServer.afterPropertiesSet() -- service has bind for port:{}!", port);

				if (serviceRegistry != null) {
					LOGGER.info("RpcServer.afterPropertiesSet() -- to register rpc service: {}:{}! ", serverAddress, port);
					serviceRegistry.register(serverAddress + ":" + port, handlerMap.keySet(), Constant.ZK_HTTP_PATH); // register service
				}

				future.channel().closeFuture().sync();
			} catch (Exception e) {
				LOGGER.error("ChannelStartRunner.run() -- ", e);
			} finally {
				workerGroup.shutdownGracefully();
				bossGroup.shutdownGracefully();
			}
			
		}
		
	}
}
