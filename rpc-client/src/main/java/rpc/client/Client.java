package rpc.client;

import rpc.common.RpcRequest;
import rpc.common.RpcResponse;

public interface Client {

	/**
	 * 发送请求
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public RpcResponse send(RpcRequest request) throws Exception;
	
}
