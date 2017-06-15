package util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import com.alibaba.fastjson.JSONObject;

public class DiscussUtils {
	private static final String APP_KEY = "563ed6dc67e58ef259002fc6";
	private static final String TOPIC_ID = "574fc5b655c4007925471e81";
	private static final String BASEURL = "https://rest.wsq.umeng.com";
	private static final String URL_LOGIN = BASEURL + "/0/get_access_token";
	private static final String URL_POSTDISCUSS = BASEURL + "/0/feed/update";
	private static final long TIME_OUT = 20 * 60 * 1000;
	private static long LAST_TIME = 0;
	private static String ACCESS_TOKEN = null;
	private static DiscussUtils instance = new DiscussUtils();

	/** 构造器 */
	private DiscussUtils() {
	}

	/** 获取实例，仅允许内部获取 */
	private static DiscussUtils getInstance() {
		return instance;
	}

	/** 请求方式 */
	private enum HttpMethod {
		POST, GET, PUT, DELETE
	}

	/** 发送请求 */
	private String sentRequest(String fullUrl, HttpMethod httpMethod,
			Map<String, Object> data) {
		if (fullUrl == null) {
			return null;
		}
		String result = null;

		if (httpMethod == HttpMethod.GET || httpMethod == HttpMethod.DELETE) {
			fullUrl = fullUrl + buildParameter(ACCESS_TOKEN, APP_KEY, data);
			// System.out.println("Umeng rest url:" + fullUrl);
		} else {
			fullUrl = fullUrl + buildParameter(ACCESS_TOKEN, APP_KEY, null);
			// System.out.println("Umeng rest url:" + fullUrl);
		}
		HttpsURLConnection urlConnection;
		OutputStream outputStream;
		try {
			urlConnection = (HttpsURLConnection) new URL(fullUrl)
					.openConnection();
			switch (httpMethod) {
			case POST:
				urlConnection.setRequestMethod("POST");
				urlConnection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				urlConnection.setDoOutput(true);
				break;
			case PUT:
				urlConnection.setRequestMethod("PUT");
				urlConnection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				urlConnection.setDoOutput(true);
				break;
			case GET:
				urlConnection.setRequestMethod("GET");
				break;
			case DELETE:
				urlConnection.setRequestMethod("DELETE");
				break;
			default:
				urlConnection.setRequestMethod("GET");
				break;
			}
			if (HttpMethod.POST == httpMethod || HttpMethod.PUT == httpMethod) {
				outputStream = new DataOutputStream(
						urlConnection.getOutputStream());
				outputStream.write(buildParameter(null, null, data).getBytes(
						"UTF-8"));
				outputStream.flush();
				outputStream.close();
			}
			if (urlConnection.getResponseCode() == 200) {
				result = convertStreamToString(urlConnection.getInputStream());
			} else {
				System.out.println("Umeng Discuss:Response code:"
						+ urlConnection.getResponseCode() + " msg:"
						+ urlConnection.getResponseMessage());
			}
		} catch (MalformedURLException e) {
			System.out.println("Umeng Discuss:" + e.getMessage());
		} catch (IOException e) {
			System.out.println("Umeng Discuss:" + e.getMessage());
		}
		return result;
	}

	/** 构建参数 */
	private String buildParameter(String accessToken, String appKey,
			Map<String, Object> data) {
		StringBuilder sb = new StringBuilder();
		if (appKey != null && !appKey.equals("")) {
			sb.append("?ak=" + appKey);
		}
		if (accessToken != null && !accessToken.equals("")) {
			sb.append("&access_token=" + ACCESS_TOKEN);
		}
		if (data != null && !data.isEmpty()) {
			Set<String> keySet = data.keySet();
			for (String key : keySet) {
				Object value = data.get(key);
				if (value instanceof String) {
					sb.append("&" + key + "=" + value);
				} else if (value instanceof Integer) {
					sb.append("&" + key + "=" + value);
				}
			}
		}
		return sb.toString();
	}

