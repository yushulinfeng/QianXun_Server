package controller;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import util.MsgUtil;

import com.opensymphony.xwork2.ActionSupport;
import com.szdd.qianxun.alipay.AlipaySignUtils;

public class AlipayAction extends ActionSupport implements Serializable, SessionAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1226230017435058563L;

	private Map<String, Object> session;

	private InputStream inputStream;

	private String ali_sign;

	public String signature() {
		System.out.println("signature-ali_sign:" + ali_sign);
		String result = "-1";

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("getBalance-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		try {
			String signture = AlipaySignUtils.getAlipaySign(ali_sign);
			result = signture;
		} catch (Exception e) {
			e.printStackTrace();
			result = "-2";
		}

		System.out.println("signature-result:" + result);
		inputStream = MsgUtil.sendString(result);
		return SUCCESS;
	}

	/**********************************/

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public String getAli_sign() {
		return ali_sign;
	}

	public void setAli_sign(String ali_sign) {
		this.ali_sign = ali_sign;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public Map<String, Object> getSession() {
		return session;
	}

}
