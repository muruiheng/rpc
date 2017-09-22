package rpc.common;


public class Constant {

	public static int ZK_SESSION_TIMEOUT = 5000;
	
	/**
	 * http协议
	 */
	public static final String protocol_http = "http";
	
	/**
	 * tcp协议
	 */
	public static final String protocol_tcp = "tcp";
	public static String ZK_ROOT_PATH = "/rpc";
	
	/**
	 * 
	 */
	public static String ZK_REGISTRY_PATH = "/rpc/registry";
    
	/**
	 * TCP协议接口目录
	 */
	public static String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/services";
	
	/**
	 * TCP协议接口目录
	 */
	public static String ZK_TCP_PATH = ZK_REGISTRY_PATH + "/tcp";
	
	/**
	 * http 协议接口目录
	 */
	public static String ZK_HTTP_PATH = ZK_REGISTRY_PATH + "/http";
	
	/**
	 * http 协议接口目录
	 */
	public static String ZK_RMI_PATH = ZK_REGISTRY_PATH + "/rmi";
	
	
}
