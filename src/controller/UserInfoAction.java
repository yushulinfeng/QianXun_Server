package controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.apache.struts2.interceptor.SessionAware;

import service.BusinessServiceService;
import service.UserService;
import util.BaiChuanUtils;
import util.ImageUtil;
import util.MsgUtil;

import com.mob.sms.spi.SmsVerifyKit;
import com.opensymphony.xwork2.ActionSupport;

import config.BaseConfiger;
import config.BasicInfoConfig;
import dao.UserDAO;
import domain.BusinessService;
import domain.User;

public class UserInfoAction extends ActionSupport implements SessionAware {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4453619769683506633L;
	private Map<String, Object> session;
	private InputStream inputStream;
	private UserService userService;
	private BusinessServiceService businessServiceService;

	private long username;
	private String checkCode;
	private String password;
	private int userId;
	private int serviceId;

	private File backgroundImage;
	private File headIcon;
	private UserDAO userDAO;
	private String appSecret;
	private User user;

	private String jsonObj;

	public String updateLocation() {
		int status = -1;
		JSONObject js = JSONObject.fromObject(jsonObj);
		try {
			long username = js.getLong("username");
			String location_x = js.getString("location_x");
			String location_y = js.getString("location_y");
			userService.updateLocation(username, location_x, location_y);
			status = 1;
		} catch (Exception e) {
			e.printStackTrace();
			status = -2;
		}

		inputStream = MsgUtil.sendString(status + "");

		return SUCCESS;
	}

