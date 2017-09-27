package rpc.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

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
import rpc.common.RpcDecoder;
import rpc.common.RpcEncoder;
import rpc.common.RpcRequest;
import rpc.common.RpcResponse;
import rpc.common.annotation.RpcService;
import rpc.common.annotation.TcpService;
import rpc.server.handler.RpcHandler;

/**
 * rpc server domain <br/>
 * this class to start rpc server and excute register method 
 * 2016-3-6
 * @author mrh
 *
 */
public class TcpServer extends AbstractServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(TcpServer.class);
	protected static final Map<String, Object> handlerMap = new ConcurrentHashMap<String, Object>();
	
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

	@Override
	protected void initalHandlerMap(ApplicationContext ctx) {
		LOGGER.info("RpcServer.setApplicationContext() -- to set ApplicationContext for RPC SERVER");
		Map<String, Object> tcpServices = ctx.getBeansWithAnnotation(TcpService.class); // get the tcp serice in spring context
		Map<String, Object> rpcServices = ctx.getBeansWithAnnotation(RpcService.class); 
		if (rpcServices != null && !rpcServices.isEmpty()) 
			tcpServices.putAll(tcpServices);
		
		if (MapUtils.isNotEmpty(tcpServices) && tcpServices.values() != null) {
			for (Object serviceBean : tcpServices.values()) {
				String interfaceName = serviceBean.getClass().getAnnotation(TcpService.class).value().getName();
				LOGGER.info("interfaceName = " + interfaceName);
				handlerMap.put(interfaceName, serviceBean);
			}
		} else {
			LOGGER.warn("non rpc service!");
		}
		LOGGER.info("RpcServer.setApplicationContext() -- to set ApplicationContext for RPC SERVER COMPLETED!");
		
	}

	@Override
	protected void startServer() {
		Thread thread = new Thread(new ChannelStartRunner());
		thread.start();
	}

	@Override
	protected Map<String, Object> getHandlerMaps() {
		return handlerMap;
	}
}