package rpc.junit;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

public class IpAddressTest {

	
	@Test
	public void testAdd() {
		try {
			InetAddress ia = InetAddress.getLocalHost();
			System.out.println(ia.getHostAddress());
			System.out.println(ia.getAddress());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
