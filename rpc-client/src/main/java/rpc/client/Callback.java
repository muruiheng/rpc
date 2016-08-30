package rpc.client;

import rpc.common.RpcResponse;

/**
 * 回调接口
 * @author mrh
 *
 */
public interface Callback {

	public void doResult(RpcResponse response);
}