	public String updateUserBackGroundImage() {
		System.out.println("updateUserBackGroundImage:" + userId + ":" + backgroundImage);
		String result = "-1";
		boolean flag = userService.updateUserBackGroundImage(userId, backgroundImage);

		if (flag) {
			result = "1";
		} else {
			result = "-1";
		}

		System.out.println("updateUserBackGroundImage-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;
	}

	public String updateUserHeadIcon() {
		System.out.println("updateUserHeadIcon:" + userId + ":" + headIcon);
		String result = "-1";

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("updateUserHeadIcon-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		boolean flag = userService.updateUserHeadIcon(userId, headIcon);

		if (flag) {
			result = "1";
		} else {
			result = "-1";
		}

		System.out.println("updateUserHeadIcon-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;
	}

	public String getIdByUsername() {
		System.out.println("getIdByUsername-username:" + username);
		String result = "-1";

		// Object user = session.get("user");
		// System.out.println("user is " + user);
		// if (user == null) {
		// result = "-6";// 未登录
		// System.out.println("updateUserHeadIcon-result:" + result);
		// inputStream = MsgUtil.sendString(result);
		// return SUCCESS;
		// }

		int status = -1;
		try {
			status = userService.getIdByUsername(username);
			if (status == -1) {
				status = -3;// -3不存在该username
			}
		} catch (Exception e) {
			e.printStackTrace();
			status = -2;
		}

		result = status + "";
		System.out.println("getIdByUsername-result:" + result);
		inputStream = MsgUtil.sendString(result + "");

		return SUCCESS;
	}

	public String getInfoById() {
		// System.out.println("getInfoById-userId:"+userId);
		// String result = "-1";
		//
		// if (user == null) {
		// result = "-6";// 未登录
		// System.out.println("updateUserHeadIcon-result:" + result);
		// inputStream = MsgUtil.sendString(result);
		// return SUCCESS;
		// }

		User user = userDAO.findById(userId);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.setExcludes(new String[] { "password", "concernedPeople", "favoriteServices", "favoriteServices",
				"photos", "receivedRequest", "services", "todoRequest", "userComment", "userRequest", "" });
		JSONObject userJo = JSONObject.fromObject(user, jsonConfig);
		System.out.println("getInfoById:" + userId + ":" + userJo);
		inputStream = new ByteArrayInputStream(userJo.toString().getBytes());
		return SUCCESS;
	}

	public String updateInfoById() {
		String result = "-1";
		System.out.println("updateInfoById-id:" + user.getId());
		System.out.println("updateInfoById-nickName:" + user.getNickName());
		System.out.println("updateInfoById-username:" + user.getUsername());
		System.out.println("updateInfoById-headIcon:" + user.getHeadIcon());
		System.out.println("updateInfoById-Iconfile:" + user.getIconfile());
		System.out.println("updateInfoById-school:" + user.getSchool());
		int status = -1;

		Object sessionUser = session.get("user");
		System.out.println("user is " + sessionUser);
		if (sessionUser == null) {
			result = "-6";// 未登录
			System.out.println("updateUserHeadIcon-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		try {
			if (user.getId() != 0) {
				User u = userService.getById(user.getId());
				if (u != null) {
					// username不允许修改
					// u.setUsername(user.getUsername());
					u.setBirthday(user.getBirthday());
					u.setAddress(user.getAddress());
					if (!"".equals(user.getIconfile()) && !"null".equals(user.getIconfile())
							&& null != user.getIconfile()) {
						String picName = u.getUsername() + System.currentTimeMillis() + ".png";
						File file = new File(BaseConfiger.FileSavePath + BasicInfoConfig.headIconDir + picName);
						// 得到图片保存的位置(根据root来得到图片保存的路径在tomcat下的该工程里)
						System.out.println("headIcon saved path:" + BaseConfiger.FileSavePath
								+ BasicInfoConfig.headIconDir + picName);

						InputStream is = new FileInputStream(user.getIconfile());

						// 把图片写入到上面设置的路径里
						OutputStream os = new FileOutputStream(file);
						byte[] buffer = new byte[400];
						int length = 0;
						while ((length = is.read(buffer)) > 0) {
							os.write(buffer, 0, length);
						}
						is.close();
						os.close();

						ImageUtil.zipTo400(BaseConfiger.FileSavePath + BasicInfoConfig.headIconDir + picName,
								BaseConfiger.FileSavePath + BasicInfoConfig.SmallheadIconDir + picName);
						BaiChuanUtils.updateUser(userId, user.getNickName(), BasicInfoConfig.HOST
								+ BasicInfoConfig.headIconDir + picName);
						u.setHeadIcon("/qianxun" + BasicInfoConfig.headIconDir + picName);
					}
					u.setNickName(user.getNickName());
					u.setGender(user.getGender());
					u.setSign(user.getSign());
					u.setHobby(user.getHobby());
					u.setJob(user.getJob());
					u.setSchool(user.getSchool());

					userService.update(u);
					status = 1;
				} else {
					status = -4; // 无此用户
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			status = -2;
		}
		result = status + "";
		System.out.println("updateInfoById-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	public String getInfoByUsername() {
		String result = "failed";
		System.out.println("getInfoByUsername-username:" + username);
		JSONObject userJo = null;
		try {
			User user = userService.findByUsername(username);
			JsonConfig jsConfig = new JsonConfig();
			jsConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			jsConfig.setExcludes(new String[] { "password", "userRequest", "userComment", "todoRequest",
					"concernedPeople", "favoriteServices", "photos", "receivedRequest", "services" });
			userJo = JSONObject.fromObject(user, jsConfig);
			result = userJo.toString();
		} catch (Exception e) {
			result = "failed";
			e.printStackTrace();
		}

		System.out.println("getInfoByUsername-result:" + result);
		inputStream = MsgUtil.sendString(result);
		return SUCCESS;
	}

	/**
	 * 已精简
	 * 
	 * @return
	 */
	// 得到所有关注的人
	public String getAllConcernPeopleById() {
		String result = "-1";
		System.out.println("getAllConcernPeopleById-userId:" + userId);

		Object sessionUser = session.get("user");
		System.out.println("user is " + sessionUser);
		if (sessionUser == null) {
			result = "-6";// 未登录
			System.out.println("updateUserHeadIcon-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		try {

			List<User> list = userService.getAllConcernPeople(userId);

			JSONObject jsResp = new JSONObject();
			JsonConfig jsConfig = new JsonConfig();
			jsConfig.setExcludes(new String[] { "age", "address", "birthday", "concernedPeople", "favoriteServices",
					"gender", "hobby", "homePageBackgroundImage", "job", "latestLocation_x", "latestLocation_y",
					"password", "photos", "rank", "rank_credit", "receivedRequest", "registCheckCode", "registTime",
					"services", "sign", "todoRequest", "userComment", "userRequest", "verifyStatus", "alipay_verify" });
			jsConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			JSONArray ja = JSONArray.fromObject(list, jsConfig);

			jsResp.put("list", ja);
			result = jsResp.toString();
		} catch (Exception e) {
			e.printStackTrace();
			result = "-2";
		}

		System.out.println("getAllConcernPeopleById-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	// 添加收藏的服务
	public String addFavoriteService() {
		String result = "-1";
		System.out.println("addFavoriteService-serviceId:" + serviceId);
		System.out.println("addFavoriteService-userId:" + userId);

		Object sessionUser = session.get("user");
		System.out.println("user is " + sessionUser);
		if (sessionUser == null) {
			result = "-6";// 未登录
			System.out.println("updateUserHeadIcon-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		try {
			User u = userService.getById(userId);
			if (u == null) {
				result = "-3";// 没有此用户
				System.out.println("addFavoriteService-result:" + result);
				inputStream = MsgUtil.sendString(result);
				return SUCCESS;
			}
			BusinessService bs = businessServiceService.getByBServiceId(serviceId);
			if (bs == null) {
				result = "-5";// 没有此服务
				System.out.println("addFavoriteService-result:" + result);
				inputStream = MsgUtil.sendString(result);
				return SUCCESS;
			}
			Set<BusinessService> set = u.getFavoriteServices();

			boolean isAdded = false;
			Iterator<BusinessService> it = set.iterator();
			while (it.hasNext()) {
				BusinessService bss = it.next();
				if (bss.getId() == serviceId) {
					isAdded = true;
					break;
				}
			}

			if (!isAdded) {
				int number = bs.getFavoriteNumber();
				bs.setFavoriteNumber(++number);
				businessServiceService.update(bs);
				set.add(bs);
				u.setFavoriteServices(set);
				userService.update(u);
				result = "1";
			} else {
				result = "-4"; // 已经关注过
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = "-2";
		}

		System.out.println("addFavoriteService-result:" + result);
		inputStream = MsgUtil.sendString(result);
		return SUCCESS;
	}

	// 删除收藏的服务
	public String cancelFavoriteService() {
		String result = "-1";
		System.out.println("cancelFavoriteService-serviceId:" + serviceId);
		System.out.println("cancelFavoriteService-userId:" + userId);

		Object sessionUser = session.get("user");
		System.out.println("user is " + sessionUser);
		if (sessionUser == null) {
			result = "-6";// 未登录
			System.out.println("updateUserHeadIcon-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		try {
			User u = userService.getById(userId);
			if (u == null) {
				result = "-3";// 没有此用户
				System.out.println("addFavoriteService-result:" + result);
				inputStream = MsgUtil.sendString(result);
				return SUCCESS;
			}
			BusinessService bs = businessServiceService.getByBServiceId(serviceId);
			if (bs == null) {
				result = "-5";// 没有此服务
				System.out.println("cancelFavoriteService-result:" + result);
				inputStream = MsgUtil.sendString(result);
				return SUCCESS;
			}
			Set<BusinessService> set = u.getFavoriteServices();

			boolean isAdded = false;
			Iterator<BusinessService> it = set.iterator();
			while (it.hasNext()) {
				BusinessService bss = it.next();
				if (bss.getId() == serviceId) {
					isAdded = true;
					break;
				}
			}

			if (isAdded) {
				int number = bs.getFavoriteNumber();
				bs.setFavoriteNumber(--number);
				businessServiceService.update(bs);
				set.remove(bs);
				u.setFavoriteServices(set);
				userService.update(u);
				result = "1";
			} else {
				result = "-4"; // 已经关注过这个人
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = "-2";
		}

		System.out.println("cancelFavoriteService-result:" + result);
		inputStream = MsgUtil.sendString(result);
		return SUCCESS;
	}

	// 加关注
	public String concernPeople() {
		String result = "-1";
		System.out.println("concernPeople-jsonObj:" + jsonObj);
		JSONObject js = JSONObject.fromObject(jsonObj);
		System.out.println("concernPeople:" + js.toString());

		Object sessionUser = session.get("user");
		System.out.println("user is " + sessionUser);
		if (sessionUser == null) {
			result = "-6";// 未登录
			System.out.println("updateUserHeadIcon-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		try {
			int id = js.getInt("id");
			int concernedId = js.getInt("concernedId");

			if (id == concernedId) {
				result = "-4";
				System.out.println("concernPeople-result:" + result);
				inputStream = MsgUtil.sendString(result);
				return SUCCESS;
			}

			User user = userService.getById(id);

			User concern = new User();
			concern.setId(concernedId);

			boolean isConcerned = false;
			Set<User> concerns = user.getConcernedPeople();

			Iterator<User> it = concerns.iterator();
			while (it.hasNext()) {
				User u = it.next();
				if (u.getId() == concernedId) {
					isConcerned = true;
					break;
				}
			}
			if (!isConcerned) {
				concerns.add(concern);
				user.setConcernedPeople(concerns);
				userService.update(user);
				result = "1";
			} else {
				result = "-3"; // 已经关注
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = "-2";
		}
		System.out.println("concernPeople-result:" + result);
		inputStream = MsgUtil.sendString(result);
		return SUCCESS;
	}

	// 取消关注
	public String cancelConcern() {
		String result = "-1";

		Object sessionUser = session.get("user");
		System.out.println("user is " + sessionUser);
		if (sessionUser == null) {
			result = "-6";// 未登录
			System.out.println("updateUserHeadIcon-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		JSONObject js = JSONObject.fromObject(jsonObj);
		System.out.println("cancelConcern:" + js.toString());
		try {
			int id = js.getInt("id");
			int concernedId = js.getInt("concernedId");

			User user = userService.getById(id);
			// User concern = new User();
			// concern.setId(concernedId);
			User concern = userService.getById(concernedId);

			boolean isConcerned = false;
			Set<User> concerns = user.getConcernedPeople();

			Iterator<User> it = concerns.iterator();
			while (it.hasNext()) {
				User u = it.next();
				if (u.getId() == concernedId) {
					isConcerned = true;
					break;
				}
			}

			if (isConcerned) {
				concerns.remove(concern);
				user.setConcernedPeople(concerns);
				userService.update(user);
				result = "1";
			} else {
				result = "-3"; // 未关注过
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = "-2";
		}
		System.out.println("cancelConcern-result:" + result);
		inputStream = MsgUtil.sendString(result);
		return SUCCESS;

	}

	public String concernAllPeople() {
		String result = "failed";

		Object sessionUser = session.get("user");
		System.out.println("user is " + sessionUser);
		if (sessionUser == null) {
			result = "-6";// 未登录
			System.out.println("updateUserHeadIcon-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		JSONObject js = JSONObject.fromObject(jsonObj);
		System.out.println("concernAllPeople:" + js.toString());
		try {
			long username = js.getLong("username");
			JSONArray peoples = js.getJSONArray("concernAll");

			User user = userService.findByUsername(username);

			Set<User> concerns = user.getConcernedPeople();
			concerns.removeAll(concerns);

			for (int i = 0; i < peoples.size(); i++) {
				long uname = peoples.getLong(i);
				User us = userService.findByUsername(uname);
				concerns.add(us);
			}
			user.setConcernedPeople(concerns);
			userService.update(user);
			result = "success";
		} catch (Exception e) {
			e.printStackTrace();
			result = "failed";
		}
		System.out.println("concernAllPeople-result:" + result);
		inputStream = MsgUtil.sendString(result);
		return SUCCESS;

	}

	// 判断某人是否已收藏某项服务
	public String isFavorite() {
		System.out.println("isFavorite-userId:" + userId);
		System.out.println("isFavorite-serviceId:" + serviceId);
		String result = "-1";

		Object sessionUser = session.get("user");
		System.out.println("user is " + sessionUser);
		if (sessionUser == null) {
			result = "-6";// 未登录
			System.out.println("updateUserHeadIcon-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		try {
			boolean flag = userService.isFavorite(userId, serviceId);
			if (flag) {
				result = "1";
			} else {
				result = "0";
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = "-2";
		}

		System.out.println("isFavorite-result:" + result);
		inputStream = MsgUtil.sendString(result);
		return SUCCESS;
	}

	// 得到该用户收藏的所有的服务
	public String getAllFavoriteServiceByUserId() {
		String result = "-1";
		System.out.println("getAllFavoriteServiceByUserId-jsonObj:" + jsonObj);

		Object sessionUser = session.get("user");
		System.out.println("user is " + sessionUser);
		if (sessionUser == null) {
			result = "-6";// 未登录
			System.out.println("updateUserHeadIcon-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		try {
			JSONObject js = JSONObject.fromObject(jsonObj);
			int userId = js.getInt("userId");
			int page = js.getInt("page");

			List<BusinessService> list = userService.getAllFBByUserId(page, userId);

			JSONObject jsResp = new JSONObject();
			JsonConfig jsConfig = new JsonConfig();
			jsConfig.setExcludes(new String[] { "canServiceDay", "category", "exchange", "finishedPeople",
					"favoritePeople", "sign", "greatPeople", "location_x", "location_y", "serviceCity", "serviceTime",
					"userId", "address", "age", "birthday", "concernedPeople", "favoriteServices", "hobby",
					"homePageBackgroundImage", "job", "latestLocation_x", "latestLocation_y", "password", "photos",
					"rank", "rank_credit", "receivedRequest", "registCheckCode", "registTime", "services",
					"todoRequest", "userComment", "userRequest" });
			jsConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			JSONArray ja = JSONArray.fromObject(list, jsConfig);

			jsResp.put("list", ja);
			result = jsResp.toString();
		} catch (Exception e) {
			e.printStackTrace();
			result = "-2";
		}

		System.out.println("getAllFavoriteServiceByUserId-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	public String getMyInfo() {
		try {
			User user = (User) session.get("user");
			if (user == null) {
				setInputStream(new ByteArrayInputStream("-2".getBytes()));
			} else {
				System.out.println(user);
				JSONObject userJo = JSONObject.fromObject(user);
				setInputStream(new ByteArrayInputStream(userJo.toString().getBytes()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}

	public String getChangePwdCheckCode() {
		Map<String, Object> infoMap = userService.getLoginCheckCode(username);
		int status = (int) infoMap.get("status");
		if (status == 1) {
			String checkCode = (String) infoMap.get("checkCode");
			session.put("changePwdCheckCode", checkCode);
		}
		this.inputStream = new ByteArrayInputStream((status + "").getBytes());
		return SUCCESS;
	}

	public String changePwd() {
		String result = "failed";
		int status = -1;
		try {
			String resp = new SmsVerifyKit(username + "", checkCode).go();
			status = JSONObject.fromObject(resp).getInt("status");
			if (status == 200) {
				Map<String, Object> map = userService.changePwd(username, password);
				status = (int) map.get("status");
			}
		} catch (Exception e) {
			status = -2;
			System.out.println("*" + e.getMessage());
			e.printStackTrace();
		}
		result = status + "";
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;
	}

	public String changePwd2() {
		System.out.println("hehe");
		Object obj = session.get("changePwdCheckCode");
		String result;
		if (obj == null) {
			result = "-2";
		} else {
			String key = (String) obj;
			if (key.equals(checkCode)) {
				session.remove("changePwdCheckCode");
				Map<String, Object> infoMap = userService.changePwd(username, password);
				int status = (int) infoMap.get("status");
				result = status + "";
			} else {
				session.remove("changePwdCheckCode");
				result = "-2";
			}
		}
		System.out.println(result);
		this.inputStream = new ByteArrayInputStream(result.getBytes());
		return SUCCESS;
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

	public String getCheckCode() {
		return checkCode;
	}

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public String getJsonObj() {
		return jsonObj;
	}

	public void setJsonObj(String jsonObj) {
		this.jsonObj = jsonObj;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
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

	public int getServiceId() {
		return serviceId;
	}

	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}

	public BusinessServiceService getBusinessServiceService() {
		return businessServiceService;
	}

	public void setBusinessServiceService(BusinessServiceService businessServiceService) {
		this.businessServiceService = businessServiceService;
	}

	public File getBackgroundImage() {
		return backgroundImage;
	}

	public void setBackgroundImage(File backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

	public File getHeadIcon() {
		return headIcon;
	}

	public void setHeadIcon(File headIcon) {
		this.headIcon = headIcon;
	}

}
