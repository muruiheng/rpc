package rpc.common.junit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import rpc.common.GZIPCompressUtil;
import rpc.common.RpcRequest;
import rpc.common.SerializationUtil;

public class SerializationTest {

	@Test
	public void testCompress() {
		String uncompress = "UI设计需求 根据微电网系统界面设计，需要充分考虑到界面的美观、交互的友好性， 尽可能减少图片背景使用；重点设计系统交互相关的优化以及界面的整合涉及到的组件：导航栏、菜单、操作区域、树形、表格、表单、弹出窗口、按钮、输入组件、搜索组件、下拉列表";
		
		byte[] data = uncompress.getBytes();
		
		System.out.println(data.length);
		try {
			data = GZIPCompressUtil.compress(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(data.length);
		
		try {
			String compress = new String(GZIPCompressUtil.uncompress(data));
			System.out.println(uncompress);
			System.out.println(compress);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void uncompressSerialization() {
		RpcRequest request = new RpcRequest();
		request.setClassName(SerializationUtil.class.getName());
		request.setMethodName("getName");
		Class<?>[] clazzs = {Map.class, List.class};
		request.setParameterTypes(clazzs);
		Object[] parameters = {new HashMap<>(), new ArrayList<>()};
		request.setParameters(parameters);
		
		byte[] data = SerializationUtil.serialize(request);
		
		
		RpcRequest unCom_request = SerializationUtil.deserialize(data, RpcRequest.class);
		System.out.println(unCom_request.toString());
	}
}
