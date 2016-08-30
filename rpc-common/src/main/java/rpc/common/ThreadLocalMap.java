package rpc.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


public class ThreadLocalMap {

	private static Logger LOGGER = Logger.getLogger(ThreadLocalMap.class);
	protected final static ThreadLocal<Map<String, Object>> threadContext = new MapThreadLocal();

	private ThreadLocalMap() {
	};

	public static void put(String key, Object value) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("put value of key " + key + " value = " + value);
		}
		getContextMap().put(key, value);
	}

	@SuppressWarnings("unchecked")
	public static <T> T remove(String key) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("remove value of key " + key + " value = " + getContextMap().get(key));
		}
		return (T) getContextMap().remove(key);
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(String key) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("get value of key " + key + " value = " + getContextMap().get(key));
		}
		return (T) getContextMap().get(key);
	}

	
	public static boolean containsKey(String key) {
		return getContextMap().containsKey(key);
	}
	private static class MapThreadLocal extends ThreadLocal<Map<String, Object>> {
		protected Map<String, Object> initialValue() {
			return new HashMap<String, Object>() {
				private static final long serialVersionUID = 3637958959138295593L;
				public Object put(String key, Object value) {
					if (LOGGER.isDebugEnabled()) {
						if (containsKey(key)) {
							LOGGER.debug("Overwritten attribute to thread context: " + key + " = " + value);
						} else {
							LOGGER.debug("Added attribute to thread context: " + key + " = " + value);
						}
					}
					return super.put(key, value);
				}
			};
		}
	}

	/**
	 * 取得thread context Map的实例。
	 * 
	 * @return thread context Map的实例
	 */
	protected static Map<String, Object> getContextMap() {
		return threadContext.get();
	}

	/**
	 * 清理线程所有被hold住的对象。以便重用！
	 */

	public static void reset() {
		getContextMap().clear();
	}

}
