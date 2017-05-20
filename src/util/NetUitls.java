package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetUitls {

	/**
	 * 向指定 URL发送POST方法的请求，任何错误都将返回""
	 * 
	 * @param url
	 *            请求地址
	 * @param head
	 *            要发送的请求头（请构造"key1=value1&key2=value2"格式，无问号）
	 * @param body
	 *            要发送的内容（键值对的话请构造"key1=value1&key2=value2"格式，无问号）
	 * @return 请求的返回
	 */

	public static String sendPost(String url, String head, String body) {
		BufferedReader in = null;
		String result = "";
		try {
			// 初始化连接
			URL realUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) realUrl
					.openConnection();
			conn.setConnectTimeout(20000);// 连接超时20s
			conn.setReadTimeout(30000);// 读取超时30s
			// 发送POST请求必须设置的项目
			if (head != null && !head.trim().equals("")) {
				try {// 这里出错继续往下执行
					head = head.trim();
					String[] alls = head.split("&");
					for (int i = 0; i < alls.length; i++) {
						String[] key_value = alls[i].split("=");
						key_value[0] = (key_value[0] == null) ? ""
								: key_value[0];
						key_value[1] = (key_value[1] == null) ? ""
								: key_value[1];
						conn.setRequestProperty(key_value[0], key_value[1]);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.getOutputStream().write(body.getBytes("utf8"));
			// 读取响应
			int resp_code = conn.getResponseCode();
			System.out.println("POST-返回码：" + resp_code);
			try {
				in = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
			} catch (Exception e) {
				in = new BufferedReader(new InputStreamReader(
						conn.getErrorStream()));
			}
			StringBuffer sb = new StringBuffer("");
			String line = "";
			while ((line = in.readLine()) != null) {
				sb.append(line + "\n");
			}
			result = sb.toString();
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 关闭流
		finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 向指定 URL发送POST方法的请求，任何错误都将返回""
	 * 
	 * @param url
	 *            请求地址
	 * @param body
	 *            要发送的内容（键值对的话请构造"key1=value1&key2=value2"格式，无问号）
	 * @return 请求的返回
	 */
	public static String sendPost(String url, String body) {
		return sendPost(url, null, body);
	}

	/**
	 * 向指定 URL发送GET方法的请求，任何错误都将返回""
	 * 
	 * @param url
	 *            请求地址，请自行把url后的请求加到url上
	 * @return 请求的返回
	 */
	public static String sendGet(String url) {
		BufferedReader in = null;
		String result = "";
		try {
			// 初始化连接
			URL realUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) realUrl
					.openConnection();
			conn.setConnectTimeout(20000);// 连接超时20s
			conn.setReadTimeout(30000);// 读取超时30s
			// 读取响应
			int resp_code = conn.getResponseCode();
			System.out.println("GET-返回码：" + resp_code);
			try {
				in = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
			} catch (Exception e) {
				in = new BufferedReader(new InputStreamReader(
						conn.getErrorStream()));
			}
			StringBuffer sb = new StringBuffer("");
			String line = "";
			while ((line = in.readLine()) != null) {
				sb.append(line + "\n");
			}
			result = sb.toString();
			// System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 关闭流
		finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

}
