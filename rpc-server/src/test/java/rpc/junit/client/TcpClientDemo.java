package rpc.junit.client;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import rpc.junit.TcpServiceDemo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/spring-rpc.xml"})
public class TcpClientDemo {
	
	@Autowired
	private TcpServiceDemo tcpDemo;

	/**
	 * @param tcpDemo the tcpDemo to set
	 */
	public void setTcpDemo(TcpServiceDemo tcpDemo) {
		this.tcpDemo = tcpDemo;
	}
	
	public void testTcp() {
		this.tcpDemo.helloword("TCP");
	}
}
