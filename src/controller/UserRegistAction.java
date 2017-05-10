package controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.struts2.interceptor.SessionAware;

import service.UserService;
import util.MsgUtil;
import util.SMSUtil;

import com.mob.sms.spi.SmsVerifyKit;
import com.opensymphony.xwork2.ActionSupport;

import domain.User;

public class UserRegistAction extends ActionSupport implements SessionAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1383916834959941834L;
	private Map<String, Object> session;
	private UserService userService;
	private User user;
	private InputStream inputStream;
	private long username;

	public String checkPNumber() {
		String result = "failed";
		System.out.println("checkPNumber-username:" + username);
		boolean flag = userService.hasPhoneNumber(username);
		if (flag) {
			result = "-1";// 已注册
		} else {
			result = "1";// 没有
		}
		System.out.println("checkPNumber-result:" + result);
		inputStream = MsgUtil.sendString(result);
		return SUCCESS;
	}

	public String getRegistCheckCode() {
		Map<String, Object> infoMap = userService.getRegistCheckCode(username);
		int status = (int) infoMap.get("status");
		if (status == 1) {
			String checkCode = (String) infoMap.get("checkCode");
			session.put("registCheckCode", checkCode);
		}
		this.inputStream = new ByteArrayInputStream((status + "").getBytes());
		return SUCCESS;
	}

	public String regist() {
		System.out.println("regist:");
		String result = "-1";
		if (user != null) {
			String code = user.getRegistCheckCode();
			String username = user.getUsername() + "";
			int status = -1;
			try {
				String resp = new SmsVerifyKit(username, code).go();
				status = JSONObject.fromObject(resp).getInt("status");
				if (status == 200) {
					user.setRank_credit(50);// 加积分
					int flag = userService.regist(user);
					if (flag == 1 || flag == -1) {
						result = flag + "";
					}
				} else {
					result = "-3";// 验证码错误
				}
			} catch (Exception e) {
				e.printStackTrace();
				result = "-2";
			}
		}
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;
	}

	public String getMessageImageVerifyForWeb() {
		String result = "-1";
		String msg = SMSUtil.getImageVerifyCode();
		if (!result.equals("")) {
			JSONObject json = JSONObject.fromObject(msg);
			if (json.getInt("code") == 200) {
				String imagePath = json.getString("url");
				String verifyCode = json.getString("verifyId");
				session.put("verifyId", verifyCode);
				result = imagePath;
			}

		}

		System.out.println("getMessageImageVerify-result:" + result);
		inputStream = MsgUtil.sendString(result);
		return SUCCESS;

	}

	public String sendMessageForWeb() {
		String result = "-1";
		System.out.println("sendMessageForWeb-phone:" + user.getUsername());
		System.out.println("sendMessageForWeb-phone:" + user.getRegistCheckCode());

		if (session != null) {
			String verifyId = (String) session.get("verifyId");
			System.out.println("verifyId:" + verifyId);
			String sessionId = SMSUtil.sendSMS(user.getUsername() + "", verifyId, user.getRegistCheckCode());

			if (session.containsKey("verifyId")) {
				session.remove("verifyId");
				session.put("sessionId", sessionId);
			}
			result = "1";
		}

		System.out.println("getMessageImageVerify-result:" + result);
		inputStream = MsgUtil.sendString(result);
		return SUCCESS;

	}

	public String registForWeb() {
		System.out.println("regist:");
		String result = "-1";

		if (session == null) {
			result = "-4";// 身份无法识别
			System.out.println("registForWeb-result" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		if (user != null) {
			try {
				String sessionId = (String) session.get("sessionId");
				boolean isSucceed = SMSUtil.checkSMS(sessionId, user.getRegistCheckCode());
				if (isSucceed) {
					user.setRank_credit(50);// 加积分
					int flag = userService.regist(user);
					if (flag == 1 || flag == -1) {
						result = flag + "";
					}
				} else {
					result = "-3";// 验证码错误
				}
			} catch (Exception e) {
				e.printStackTrace();
				result = "-2";
			}
		}

		System.out.println("registForWeb-result" + result);
		inputStream = MsgUtil.sendString(result);
		return SUCCESS;

	}

	/*****************************/
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public InputStream getInputStream() {
		return this.inputStream;
	}

	public long getUsername() {
		return username;
	}

	public void setUsername(long username) {
		this.username = username;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	public UserService getUserService() {
		return userService;
	}

	public User getUser() {
		return user;
	}
}
