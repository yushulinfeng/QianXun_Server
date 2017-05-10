package controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.struts2.interceptor.SessionAware;

import service.UserService;
import util.BaiChuanUtils;
import util.MsgUtil;

import com.mob.sms.spi.SmsVerifyKit;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import domain.User;

public class UserLoginAction extends ActionSupport implements SessionAware {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7952763708806602932L;

	private long username;
	private String password;
	private UserService userService;
	private Map<String, Object> session;
	private InputStream inputStream;
	private String appSecret;
	private String checkCode;
	private User user;

	public String login() {
		System.out.println("login:" + username + "-" + password);
		String result = "failed";
		try {
			Map<String, Object> infoMap = userService.login(username, password);
			int status = (int) infoMap.get("status");
			if (status == 1) {
				JSONObject js = new JSONObject();
				js.put("userId", infoMap.get("userId"));
				long userId = (int) infoMap.get("userId");
				String pass = BaiChuanUtils.getUserPass(userId);
				js.put("password", pass);
				js.put("timestamp", System.currentTimeMillis());

				User user = (User) infoMap.get("user");
				session.put("user", user);
				session.put("userId", user.getId());
				session.put("name", user.getNickName());
				Map<String, Object> sessionMap = ActionContext.getContext().getSession();
				if (!sessionMap.containsKey("user")) {
					sessionMap.put("user", user);
				}
				result = js.toString();
			} else {
				result = status + "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = "-2";
		}
		System.out.println("login-result:" + result);
		this.inputStream = new ByteArrayInputStream(result.getBytes());
		return SUCCESS;
	}

	public String loginByCheckCode() {
		String result = "failed";
		try {
			String resp = new SmsVerifyKit(username + "", checkCode).go();
			int status = JSONObject.fromObject(resp).getInt("status");
			if (status == 200) {
				User user = (User) userService.findByUsername(username);
				JSONObject js = new JSONObject();
				js.put("userId", user.getId());
				String pass = BaiChuanUtils.getUserPass(user.getId());
				js.put("password", pass);
				js.put("timestamp", System.currentTimeMillis());

				session.put("user", user);
				result = js.toString();
			} else {
				result = "-1";
			}
		} catch (Exception e) {
			result = "-2";
			e.printStackTrace();
		}
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;
	}

	public String logout() {
		System.out.println("logout");
		Object user = session.get("user");
		if (user != null) {
			session.remove("user");
			session.remove("userId");
			session.remove("name");
		}
		return NONE;
	}

	public String getLoginCheckCode() {
		Map<String, Object> infoMap = userService.getLoginCheckCode(username);
		int status = (int) infoMap.get("status");
		if (status == 1) {
			String checkCode = (String) infoMap.get("checkCode");
			session.put("loginCheckCode", checkCode);
		}
		this.inputStream = new ByteArrayInputStream((status + "").getBytes());
		return SUCCESS;
	}

	/********************/
	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public long getUsername() {
		return username;
	}

	public void setUsername(long username) {
		this.username = username;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getCheckCode() {
		return checkCode;
	}

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
