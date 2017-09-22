package rpc.junit;

import rpc.common.annotation.TcpService;

@TcpService(TcpServiceDemo.class)
public class TcpServiceDemoImpl implements TcpServiceDemo {

	@Override
	public String helloword(String name) {
		// TODO Auto-generated method stub
		String result = "hello "+ name;
		return result;
	}

}
