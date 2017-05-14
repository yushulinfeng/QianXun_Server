package util;

import org.apache.commons.codec.digest.DigestUtils;

import com.alibaba.fastjson.JSONObject;

//使用网络同步
public class SMSUtil {
	private static final String APP_KEY = "c9kqb3rdkgtnj";
	private static final String APP_SECRET = "x5S6RC8uNwQ";
	private static final String SMS_ID = "fEtapccdA2O8UBEbOioIjI";
	private static final String URL_SEND = "http://api.sms.ronghub.com/sendCode.json";
	private static final String URL_CHECK = "http://api.sms.ronghub.com/verifyCode.json";
	private static final String URL_IMAGE = "http://api.sms.ronghub.com/getImgCode.json?appKey=" + APP_KEY;

	/** 获取时间戳 */
	private static String getTimestamp() {
		return System.currentTimeMillis() + "";
	}

	/** 获取随机数（无长度限制） */
	private static String getNonce() {
		return (long) (Math.random() * 1000000L + 100) + "";// 确保非零
	}

	/** 计算签名 */
	private static String getSign(String raw_str) {
		String sign = new String(DigestUtils.shaHex(raw_str));
		return sign;
	}

	/** 获取请求头信息 */
	private static String getHeader() {
		String head = "";
		String nonce = getNonce();
		String time = getTimestamp();
		String raw_str = APP_SECRET + nonce + time;
		head += "App-Key=" + APP_KEY;
		head += "&Nonce=" + nonce;
		head += "&Timestamp=" + time;
		head += "&Signature=" + getSign(raw_str);
		return head;
	}

	/**
	 * 获取图片验证码，仅用于防止短信请求攻击（需要后台开启）
	 * 
	 * @return {"code": 200,"url": "xxxxxxxxxxxxx","verifyId": "xxxxxxxx" }
	 */
	public static String getImageVerifyCode() {
		return NetUitls.sendGet(URL_IMAGE);
	}

	/** 获取请求Body信息1 */
	private static String getBody(String phone, String imgId, String imgCode) {
		String body = "";
		body += "mobile=" + phone;// 接收短信验证码的目标手机号
		body += "&templateId=" + SMS_ID;// 短信模板 Id
		body += "&region=" + "86";// 手机号码所属国家区号，目前只支持中图区号 86
		if (imgId != null)
			body += "&verifyId=" + imgId;// 图片验证标识 Id
		if (imgCode != null)
			body += "&verifyCode=" + imgCode;// 图片验证码
		return body;
	}

	/** 获取请求Body信息2 */
	private static String getBody(String phone) {
		return getBody(phone, null, null);
	}

	/** 获取请求CheckBody信息 */
	private static String getCheckBody(String id, String code) {
		String body = "";
		body += "sessionId=" + id;// 短信验证码唯一标识，在发送短信验证码方法，返回值中获取。
		body += "&code=" + code;// 短信验证码内容。
		return body;
	}

	// ///////////////////////////////////////////////

	/**
	 * 发送短息验证码
	 * 
	 * @param phone
	 *            手机号
	 * @param imgId
	 *            图片验证码ID
	 * @param imgCode
	 *            图片验证码
	 * @return 结果： sessionId
	 */
	public static String sendSMS(String phone, String imgId, String imgCode) {
		String head = getHeader();
		String body = getBody(phone, imgId, imgCode);
		String response = NetUitls.sendPost(URL_SEND, head, body);
		try {
			JSONObject json = JSONObject.parseObject(response);
			if (json.getInteger("code") == 200) {
				String sessionId = json.getString("sessionId");
				if (sessionId == null || sessionId.trim().equals(""))
					return null;
				return sessionId;
			}
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 发送短息验证码
	 * 
	 * @param phone
	 *            手机号
	 * @return 结果： sessionId
	 */
	public static String sendSMS(String phone) {
		return sendSMS(phone, null, null);
	}

	/**
	 * 校验短信验证码（一个验证码成功校验一次后就会失效）
	 * 
	 * @param sessionId
	 *            短信验证码唯一标识
	 * @param code
	 *            短信验证码内容
	 * @return true 成功
	 */
	public static boolean checkSMS(String sessionId, String code) {
		String head = getHeader();
		String body = getCheckBody(sessionId, code);
		String response = NetUitls.sendPost(URL_CHECK, head, body);
		try {
			JSONObject json = JSONObject.parseObject(response);
			if (json.getInteger("code") == 200 && json.getBooleanValue("success") == true)
				return true;
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * code 描述 HTTP 状态码<br/>
	 * 1009 没有开启图验功能 400<br/>
	 * 1010 未使用已开启的图验功能 400<br/>
	 * 1000 内部逻辑错误 500<br/>
	 * 1003 参数错误 400<br/>
	 * 1005 参数长度超出限制 400<br/>
	 * 1008 调用超过频率上限 429<br/>
	 * 1002 缺少参数 400<br/>
	 * 1004 验证签名错误 401<br/>
	 * 1050 内部服务响应超时 504<br/>
	 * 1012 图片验证码不正确 430<br/>
	 * 1011 剩余条数不足，需要充值 430<br/>
	 * 1013 短信通道不可用 430<br/>
	 * 1014 短信验证码不正确 430<br/>
	 * 1015 短信验证码过期无效 430<br/>
	 */
	public static void main(String[] args) {
		// System.out.println(sendSMS("17865169753"));
		System.out.println(checkSMS("44JhNCJ9Qfh8U9N-evcA8e", "960337"));
	}

}
