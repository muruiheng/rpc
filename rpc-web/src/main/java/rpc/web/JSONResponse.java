package rpc.web;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <pre>
 * 返回结结果集 包含的信息(用于返回json的数据对象)：
 * 1、操作是否成功（默认为true）
 * 2、是否需要登陆（默认为false， 除特殊场景外不需要开发人员设定）
 * 3、返回结果集 Map
 * </pre>
 * @author mrh 2016年3月24日
 *
 */
public class JSONResponse {
	
	private boolean success = true;
	
	private boolean relogin = false;
	
	private Map<String, Object> values = new LinkedHashMap<String, Object>();;
	
	private String message;

	
	public JSONResponse() {
		super();
	}

	/**
	 * 构造方法-用于框架权限、资源验证不通过时，拒绝用户请求时使用
	 * @param relogin
	 */
	public JSONResponse(boolean relogin) {
		super();
		this.relogin = relogin;
	}

	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * @param success the success to set
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * @return the relogin
	 */
	public boolean isRelogin() {
		return relogin;
	}

	/**
	 * @return the value
	 */
	public Map<String, Object> getValues() {
		return values;
	}

	/**
	 * @param the value Object
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T)this.values.get(key);
	}
	
	/**
	 * @param value the value to set
	 */
	public void put(String key, Object value) {
		this.values.put(key, value);
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return JSONUtil.toString(this);
	}
	
}
