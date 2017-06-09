package controller;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.struts2.interceptor.SessionAware;

import service.StudentVerifyService;
import service.UserService;
import service.UserVerifyService;
import util.MsgUtil;

import com.opensymphony.xwork2.ActionSupport;

import dao.AdminDAO;
import domain.Admin;
import domain.StudentVerify;
import domain.User;
import domain.UserVerify;

public class UserVerifyAction extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = -6165689196957147993L;

	private Map<String, Object> session;

	private UserService userService;
	private UserVerifyService userVerifyService;
	private StudentVerifyService studentVerifyService;
	private AdminDAO adminDAO;
	private InputStream inputStream;

	private UserVerify userVerify;
	private StudentVerify studentVerify;

	private int id;
	private int page;
	private long username;
	private int uvId;

	private Admin admin;

	public String adminLogin() {
		int status = -1;
		System.out.println("adminLogin-username:" + admin.getUsername());
		try {
			Admin ad = adminDAO.getByUsername(admin.getUsername());
			if (ad != null) {
				if (ad.getPassword().equals(admin.getPassword())) {
					status = 1;
					session.put("admin", admin);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			status = -2;
		}

		inputStream = MsgUtil.sendString(status + "");

		return SUCCESS;
	}

	public String adminLogout() {
		System.out.println("adminLogout");
		Object admin = session.get("admin");
		if (admin != null) {
			session.remove(admin);
		}
		return NONE;
	}

	public String saveUser() {
		int status = -1;
		System.out.println("saveUser-phone:" + userVerify.getPhone());
		System.out.println("saveUser-realName:" + userVerify.getRealName());

		try {
			User user = userService.findByUsername(userVerify.getPhone());
			if (user == null) {
				inputStream = MsgUtil.sendString("-4");// 无此人
				return SUCCESS;
			}
			if (user.getVerifyStatus() != 0) {
				inputStream = MsgUtil.sendString("-3");// 已经认证完成或者正在审核中
				return SUCCESS;
			}
			boolean flag = userVerifyService.saveUserVerify(userVerify);
			if (flag) {
				status = 1;
				user.setVerifyStatus(-1);
				userService.update(user);
			}
		} catch (Exception e) {
			e.printStackTrace();
			status = -2;
		}

		System.out.println("saveUser-result:" + status);
		inputStream = MsgUtil.sendString(status + "");

		return SUCCESS;
	}

	public String saveStu() {
		int status = -1;
		System.out.println("saveStu-Phone:" + studentVerify.getPhone());
		try {
			User user = userService.findByUsername(studentVerify.getPhone());
			if (user == null) {
				System.out.println("saveStu-status:-4");
				inputStream = MsgUtil.sendString("-4");// 无此人
				return SUCCESS;
			}
			if (user.getVerifyStatus() != 1) {
				status = -3;// 还未通过身份证认证
			} else {
				boolean flag = studentVerifyService.saveStudentVerify(studentVerify);
				if (flag) {
					status = 1;
					user.setVerifyStatus(-2);
					userService.update(user);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			status = -2;
		}

		System.out.println("saveStu-status:" + status);
		inputStream = MsgUtil.sendString(status + "");
		return SUCCESS;
	}

	public String checkUserInfoByPage() {
		String result = "failed";

		Object admin = session.get("admin");
		if (admin == null) {
			inputStream = MsgUtil.sendString("-3");// 未登录
			return SUCCESS;
		}

		try {
			List<UserVerify> list = userVerifyService.getUserInfoByPage(page);

			JSONObject jsResp = new JSONObject();
			jsResp.put("list", list);

			result = jsResp.toString();

		} catch (Exception e) {
			e.printStackTrace();
			result = "failed";
		}

		inputStream = MsgUtil.sendString(result);
		return SUCCESS;

	}

	public String checkStuInfoByPage() {
		String result = "failed";

		Object admin = session.get("admin");
		if (admin == null) {
			inputStream = MsgUtil.sendString("-3");// 未登录
			return SUCCESS;
		}

		try {
			List<StudentVerify> list = studentVerifyService.getByPage(page);

			JSONObject jsResp = new JSONObject();
			jsResp.put("list", list);

			result = jsResp.toString();
		} catch (Exception e) {
			e.printStackTrace();
			result = "failed";
		}

		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	public String getUserVerifyInfoById() {
		String result = "failed";

		Object admin = session.get("admin");
		if (admin == null) {
			inputStream = MsgUtil.sendString("-3");// 未登录
			return SUCCESS;
		}

		try {
			UserVerify uv = userVerifyService.getUserVerifyInfoById(id);
			JSONObject js = JSONObject.fromObject(uv);
			result = js.toString();
		} catch (Exception e) {
			e.printStackTrace();
			result = "failed";
		}
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;
	}

	public String getStuVerifyInfoById() {
		String result = "failed";

		Object admin = session.get("admin");
		if (admin == null) {
			inputStream = MsgUtil.sendString("-3");// 未登录
			return SUCCESS;
		}

		try {
			StudentVerify uv = studentVerifyService.getStuVerifyInfoById(id);
			JSONObject js = JSONObject.fromObject(uv);
			result = js.toString();
		} catch (Exception e) {
			e.printStackTrace();
			result = "failed";
		}
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;
	}

	public String confirmUserVerify() {
		int status = -1;

		Object admin = session.get("admin");
		if (admin == null) {
			inputStream = MsgUtil.sendString("-3");// 未登录
			return SUCCESS;
		}

		try {
			User user = userService.findByUsername(username);
			user.setVerifyStatus(User.USERID_COMMIT_VERIFY);
			userService.update(user);

			// 加积分
			try {
				userService.addRank_Credit(user.getId(), 100);
			} catch (Exception e) {
				e.printStackTrace();
			}

			status = 1;
		} catch (Exception e) {
			e.printStackTrace();
			status = -2;
		}

		inputStream = MsgUtil.sendString(status + "");

		return SUCCESS;
	}

	public String confirmStuVerify() {
		int status = -1;

		Object admin = session.get("admin");
		if (admin == null) {
			inputStream = MsgUtil.sendString("-3");// 未登录
			return SUCCESS;
		}

		try {
			User user = userService.findByUsername(username);
			user.setVerifyStatus(User.STU_COMMIT_VERIFY);
			userService.update(user);

			// 加积分
			try {
				userService.addRank_Credit(user.getId(), 200);
			} catch (Exception e) {
				e.printStackTrace();
			}

			status = 1;
		} catch (Exception e) {
			e.printStackTrace();
			status = -2;
		}

		inputStream = MsgUtil.sendString(status + "");

		return SUCCESS;

	}

	public String denyUserVerify() {
		int status = -1;

		Object admin = session.get("admin");
		if (admin == null) {
			inputStream = MsgUtil.sendString("-3");// 未登录
			return SUCCESS;
		}

		try {
			User user = userService.findByUsername(username);
			user.setVerifyStatus(0);
			userService.update(user);
			userVerifyService.delete(uvId);
			status = 1;
		} catch (Exception e) {
			e.printStackTrace();
			status = -2;
		}

		inputStream = MsgUtil.sendString(status + "");

		return SUCCESS;

	}

	public String denyStuVerify() {
		int status = -1;

		Object admin = session.get("admin");
		if (admin == null) {
			inputStream = MsgUtil.sendString("-3");// 未登录
			return SUCCESS;
		}

		try {
			User user = userService.findByUsername(username);
			user.setVerifyStatus(1); // 回归身份证认证
			userService.update(user);
			status = 1;
		} catch (Exception e) {
			e.printStackTrace();
			status = -2;
		}

		inputStream = MsgUtil.sendString(status + "");

		return SUCCESS;

	}

	/*******************/
	public UserVerify getUserVerify() {
		return userVerify;
	}

	public void setUserVerify(UserVerify userVerify) {
		this.userVerify = userVerify;
	}

	public UserVerifyService getUserVerifyService() {
		return userVerifyService;
	}

	public void setUserVerifyService(UserVerifyService userVerifyService) {
		this.userVerifyService = userVerifyService;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public StudentVerifyService getStudentVerifyService() {
		return studentVerifyService;
	}

	public void setStudentVerifyService(StudentVerifyService studentVerifyService) {
		this.studentVerifyService = studentVerifyService;
	}

	public StudentVerify getStudentVerify() {
		return studentVerify;
	}

	public void setStudentVerify(StudentVerify studentVerify) {
		this.studentVerify = studentVerify;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public long getUsername() {
		return username;
	}

	public void setUsername(long username) {
		this.username = username;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public Admin getAdmin() {
		return admin;
	}

	public void setAdmin(Admin admin) {
		this.admin = admin;
	}

	public AdminDAO getAdminDAO() {
		return adminDAO;
	}

	public void setAdminDAO(AdminDAO adminDAO) {
		this.adminDAO = adminDAO;
	}

	public int getUvId() {
		return uvId;
	}

	public void setUvId(int uvId) {
		this.uvId = uvId;
	}

}
