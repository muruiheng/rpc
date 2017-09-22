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
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import rpc.common.Constant;
import rpc.common.InetAddressUtil;
import rpc.common.RpcDecoder;
import rpc.common.RpcEncoder;
import rpc.common.RpcRequest;
import rpc.common.RpcResponse;
import rpc.common.annotation.RpcService;
import rpc.server.handler.RpcHandler;
import rpc.server.registry.ServiceRegistry;

/**
 * rpc server domain <br/>
 * this class to start rpc server and excute register method 
 * {@ExtRpcServer} 改为使用ExtRpcServer 进行统一的启动， 新的启动方式默认提供端口号，不需要用户再自行指定端口号；
 * 2016-3-6
 * @author mrh
 *
 */
@Deprecated 
public class RpcServer implements ApplicationContextAware, InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

	private String serverAddress;
	private ServiceRegistry serviceRegistry;
	private int port;
	
	private static final Map<String, Object> handlerMap = new HashMap<String, Object>(); 

	
	public RpcServer() {
		this.serverAddress = InetAddressUtil.getIpAddress();
		this.port = getPort();
		LOGGER.debug("");
	}

	public RpcServer(ServiceRegistry serviceRegistry, int port) {
		this.serverAddress = InetAddressUtil.getIpAddress();
		//读取空闲的可用端口
		this.port = port;
		this.serviceRegistry = serviceRegistry;
	}
	
	public RpcServer(ServiceRegistry serviceRegistry, String serverAddress, int port) {
		this.serverAddress = serverAddress;
		//读取空闲的可用端口
		this.port = port;
		this.serviceRegistry = serviceRegistry;
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
		Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class); // get the rpc serice in spring context
		if (MapUtils.isNotEmpty(serviceBeanMap) && serviceBeanMap.values() != null) {
			for (Object serviceBean : serviceBeanMap.values()) {
				String interfaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
				LOGGER.info("interfaceName = " + interfaceName);
				handlerMap.put(interfaceName, serviceBean);
			}
		} else {
			LOGGER.warn("non rpc service!");
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
								channel.pipeline().addLast(new RpcDecoder(RpcRequest.class)) 
										.addLast(new RpcEncoder(RpcResponse.class))
										.addLast(new RpcHandler(handlerMap));
							}
						})
						.option(ChannelOption.SO_BACKLOG, 128)
						.option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 131072, 131072))
						.childOption(ChannelOption.SO_KEEPALIVE, true);
				

				ChannelFuture future = bootstrap.bind(port).sync();
				LOGGER.info("RpcServer.afterPropertiesSet() -- service has bind for port:{}!", port);

				if (serviceRegistry != null) {
					LOGGER.info("RpcServer.afterPropertiesSet() -- to register rpc service: {}:{}! ", serverAddress, port);
					serviceRegistry.register(serverAddress + ":" + port, handlerMap.keySet(), Constant.ZK_DATA_PATH); // register service
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