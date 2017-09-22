package rpc.server.handler;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import rpc.common.RpcRequest;
import rpc.common.RpcResponse;
import rpc.common.SerializationUtil;
import rpc.common.http.ByteBufToBytes;

public class RpcHttpHandler extends ChannelInboundHandlerAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcHttpHandler.class);
	private ByteBufToBytes reader;
	private HttpRequest request;
	
	private final Map<String, Object> handlerMap;

	public RpcHttpHandler(Map<String, Object> handlerMap) {
		this.handlerMap = handlerMap;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext,
	 *      java.lang.Object)
	 */
	@Override
	public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
		RpcResponse response = new RpcResponse();
		if (msg instanceof HttpRequest) {  
            request = (HttpRequest) msg;  
            if (HttpUtil.isContentLengthSet(request)) {  
                reader = new ByteBufToBytes((int) HttpUtil.getContentLength(request));  
            }  
        }  
  
        if (msg instanceof HttpContent) {  
            HttpContent httpContent = (HttpContent) msg;  
            ByteBuf content = httpContent.content();  
            reader.reading(content);  
            content.release();  
  
            if (reader.isEnd()) {  
                RpcRequest request = SerializationUtil.deserialize(reader.readFull(), RpcRequest.class);
                try {
        			LOGGER.info("RpcHttpHandler.channelRead0 deal with id = {}", request.getRequestId());
        			response.setRequestId(request.getRequestId());
        			Object result = handle(request);
        			response.setResult(result);
        			LOGGER.info("RpcHttpHandler.channelRead0 ended with id ={}", request.getRequestId());
        		} catch (Throwable t) {
        			LOGGER.error("RpcHttpHandler.channelRead0 failed of id = {}!", request.getRequestId(), t);
        			response.setError(t);
        		}
                try {
					FullHttpResponse httpresponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(SerializationUtil.serialize(response)));  
					httpresponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");  
					httpresponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpresponse.content().readableBytes());  
					ctx.writeAndFlush(httpresponse).addListener(ChannelFutureListener.CLOSE);  
                } catch (Throwable t) {
                    ctx.channel().close().sync();
                }
               
            }  
        }  
	}

	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	/**
	 * 处理消息
	 * 
	 * @param request
	 * @return
	 * @throws Throwable
	 */
	private Object handle(RpcRequest request) throws Throwable {
		LOGGER.info("RpcHandler.handle deal with id =" + request.getRequestId());
		String className = request.getClassName();
		Object serviceBean = handlerMap.get(className);

		Class<?> serviceClass = serviceBean.getClass();
		String methodName = request.getMethodName();
		Class<?>[] parameterTypes = request.getParameterTypes();
		Object[] parameters = request.getParameters();

		/*
		 * Method method = serviceClass.getMethod(methodName, parameterTypes);
		 * method.setAccessible(true); return method.invoke(serviceBean,
		 * parameters);
		 */

		FastClass serviceFastClass = FastClass.create(serviceClass);
		FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
		LOGGER.info("RpcHandler.handle ened with id =" + request.getRequestId());
		return serviceFastMethod.invoke(serviceBean, parameters);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		LOGGER.error("server caught exception", cause);
		ctx.close();
	}

}
