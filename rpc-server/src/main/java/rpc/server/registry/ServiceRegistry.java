package rpc.server.registry;

import java.util.Set;

/**
 * registry rpc service
 * @author mrh 
 */
public interface ServiceRegistry {

	/**
	 * 
	 * @param data 服务地址
	 * @param serviceName 服务名称
	 * @param dataPath 服务注册地址
	 */
	public void register(String data, Set<String> serviceName, String dataPath);

}
