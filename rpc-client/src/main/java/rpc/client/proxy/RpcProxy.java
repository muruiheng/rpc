package rpc.client.proxy;

import java.lang.reflect.Method;
import java.rmi.UnknownHostException;
import java.util.UUID;

import org.apache.log4j.Logger;

import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.Proxy;
import rpc.client.Client;
import rpc.client.HttpClient;
import rpc.client.RpcClient;
import rpc.client.discover.ZKServiceDiscovery;
import rpc.common.RpcProtocol;
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
						if (serverAddress == null) 
							throw new UnknownHostException("没有系统服务，请稍后重试！" + request.getClassName());
						String[] array = serverAddress.split(":");
						String host = array[0];
						int port = Integer.parseInt(array[1]);
						Client client = null;
						//初始化 RPC客户端
						if (array.length > 2 && RpcProtocol.HTTP == RpcProtocol.valueOf(array[2])) {
							client = new HttpClient(host, port);
						} else if (array.length > 2 && RpcProtocol.TCP == RpcProtocol.valueOf(array[2])){
							client = new RpcClient(host, port);
						} else {
							client = new RpcClient(host, port);
						}
						RpcResponse response = client.send(request); // 发送 RPC请求道服务端
						client = null;
						if (logger.isDebugEnabled())
							logger.debug("RpcProxy....rpc ended!");
						if (response.isError()) {
							if (logger.isDebugEnabled())
								logger.debug("RpcProxy remote server process failed!", response.getError());
							throw response.getError();
						} else {
							return response.getResult();
						}
					}
				});
	}
	
	/**
	 * 
	 * @param interfaceClass
	 * @param serviceName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T create(Class<?> interfaceClass, final String serviceName) {
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass },
				new InvocationHandler() {
					private String remoteServiceName = serviceName;
					
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						if (logger.isDebugEnabled())
							logger.debug("RpcProxy....to send rpc request!");
						RpcRequest request = new RpcRequest(); // 初始化 RPC 请求报错
						request.setRequestId(UUID.randomUUID().toString());
						request.setClassName(remoteServiceName);
						request.setMethodName(method.getName());
						request.setParameterTypes(method.getParameterTypes());
						request.setParameters(args);

						if (serviceDiscovery != null) {
							serverAddress = serviceDiscovery.discover(request.getClassName()); // 发现服务
						}
						if (serverAddress == null) 
							throw new UnknownHostException("没有系统服务，请稍后重试！" + request.getClassName());
						String[] array = serverAddress.split(":");
						String host = array[0];
						int port = Integer.parseInt(array[1]);

						RpcClient client = new RpcClient(host, port); //初始化 RPC客户端
						RpcResponse response = client.send(request); // 发送 RPC请求道服务端
						if (logger.isDebugEnabled())
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
