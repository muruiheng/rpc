package rpc.server.registry;

import java.util.Set;

/**
 * registry rpc service
 * @author mrh 
 */
public interface ServiceRegistry {

	public void register(String data, Set<String> serviceName);

}
