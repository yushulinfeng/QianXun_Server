package controller;

import java.io.InputStream;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import util.MsgUtil;

import com.opensymphony.xwork2.ActionSupport;

import dao.AdminDAO;

public class AdminOpAction extends ActionSupport implements SessionAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7031848578929485816L;
	private Map<String, Object> session;
	private InputStream inputStream;

	private AdminDAO adminDAO;

	private String startTime;
	private String endTime;

	/**
	 * 通过时间查询当前时间内注册人数
	 * 
	 * @return
	 */
	public String getRegistUserByTime() {
		System.out.println("getRegistUserByTime-startTime:" + startTime);
		System.out.println("getRegistUserByTime-endTime:" + endTime);

		String result = "-1";

		// 去空格
		startTime = startTime.trim().replaceAll("\\s", "");
		endTime = endTime.trim().replaceAll("\\s", "");

		if (!startTime.matches("[0-9]{0,4}-[0-9]{0,2}-[0-9]{0,2}")) {
			result = "-3";
			System.out.println("getRegistUserByTime-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}
		if (!endTime.matches("[0-9]{0,4}-[0-9]{0,2}-[0-9]{0,2}")) {
			result = "-3";
			System.out.println("getRegistUserByTime-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		long number = adminDAO.getRegistUserNumberByTime(startTime, endTime);

		result = number + "";
		System.out.println("getRegistUserByTime-result:" + result);
		inputStream = MsgUtil.sendString(result);
		return SUCCESS;

	}

	public Map<String, Object> getSession() {
		return session;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public AdminDAO getAdminDAO() {
		return adminDAO;
	}

	public void setAdminDAO(AdminDAO adminDAO) {
		this.adminDAO = adminDAO;
	}

}
