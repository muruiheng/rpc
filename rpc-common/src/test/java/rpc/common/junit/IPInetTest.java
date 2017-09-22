package rpc.common.junit;

import org.junit.Test;

import rpc.common.InetAddressUtil;

public class IPInetTest {

	@Test
	public void testIP() {
		System.out.println(InetAddressUtil.getIpAddress());
	}
}
