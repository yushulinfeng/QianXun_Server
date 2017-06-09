package controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import service.UserService;

import com.opensymphony.xwork2.ActionSupport;

public class UserCheckAction extends ActionSupport implements SessionAware {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1633820249694400053L;
	private Map<String, Object> session;
	private InputStream inputStream;
	private long username;
	private UserService userService;

	public String check() {
		Map<String, Object> infoMap = userService.check(username);
		setInputStream(new ByteArrayInputStream(infoMap.get("status").toString().getBytes()));
		return SUCCESS;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setUsername(long username) {
		this.username = username;
	}

	public long getUsername() {
		return username;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public UserService getUserService() {
		return userService;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

}
