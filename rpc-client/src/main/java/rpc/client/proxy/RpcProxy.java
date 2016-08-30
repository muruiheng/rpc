package rpc.client.proxy;

import java.lang.reflect.Method;
import java.rmi.UnknownHostException;
import java.util.UUID;

import org.apache.log4j.Logger;

import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.Proxy;
import rpc.client.RpcClient;
import rpc.client.discover.ZKServiceDiscovery;
import rpc.common.RpcRequest;
import rpc.common.RpcResponse;

/**
 * RPC代理
 * @author mrh
 *
 */
public class RpcProxy {

	private static final Logger logger = Logger.getLogger(RpcProxy.class);
	private String serverAddress;
	private ZKServiceDiscovery serviceDiscovery;

	
	public RpcProxy(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public RpcProxy(ZKServiceDiscovery serviceDiscovery) {
		this.serviceDiscovery = serviceDiscovery;
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Class<?> interfaceClass) {
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass },
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						logger.info("RpcProxy....to send rpc request!");
						RpcRequest request = new RpcRequest(); // 初始化 RPC 请求报错
						request.setRequestId(UUID.randomUUID().toString());
						request.setClassName(method.getDeclaringClass().getName());
						request.setMethodName(method.getName());
						request.setParameterTypes(method.getParameterTypes());
						request.setParameters(args);

						if (serviceDiscovery != null) {
							serverAddress = serviceDiscovery.discover(request.getClassName()); // 发现服务
						}
						if (serverAddress == null) 
							throw new UnknownHostException("没有系统服务，请稍后重试！");
						String[] array = serverAddress.split(":");
						String host = array[0];
						int port = Integer.parseInt(array[1]);

						RpcClient client = new RpcClient(host, port); //初始化 RPC客户端
						RpcResponse response = client.send(request); // 发送 RPC请求道服务端
						logger.info("RpcProxy....rpc ended!");
						if (response.isError()) {
							throw response.getError();
						} else {
							return response.getResult();
						}
					}
				});
	}

}
