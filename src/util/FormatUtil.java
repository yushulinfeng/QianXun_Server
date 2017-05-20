package util;

import net.sf.json.JSONObject;

public class FormatUtil {

	/**
	 * 格式化需要推送的数据,type是0-9的推送
	 * 
	 * @param userId
	 * @param reqId
	 * @param context
	 * @return
	 */
	public static String pushJsonFormat(long username, int reqId, String context) {
		JSONObject js = new JSONObject();
		js.put("userId", username);
		js.put("reqId", reqId);
		js.put("context", context);

		return js.toString();
	}
}
