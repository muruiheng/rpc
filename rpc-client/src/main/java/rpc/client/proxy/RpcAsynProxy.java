package rpc.client.proxy;

import java.lang.reflect.Method;
import java.util.UUID;

import org.apache.log4j.Logger;

import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.Proxy;
import rpc.client.Callback;
import rpc.client.RpcAsynClient;
import rpc.client.discover.ZKServiceDiscovery;
import rpc.common.RpcException;
import rpc.common.RpcRequest;

public class RpcAsynProxy {
	private static final Logger logger = Logger.getLogger(RpcProxy.class);
	private String serverAddress;
	private ZKServiceDiscovery serviceDiscovery;

	
	public RpcAsynProxy(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public RpcAsynProxy(ZKServiceDiscovery serviceDiscovery) {
		this.serviceDiscovery = serviceDiscovery;
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Class<?> interfaceClass, final Callback callback) {
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass },
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						if (logger.isDebugEnabled())
							logger.debug("RpcProxy....to send rpc request!");
						RpcRequest request = new RpcRequest(); // 初始化 RPC 请求报错
						request.setRequestId(UUID.randomUUID().toString());
						request.setClassName(method.getDeclaringClass().getName());
						request.setMethodName(method.getName());
						request.setParameterTypes(method.getParameterTypes());
						request.setParameters(args);

						if (serviceDiscovery != null) {
							serverAddress = serviceDiscovery.discover(request.getClassName()); // 发现服务
						}
						if (serverAddress == null) {
							throw new RpcException("没有找到" + request.getClassName() + "对应的服务地址！");
						}
						String[] array = serverAddress.split(":");
						String host = array[0];
						int port = Integer.parseInt(array[1]);

						RpcAsynClient client = new RpcAsynClient(host, port, callback); //初始化 RPC客户端
						client.call(request); // 发送 RPC请求道服务端
						return null;
					}
				});
	}
	
	public <T> T create(Class<?> interfaceClass) {
		return this.create(interfaceClass, null);
	}
	
}
