package rpc.junit.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import rpc.junit.HttpServiceDemo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/spring-rpc.xml"})
public class HttpClientDemo {

	@Autowired
	private HttpServiceDemo httpDemo;

	/**
	 * @param httpDemo the httpDemo to set
	 */
	public void setHttpDemo(HttpServiceDemo httpDemo) {
		this.httpDemo = httpDemo;
	}
	
	@Test
	public void testHttp() {
		this.httpDemo.helloword("HTTP");
	}
	
}
