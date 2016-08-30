package rpc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import rpc.common.RpcDecoder;
import rpc.common.RpcEncoder;
import rpc.common.RpcRequest;
import rpc.common.RpcResponse;

public class RpcClient extends SimpleChannelInboundHandler<RpcResponse> {

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);

	private String host;
	private int port;

	private RpcResponse response;

	private final Object obj = new Object();

	public RpcClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
		this.response = response;
		LOGGER.info("RpcClient..3...channelRead0");
		unLock();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LOGGER.error("client caught exception", cause);
		ctx.close();
	}

	/**
	 * 发送RPC远端请求
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public RpcResponse send(RpcRequest request) throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			this.response = null;
			LOGGER.info("RpcClient...1..send");
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel channel) throws Exception {
					channel.pipeline().addLast(new RpcEncoder(RpcRequest.class)) // 绑定RPC的请求
							.addLast(new RpcDecoder(RpcResponse.class)) // 绑定RPC相应的解析
							.addLast(RpcClient.this); // 设定请求类
				}
			})
			.option(ChannelOption.SO_KEEPALIVE, true)
			.option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 131072, 131072));

			ChannelFuture future = bootstrap.connect(host, port).sync();
			future.channel().writeAndFlush(request).sync();

			lock();

			if (response != null) {
				future.channel().closeFuture().sync();
				LOGGER.info("RpcClient..4...end");
			}
			return response;
		} finally {
			group.shutdownGracefully();
		}
	}
	
	/**
	 * 锁定线程等待
	 * @param mills
	 * @throws InterruptedException
	 */
	private void lock() throws InterruptedException {
		if (response == null) {
			synchronized (obj) {
				LOGGER.info("RpcClient..2...wait");
				if (response == null) {
					obj.wait();// 等待线程
				}
			}
		}
	}

	private void unLock() {
		synchronized (obj) {
			obj.notifyAll();
		}
	}
}
