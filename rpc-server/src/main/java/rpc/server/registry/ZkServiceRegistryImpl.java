package rpc.server.registry;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rpc.common.Constant;
import rpc.common.RpcProtocol;

/**
 * registry rpc service in zk 
 * @author mrh
 */
public class ZkServiceRegistryImpl implements ServiceRegistry{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ZkServiceRegistryImpl.class);

	private CountDownLatch latch = new CountDownLatch(1);

	private String registryAddress;
	
	 private String data;
	 private Set<String> serviceNames;
	 private String dataPath;

	public ZkServiceRegistryImpl(String registryAddress) {
		this.registryAddress = registryAddress;
	}

	public void register(String data, Set<String> serviceNames, String dataPath) {
		this.data = data;
		this.serviceNames = serviceNames;
		this.dataPath = dataPath;
		if (data != null) {
			
			ZooKeeper zk = connectServer();
			if (zk != null) {
				for (String serviceName : serviceNames) {
					StringBuilder serviceKey = new StringBuilder(data);
					serviceKey.append("#").append(serviceName);
					createNode(zk, serviceKey.toString(), dataPath);
				}
			}
			this.watchNode(zk, data, serviceNames, dataPath);
		}
	}
	
	/**
	 * 监听连接状态
	 * @param zk
	 * @param data
	 * @param serviceNames
	 * @param dataPath
	 */
	private void watchNode(final ZooKeeper zk, final String data, final Set<String> serviceNames, final String dataPath) {
        zk.register(new Watcher() {
        	 private String local_data = data;
        	 private Set<String> local_serviceNames = serviceNames;
        	 private String local_dataPath = dataPath;
        	 
            @Override
            public void process(WatchedEvent event) {
                if (event.getState() == Event.KeeperState.Disconnected) {
                	register(local_data, local_serviceNames, local_dataPath);
                }
            }
        });
    }

	/**
	 * to connect zookeeper server and countDown all process to wait on;
	 * @return
	 */
	private ZooKeeper connectServer() {
		ZooKeeper zk = null;
		try {
			LOGGER.info("connectServer....");
			zk = new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {
				@Override
				public void process(WatchedEvent event) {
					if (event.getState() == Event.KeeperState.SyncConnected) {
						LOGGER.info("connectServer....connected!");
						latch.countDown();
					}
				}
			});
			latch.await();
			zk.register(new Watcher(){
				@Override
				public void process(WatchedEvent event) {
					if (event.getType() == Event.EventType.NodeDeleted) {
						LOGGER.info("connectServer....Disconnected! try to connect");
						register(data, serviceNames, dataPath);
					}
				}
				
			});
		} catch (IOException e){
			LOGGER.error("", e);
		} catch (InterruptedException e) {
			LOGGER.error("", e);
		}
		return zk;
	}
	

	/**
	 * 
	 * @param zk
	 * @param data
	 */
	private void createNode(ZooKeeper zk, String data, String dataPth) {
		try {
			try {
				Stat exists = zk.exists(Constant.ZK_REGISTRY_PATH, false);
				if (exists == null) {
					zk.create(Constant.ZK_ROOT_PATH, Constant.ZK_ROOT_PATH.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
					zk.create(Constant.ZK_REGISTRY_PATH, Constant.ZK_REGISTRY_PATH.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				}
				LOGGER.info("------------" + exists);
			} catch (KeeperException e) {
				LOGGER.error("createNode failed", e);
			} catch (InterruptedException e) {
				LOGGER.error("createNode failed", e);
			}
			
			String[] key = data.split("#");
			StringBuffer pathStr = new StringBuffer(dataPth);
			if (key != null && key.length > 1) {
				pathStr.append("#").append(key[1]).append("#");
			}
			byte[] bytes = null;
			StringBuffer dataBuf = new StringBuffer(data);
			if (Constant.ZK_DATA_PATH.equals(dataPth)) {
				dataBuf.append("#").append(RpcProtocol.TCP.name());
				
			}
			if (Constant.ZK_HTTP_PATH.equals(dataPth)) {
				dataBuf.append("#").append(RpcProtocol.HTTP.name());
				
			}
			bytes = dataBuf.toString().getBytes();
			String path = zk.create(pathStr.toString(), bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE,
					CreateMode.EPHEMERAL_SEQUENTIAL);
			LOGGER.info("create zookeeper node ({} => {})", path, dataBuf.toString());
		} catch (KeeperException e) {
			LOGGER.error("createNode failed", e);
			if(e.code() == Code.NONODE) {
				LOGGER.error("createNode failed---------------");
			}
		} catch (InterruptedException e) {
			LOGGER.error("createNode failed", e);
		}
	}

}
