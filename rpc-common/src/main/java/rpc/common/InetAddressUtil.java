package rpc.common;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * 网络IP工具类
 * @author mrh
 *
 */
public class InetAddressUtil {

	/**
	 * 获取本机物理IP地址
	 * @return
	 */
	public static String getIpAddress() {
		try {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
					continue;
				} else {
					Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
					while (addresses.hasMoreElements()) {
						ip = addresses.nextElement();
						if (ip != null && ip instanceof Inet4Address && !ip.toString().toUpperCase().contains("VIRBOX")) {
							return ip.getHostAddress();
						}
					}
				}
			}
		} catch (Exception e) {
			// _logger.error("IP地址获取失败", e);
			e.printStackTrace();
		}
		return "";
	}
}
