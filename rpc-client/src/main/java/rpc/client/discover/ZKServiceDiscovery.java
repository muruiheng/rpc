package rpc.client.discover;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.util.internal.ThreadLocalRandom;
import rpc.common.Constant;

public class ZKServiceDiscovery {

	private static final Logger LOGGER = LoggerFactory.getLogger(ZKServiceDiscovery.class);

    private CountDownLatch latch = new CountDownLatch(1);

    private volatile Map<String,List<String>> serviceConfig = new ConcurrentHashMap<String, List<String>>();

    private String registryAddress;

    public ZKServiceDiscovery(String registryAddress) {
        this.registryAddress = registryAddress;

        ZooKeeper zk = connectServer();
        if (zk != null) {
            watchNode(zk);
        }
    }

    public String discover(String serviceName) {
        String serverNode = null;
        List<String> dataList = serviceConfig.get(serviceName);
        if (dataList == null) {
        	return serverNode;
        }
        
        int size = dataList.size();
        if (size == 1) {
            serverNode = dataList.get(0);
            LOGGER.debug("discover only serverNode: {}", serverNode);
        } else if (size > 1) {
            serverNode = dataList.get(ThreadLocalRandom.current().nextInt(size));
            LOGGER.debug("discover random serverNode: {}", serverNode);
        }
        return serverNode;
    }

    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        latch.countDown();
                    }
                }
            });
            latch.await();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("", e);
        }
        return zk;
    }

    private void watchNode(final ZooKeeper zk) {
        try {
            List<String> nodeList = zk.getChildren(Constant.ZK_REGISTRY_PATH, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getType() == Event.EventType.NodeChildrenChanged) {
                        watchNode(zk);
                    }
                }
            });
            this.serviceConfig.clear();
            for (String node : nodeList) {
                byte[] bytes = zk.getData(Constant.ZK_REGISTRY_PATH + "/" + node, false, null);
                String key =new String(bytes);
                addServices(key);
            }
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error("", e);
        }
    }
    
     
    private void addServices(String key) {
    	String[] keys = key.split("#");
    	String serviceName = keys[1];
    	String address = keys[0];
    	if (keys.length > 2)
    		address = address + ":" + keys[2];
    	List<String> dataList = serviceConfig.get(serviceName);
    	if (dataList == null) {
    		dataList = new ArrayList<String>();
    		serviceConfig.put(serviceName, dataList);
    	}
    	
    	dataList.add(address);
    }

}
