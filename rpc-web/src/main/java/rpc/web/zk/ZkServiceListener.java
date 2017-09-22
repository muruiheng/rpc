package rpc.web.zk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import rpc.common.Constant;
import rpc.web.AssertUtil;

public class ZkServiceListener implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(ZkServiceListener.class);

	private CountDownLatch latch = new CountDownLatch(1);

	private volatile JSONArray servicesList = new JSONArray();
	private volatile JSONArray nodesList = new JSONArray();
	private volatile Map<String, JSONArray> servicesMapList = new HashMap<String, JSONArray>();
	private volatile Map<String, JSONArray> nodesMapList = new HashMap<String, JSONArray>();

	private String registryAddress;
	
	private String dataId;

	private static final String node_list_key = "node_list.json";
	private static final String services_list_key = "services_list.json";
	
	private static final String node_name_key = "node";
	private static final String service_name_key = "service";
	private File dir;
	public void setRegistryAddress(String registryAddress) {
		this.registryAddress = registryAddress;
	}


	public void setDataId(String dataId) {
		this.dataId = dataId;
	}


	/**
	 * 连接Zookeeper
	 * @return
	 */
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

	/**
	 * 监听节点
	 * @param zk
	 * @throws IOException 
	 */
	private void watchNode(final ZooKeeper zk) throws IOException {
		try {
			List<String> nodeList = zk.getChildren(this.dataId == null? Constant.ZK_REGISTRY_PATH : this.dataId, new Watcher() {
				@Override
				public void process(WatchedEvent event) {
					if (event.getType() == Event.EventType.NodeChildrenChanged) {
						try {
							watchNode(zk);
						} catch (IOException e) {
							LOGGER.error("", e);
						}
					}
				}
			});

			
			this.clearFiles();
			for (String node : nodeList) {
				byte[] bytes = zk.getData(Constant.ZK_REGISTRY_PATH + "/" + node, false, null);
				String key = new String(bytes);
				addServices(key);
			}

			this.outputListFile(services_list_key, "services", this.servicesList);
			this.outputListFile(node_list_key, "nodes", this.nodesList);
			this.outputMapingFile();
			this.clearService();
		} catch (KeeperException | InterruptedException e) {
			LOGGER.error("", e);
		}
	}

	/**
	 * 清空映射对象
	 */
	private void clearService() {

		this.nodesList.clear();
		this.nodesMapList.clear();

		this.servicesList.clear();
		this.servicesMapList.clear();
	}
	
	/**
	 * 清空映射对象
	 */
	private void clearFiles() {
		for (File file : this.dir.listFiles()) {
			file.delete();
		}
	}

	/**
	 * 添加服务到映射对象中
	 * 
	 * @param key
	 */
	private void addServices(String key) {
		String[] keys = key.split("#");
		String serviceName = keys[1];
		String address = keys[0].replace(":", "_");
		if (keys.length > 2) {
			address = address + "(" + keys[2] + ")";
		} else {
			address = address + "(TCP)";
		}
		JSONObject nodeJSON = getJSON(node_name_key, address);
		if (!this.nodesList.contains(nodeJSON)) {
			this.nodesList.add(nodeJSON);
		}
		JSONObject serviceJSON = getJSON(service_name_key, serviceName);
		if (!this.servicesList.contains(serviceJSON)) {
			this.servicesList.add(serviceJSON);
		}

		// 添加service 与 服务器节点的映射
		JSONArray nodeList = this.servicesMapList.get(serviceName);
		if (!AssertUtil.isVal(nodeList)) {
			nodeList = new JSONArray();
			this.servicesMapList.put(serviceName, nodeList);
		}
		nodeList.add(getJSON(node_name_key, address));

		// 添加节点与服务器的映射
		JSONArray serviceList = this.nodesMapList.get(address);
		if (!AssertUtil.isVal(serviceList)) {
			serviceList = new JSONArray();
			this.nodesMapList.put(address, serviceList);
		}
		serviceList.add(getJSON(service_name_key, serviceName));
	}
	
	/**
	 * 返回json 对象
	 * @param name
	 * @param value
	 * @return
	 */
	private JSONObject getJSON (String name, Object value) {
		JSONObject json = new JSONObject();
		json.put(name, value);
		return json;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		URL absPath = this.getClass().getClassLoader().getResource("");
		this.dir = new File(absPath.getPath() + ".." + File.separator + "data");
		if (!this.dir.exists()) {
			this.dir.mkdir();
		}
		ZooKeeper zk = connectServer();
		if (zk != null) {
			watchNode(zk);
		}
	}

	/**
	 * 输出服务列表到文件中
	 * @throws IOException 
	 */
	private void outputListFile(String filename, String key, Object data) throws IOException {
	
		File servlistFile = new File( dir.getPath() + File.separator +filename);

		if (!servlistFile.exists()) {
			servlistFile.createNewFile();
		}
		
		this.outputFile(getJSON(key, data), servlistFile);
	}

	/**
	 * 输出映射文件
	 * @throws IOException
	 */
	private void outputMapingFile() throws IOException {
		for (Object node : nodesList) {
			JSONObject json = (JSONObject) node;
			JSONArray servicelist = this.nodesMapList.get(json.getString(node_name_key));
			this.outputListFile(json.getString(node_name_key)+"_" + services_list_key, "services", servicelist);
		}
		
		for (Object service : servicesList) {
			JSONObject json = (JSONObject) service;
			JSONArray nodeList = this.servicesMapList.get(json.getString(service_name_key));
			this.outputListFile(json.getString(service_name_key)+"_" + node_list_key, "nodes", nodeList);
		}
	}
	/**
	 * 输出文件
	 * 
	 * @param json
	 * @param file
	 */
	private void outputFile(JSONObject json, File file) {
		OutputStreamWriter  writer = null;
		try {
			writer =  new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			writer.write(json.toJSONString());
			writer.flush();
		} catch (IOException e) {
			LOGGER.error("write Data to Disk failed!", e);
		} finally {
			try {
				if (AssertUtil.isVal(writer))
					writer.close();
			} catch (IOException e) {
				LOGGER.warn("FileWriter close failed!", e);
			}
		}
	}

}
