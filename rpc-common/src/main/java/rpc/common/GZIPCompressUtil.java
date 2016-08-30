package rpc.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class GZIPCompressUtil {
	/**
	 * 压缩
	 * 
	 * @param data
	 *            byte[]
	 * @return
	 * @throws Exception
	 */
	public static byte[] compress(byte[] data) throws Exception {
		if (data == null || data.length == 0) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		try {
			gzip.write(data);
			gzip.close();
			data = out.toByteArray();
			out.close();
			return data;
		} finally {
			gzip.close();
			out.close();
		}

	}

	/**
	 * 解压缩
	 * 
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public static byte[] uncompress(byte[] data) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		GZIPInputStream gunzip = new GZIPInputStream(in);
		try {
			byte[] buffer = new byte[256];
			int n;
			while ((n = gunzip.read(buffer)) >= 0) {
				out.write(buffer, 0, n);
			}
			gunzip.close();
			in.close();
			data = out.toByteArray();
			out.close();
			return data;
		} finally {
			gunzip.close();
			in.close();
			out.close();
		}

	}
}
