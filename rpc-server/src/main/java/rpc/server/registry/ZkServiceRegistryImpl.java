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

/**
 * registry rpc service in zk 
 * @author mrh
 */
public class ZkServiceRegistryImpl implements ServiceRegistry{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ZkServiceRegistryImpl.class);

	private CountDownLatch latch = new CountDownLatch(1);

	private String registryAddress;

	public ZkServiceRegistryImpl(String registryAddress) {
		this.registryAddress = registryAddress;
	}

	public void register(String data, Set<String> serviceNames) {
		if (data != null) {
			ZooKeeper zk = connectServer();
			if (zk != null) {
				for (String serviceName : serviceNames) {
					StringBuilder serviceKey = new StringBuilder(data);
					serviceKey.append("#").append(serviceName);
					createNode(zk, serviceKey.toString());
				}
			}
		}
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
	private void createNode(ZooKeeper zk, String data) {
		try {
			byte[] bytes = data.getBytes();
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
			StringBuffer pathStr = new StringBuffer(Constant.ZK_DATA_PATH);
			if (key != null && key.length > 1) {
				pathStr.append("#").append(key[1]).append("#");
			}
			String path = zk.create(pathStr.toString(), bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE,
					CreateMode.EPHEMERAL_SEQUENTIAL);
			LOGGER.info("create zookeeper node ({} => {})", path, data);
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
