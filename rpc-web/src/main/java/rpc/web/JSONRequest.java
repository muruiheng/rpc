package rpc.web;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.DefaultJSONParser.ResolveTask;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.FieldDeserializer;

/**
 * <pre>
 * 功能： 用于封装用户的请求，将请求转换为相应的对象
 * </pre>
 * @author mrh 2016年3月24日
 */
public class JSONRequest extends LinkedHashMap<String, Object>{
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = -9005858241290023408L;

	/**
	 * LOGGER - log4j
	 */
	private static final Logger LOGGER = Logger.getLogger(JSONRequest.class);
	
	
	/**
	 * 根据Class获取对应的VO对象
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> clazz, String voName) {
		if (!AssertUtil.isVal(this.values())) {
			LOGGER.debug("the val is null");
			return null;
		}
		Object obj =  this.get(voName);
		if (obj instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) this.get(voName);
			return this.getVOFormMap(clazz, map);
		} else {
			throw new RuntimeException(voName + "的目标值的数据类型不是" + clazz.getName() + "，请确认数据类型!");
		}
	}
	
	/**
	 * 根据Class获取对应的VO对象
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public <T> T get(Class<T> clazz) {
		if (!AssertUtil.isVal(this.values())) {
			LOGGER.debug("the val is null");
			return null;
		}
		Field[] fields = clazz.getDeclaredFields();
		if (!AssertUtil.isVal(fields)) {
			return null;
		}
		JSONObject json = new JSONObject();
		for (Field field : fields) {
			json.put(field.getName(), this.get(field.getName()));
		}
		return this.parseObject(json, clazz);
	}
	
	/**
	 * 根据Class获取对应的VO对象
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getList(Class<T> clazz, String listName) {
		if (!AssertUtil.isVal(this.values())) {
			LOGGER.debug("the val is null");
			return null;
		}
		List<Map<String, Object>> list = (List<Map<String, Object>>)this.get(listName);
		if (!AssertUtil.isVal(list)) {
			return null;
		}
		List<T> result = new ArrayList<T>();
		for (int index = 0; index < list.size(); index++) {
			result.add(this.getVOFormMap(clazz, list.get(index)));
		}
		return result;
	}
	
	/**
	 * 返回String类型的请求参数 
	 * @param parameter
	 * @return
	 */
	public String getString(String parameter) {
		if (!AssertUtil.isVal(this.values())) {
			return null;
		}
		Object obj = this.get(parameter);
		if (!AssertUtil.isVal(obj)) {
			return null;
		}
		if (!(obj instanceof String)) {
			throw new RuntimeException("目标值的数据类型不是String，请确认目标值的数据类型！");
		}
		return obj.toString(); 
	}
	
	/**
	 * 返回String类型的请求参数 
	 * @param parameter
	 * @return
	 */
	public int getInt(String parameter) {
		if (!AssertUtil.isVal(this.values())) {
			return 0;
		}
		Object obj = this.get(parameter);
		if (!AssertUtil.isVal(obj)) {
			return 0;
		}
		if (obj instanceof String) {
			return Integer.parseInt(obj.toString()); 
		}
		if (obj instanceof Integer) {
			return ((Integer)obj).intValue(); 
		} 
		return Integer.parseInt(obj.toString()); 
	}
	
	public Long getLong(String parameter) {
		if (!AssertUtil.isVal(this.values())) {
			return new Long(0);
		}
		Object obj = this.get(parameter);
		if (!AssertUtil.isVal(obj)) {
			return new Long(0);
		}
		if (obj instanceof String) {
			return Long.parseLong(obj.toString()); 
		}
		return Long.parseLong(obj.toString()); 
	}
	/**
	 * 获取前台的 BigDecimal 类型的参数
	 * @param parameter String
	 * @return BigDecimal
	 */
	public BigDecimal getBigDecimal(String parameter) {
		if (!AssertUtil.isVal(this.values())) {
			LOGGER.debug("the val is null");
			return null;
		}
		Object obj = this.get(parameter);
		if (!AssertUtil.isVal(obj)) {
			return null;
		}
		if (obj instanceof String) {
			return new BigDecimal(this.getString(parameter));
		}
		if (obj instanceof BigDecimal) {
			return (BigDecimal)obj;
		}
		throw new RuntimeException("目标值的数据类型不是BigDecimal，请确认目标值的数据类型！");
	}
	
	/**
	 * 获取前台的 Timestamp 类型的参数
	 * @param parameter
	 * @return
	 */
	public Timestamp getTimestamp(String parameter) {
		if (!AssertUtil.isVal(this.values())) {
			LOGGER.debug("the val is null");
			return null;
		}
		Object obj = this.get(parameter);
		if (!AssertUtil.isVal(obj)) {
			return null;
		}
		if (!AssertUtil.isVal(obj.toString())) {
			return null;
		}
		if (obj instanceof Timestamp) {
			return (Timestamp)obj;
		}
		throw new RuntimeException("目标值的数据类型不是Timestamp，请确认目标值的数据类型！");
	}
	
	/**
	 * 从Map集合中获取javaBean对象
	 * @param clazz Class
	 * @param map Map<String, Object> 
	 * @return T
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws Exception
	 */
	protected <T> T getVOFormMap(Class<T> clazz, Map<String, Object> map) {
		if (!AssertUtil.isVal(map)) {
			LOGGER.debug("the map is null");
			return null;
		}
		JSONObject json = new JSONObject(map);
		return this.parseObject(json, clazz);
	}
	
	/**
	 * 将json对象转换成javaBean
	 * @param json
	 * @param Class
	 * @return
	 */
	protected <T> T parseObject(JSONObject json, Class<T> clazz) {
		String input = json.toJSONString();
		if (input == null) {
			return null;
		}

		DefaultJSONParser parser = new DefaultJSONParser(input, ParserConfig.getGlobalInstance(), JSON.DEFAULT_PARSER_FEATURE);
		MixDateFormat dataFormat = new MixDateFormat();
		parser.setDateFomrat(dataFormat);
		
		T value = (T) parser.parseObject(clazz);
		handleResovleTask(parser, value);
		parser.close();
		return (T) value;
	}
	
	/**
	 * 根据配置信息将数据进行格式化
	 * @param parser
	 * @param value
	 */
	protected <T> void handleResovleTask(DefaultJSONParser parser, T value) {
		if (parser.isEnabled(Feature.DisableCircularReferenceDetect)) {
			return;
		}
		int size = parser.getResolveTaskList().size();
		for (int i = 0; i < size; ++i) {
			ResolveTask task = parser.getResolveTaskList().get(i);
			FieldDeserializer fieldDeser = task.getFieldDeserializer();

			Object object = null;
			if (task.getOwnerContext() != null) {
				object = task.getOwnerContext().getObject();
			}

			String ref = task.getReferenceValue();
			Object refValue;
			if (ref.startsWith("$")) {
				refValue = parser.getObject(ref);
			} else {
				refValue = task.getContext().getObject();
			}
			fieldDeser.setValue(object, refValue);
		}
	}
}
