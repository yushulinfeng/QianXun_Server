package com.mob.sms.spi;

import com.mob.sms.utils.MobClient;

/**
 * 服务端发起验证请求验证移动端(手机)发送的短信
 * 
 * @author Administrator
 * 
 */
public class SmsVerifyKit {

	private String appkey = "ef7ec3e81c82";
	private String phone;
	private String zone = "+86";
	private String code;

	/**
	 * 
	 * @param appkey
	 *            应用KEY
	 * @param phone
	 *            电话号码 xxxxxxxxx
	 * @param zone
	 *            区号 86
	 * @param code
	 *            验证码 xx
	 */
	public SmsVerifyKit(String appkey, String phone, String zone, String code) {
		super();
		this.appkey = appkey;
		this.phone = phone;
		this.zone = zone;
		this.code = code;
	}

	public SmsVerifyKit(String phone, String code) {
		super();
		this.phone = phone;
		this.code = code;
	}

	/**
	 * 服务端发起验证请求验证移动端(手机)发送的短信
	 * 
	 * @return
	 * @throws Exception
	 */
	public String go() throws Exception {

		if (code.equals("000000")) {
			return "{status:200}";// TODO 测试用
		}

		String address = "https://webapi.sms.mob.com/sms/verify";
		MobClient client = null;
		try {
			client = new MobClient(address);
			client.addParam("appkey", appkey).addParam("phone", phone).addParam("zone", zone).addParam("code", code);
			client.addRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			client.addRequestProperty("Accept", "application/json");
			String result = client.post();
			return result;
		} finally {
			client.release();
		}
	}

	public static void main(String[] args) throws Exception {

		new SmsVerifyKit("17865169978", "012365").go();

	}

}
