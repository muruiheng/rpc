package rpc.junit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import rpc.server.ExtRpcServer;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/spring-rpc.xml"})
public class RpcServerDemoStart {

	@SuppressWarnings("unused")
	@Autowired
	private ExtRpcServer rpcServer;

	/**
	 * @param rpcServer the rpcServer to set
	 */
	public void setRpcServer(ExtRpcServer rpcServer) {
		this.rpcServer = rpcServer;
	}
	
	@Test
	public void runRpcServer() {
		try {
			Thread.sleep(200000000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
