package rpc.junit;

import rpc.common.annotation.HttpService;

@HttpService(HttpServiceDemo.class)
public class HttpServiceDemoImpl implements HttpServiceDemo{

	@Override
	public String helloword(String name) {
		// TODO Auto-generated method stub
		String result = "hello "+ name;
		return result;
	}

}