	/** 管理员登录 */
	private boolean loginDiscuss() {
		// 构建请求
		HashMap<String, Object> objectHashMap = new HashMap<String, Object>();
		objectHashMap.put("source_uid", "90000000000");
		objectHashMap.put("source", "self_account");
		HashMap<String, Object> userInfoHashMap = new HashMap<String, Object>();
		userInfoHashMap.put("name", "系统通知");
		userInfoHashMap.put("gender", 1);
		userInfoHashMap.put("icon_url",
				"http://www.sylsylsyl.com/software/qianxun/ICON/icon.png");
		objectHashMap.put("user_info", userInfoHashMap);
		// 加密后的数据
		String MANAGER_USER_TEXT = "Ziho0loJ10i/R2e5e8BRiN+xdMUL/"
				+ "6vJwY0ECSHXwWclX631oBBT7FXCIQf455fhgjpO4WP/"
				+ "O3yXZ1P3FZbFMfATxiM9eAEEz7ifJE3PvTvW5kiXHP5Sc25/"
				+ "XnfCUIeT+3IeA0XLRZRispGfvN11VcMcQBd70g+D0/"
				+ "VBqlXq2MUFdH4M1KPjfQsUfmq01LeY9+"
				+ "Pf9A3C3CGBe8pw07UnEyITo7IhfK4KnA9HQYG1/ws=";
		// 登录
		String stringData = "";
		try {
			JSONObject jsonObject = new JSONObject(objectHashMap);
			stringData = jsonObject.toString();
			HashMap<String, Object> hashMap = new HashMap<String, Object>();
			hashMap.put("encrypted_data", MANAGER_USER_TEXT);
			stringData = sentRequest(URL_LOGIN, HttpMethod.POST, hashMap);
			// 存储ACCESS_TOKEN
			JSONObject jsonResp = JSONObject.parseObject(stringData);
			ACCESS_TOKEN = jsonResp.getString("access_token");
		} catch (Exception e) {
			System.out.println("Umeng access token request error:"
					+ e.getMessage());
			return false;
		}
		return true;
	}

	/** 将流转换为字符串 */
	private static String convertStreamToString(InputStream is)
			throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is),
				8192);
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			return null;
		} finally {
			if (is != null) {
				is.close();
			}
		}
		return sb.toString();
	}

	/**
	 * 后台发布评论
	 * 
	 * @param title
	 *            评论标题
	 * @param text
	 *            评论内容
	 * @return 评论ID
	 */
	public static String postDiscuss(String title, String text) {
		// 登录(超时，或无token，则登录)
		if (ACCESS_TOKEN == null
				|| System.currentTimeMillis() - LAST_TIME > TIME_OUT) {
			if (!getInstance().loginDiscuss()) {
				System.out.println("UM Discuss Login Error.");
				return null;
			}
			LAST_TIME = System.currentTimeMillis();
		}
		// 发布
		HashMap<String, Object> postData = new HashMap<String, Object>();
		postData.put("topic_ids", TOPIC_ID);
		postData.put("title", title);
		postData.put("content", text);
		String feedResult = getInstance().sentRequest(URL_POSTDISCUSS,
				HttpMethod.POST, postData);
		// 需要获取Id并返回这个Id
		// ////////测试打印
		// System.out.println(feedResult);
		try {
			JSONObject jsonResp = JSONObject.parseObject(feedResult);
			String discussId = jsonResp.getString("id");
			return discussId;
		} catch (Exception e) {
			System.out.println("json error:" + e.getMessage());
			return null;
		}
	}

	// // 测试方法
	// public static void main(String[] args) {
	// // System.out.println(postDiscuss("管理员测试", "管理员测试内容"));
	// // getInstance().loginDiscuss();
	// // System.out.println(ACCESS_TOKEN);
	// // System.out.println("END");
	// }

}
