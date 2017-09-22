package rpc.client;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import rpc.common.RpcRequest;
import rpc.common.RpcResponse;
import rpc.common.SerializationUtil;
import rpc.common.http.ByteBufToBytes;


/**
 * 发起HTTP请求的客户端
 * @author mrh
 *
 */
public class HttpClient extends ChannelInboundHandlerAdapter implements Client {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);

	private ByteBufToBytes reader;
	private String host;
	private int port;

	private RpcResponse response;

	private final Object obj = new Object();

	public HttpClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("channelRead.........");
		if (msg instanceof HttpResponse) {
			HttpResponse httpresponse = (HttpResponse) msg;
			System.out.println("CONTENT_TYPE:" + httpresponse.headers().get(HttpHeaderNames.CONTENT_TYPE));
			if (HttpUtil.isContentLengthSet(httpresponse)) {
				reader = new ByteBufToBytes((int) HttpUtil.getContentLength(httpresponse));
			}
		}

		if (msg instanceof HttpContent) {
			HttpContent httpContent = (HttpContent) msg;
			ByteBuf content = httpContent.content();
			reader.reading(content);
			content.release();

			if (reader.isEnd()) {
				this.response = SerializationUtil.deserialize(reader.readFull(), RpcResponse.class);
				unLock();
			}
		}
		LOGGER.debug("channelRead..4...end");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LOGGER.error("4 client caught exception", cause);
		ctx.close();
		unLock();
	}

	

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		LOGGER.debug("channelInactive................");
	}

	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		LOGGER.debug("channelReadComplete................");
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
					channel.pipeline().addLast(new HttpRequestEncoder()) // 绑定RPC的请求
							.addLast(new HttpResponseDecoder()) // 绑定RPC相应的解析
							.addLast(HttpClient.this); // 设定请求类
				}
			}).option(ChannelOption.SO_KEEPALIVE, true).option(ChannelOption.RCVBUF_ALLOCATOR,
					new AdaptiveRecvByteBufAllocator(64, 131072, 131072));

			ChannelFuture future = bootstrap.connect(host, port).sync();
			
			URI uri = new URI("http://"+host+":"+port);
			DefaultFullHttpRequest httprequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,  
                    uri.toASCIIString(), Unpooled.wrappedBuffer(SerializationUtil.serialize(request)));  
            // 构建http请求  
			httprequest.headers().set(HttpHeaderNames.HOST, host);  
			httprequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);  
			httprequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, httprequest.content().readableBytes());  
			httprequest.headers().set("id", request.getRequestId());  
            
			future.channel().writeAndFlush(httprequest).sync();

			lock();

			future.channel().closeFuture().sync();
			LOGGER.info("RpcClient..4...end");
			return response;
		} finally {
			group.shutdownGracefully();
		}
	}

	/**
	 * 锁定线程等待
	 * 
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
