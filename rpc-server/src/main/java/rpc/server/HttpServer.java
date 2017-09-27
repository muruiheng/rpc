package rpc.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

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
import rpc.common.annotation.HttpService;
import rpc.server.handler.RpcHttpHandler;

public class HttpServer extends AbstractServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);
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


	@Override
	protected void startServer() {
		Thread thread = new Thread(new ChannelStartRunner());
		thread.start();
	}


	@Override
	protected void initalHandlerMap(ApplicationContext ctx) {
		Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(HttpService.class); // get the rpc serice in spring context
		if (MapUtils.isNotEmpty(serviceBeanMap) && serviceBeanMap.values() != null) {
			for (Object serviceBean : serviceBeanMap.values()) {
				String interfaceName = serviceBean.getClass().getAnnotation(HttpService.class).value().getName();
				LOGGER.debug("interfaceName = " + interfaceName);
				handlerMap.put(interfaceName, serviceBean);
			}
		} else {
			LOGGER.warn("non http service!");
		}
		LOGGER.debug("RpcServer.setApplicationContext() -- to set ApplicationContext for RPC SERVER COMPLETED!");
	}

	@Override
	protected Map<String, Object> getHandlerMaps() {
		return handlerMap;
	}
}
