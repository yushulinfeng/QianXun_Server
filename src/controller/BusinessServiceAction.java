package controller;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
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
import util.CheckUtil;
import util.MsgUtil;

import com.opensymphony.xwork2.ActionSupport;

import domain.Admin;
import domain.BusinessService;
import domain.ServiceImage;
import domain.User;

public class BusinessServiceAction extends ActionSupport implements Serializable, SessionAware {

	/**
	 * 
	 * 
	 */
	private static final long serialVersionUID = 3871836392305737624L;

	private Map<String, Object> session;

	private InputStream inputStream;

	private BusinessServiceService businessServiceService;
	private BusinessService businessService;
	private UserService userService;
	private int serviceId;// 服务id
	private int userId;

	private int page;
	private String jsonObj;
	private int category;
	private String school;

	private File image;
	private Set<File> images;

	public String save() {
		int status = -1;
		System.out.println("businessService-saving...");
		System.out.println("businessService.user:" + businessService.getUser().getId());
		System.out.println("businessService.time:" + businessService.getTime());

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			status = -6;
			String result = status + "";// 未登录
			System.out.println("getBalance-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		// checkIllegalwords
		boolean hasIllegalWords = CheckUtil.hasIllegalWords(businessService.getDetail());
		if (hasIllegalWords) {
			status = -3;

			// 加积分
			try {
				userService.addRank_Credit(businessService.getUser().getId(), -50);
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("save-status:" + status);
			inputStream = MsgUtil.sendString(status + "");
			return SUCCESS;
		}

		boolean flag = businessServiceService.save(businessService);
		if (flag) {
			status = 1;
			// 加积分
			try {
				userService.addRank_Credit(businessService.getUser().getId(), 10);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			status = -1;
		}

		System.out.println("businessService-save-status:" + status);
		inputStream = MsgUtil.sendString(status + "");

		return SUCCESS;
	}

	// shield:屏蔽的意思
	public String shield() {
		int status = -1;
		System.out.println("businessService-shield...");
		System.out.println("businessService-serviceId:" + serviceId);
		System.out.println("businessService-userId:" + userId);

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			status = -6;
			String result = status + "";// 未登录
			System.out.println("businessService-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		BusinessService businessService = businessServiceService.getByBServiceId(serviceId);

		if (businessService == null) {
			status = -4;
			String result = status + "";// 没有这个服务
			System.out.println("businessService-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		if (businessService.getUser().getId() != userId) {
			status = -3;
			String result = status + "";// 不是这个人自己的服务
			System.out.println("businessService-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		boolean flag = businessServiceService.updateStatus(serviceId, BusinessService.HASDELETED_STATUS);
		if (flag) {
			status = 1;
			// 加积分
			try {
				userService.addRank_Credit(businessService.getUser().getId(), -50);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			status = -1;
		}

		System.out.println("businessService-shield-status:" + status);
		inputStream = MsgUtil.sendString(status + "");

		return SUCCESS;
	}

	/**
	 * shield:屏蔽的意思
	 * 
	 * 管理员删除服务用，并给发推送
	 * 
	 * @return
	 */

	public String adminShield() {
		int status = -1;
		System.out.println("businessService-adminShield...");
		System.out.println("businessService-adminShield:" + serviceId);

		Admin admin = (Admin) session.get("admin");
		System.out.println("admin is " + admin);
		if (admin == null) {
			status = -6;
			String result = status + "";// 未登录
			System.out.println("businessService-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		BusinessService businessService = businessServiceService.getByBServiceId(serviceId);

		if (businessService == null) {
			status = -4;
			String result = status + "";// 没有这个服务
			System.out.println("businessService-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		boolean flag = businessServiceService.updateStatus(serviceId, BusinessService.HASDELETED_STATUS);
		if (flag) {
			status = 1;
			// 加积分
			try {
				userService.addRank_Credit(businessService.getUser().getId(), -50);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String context = "[通知]您的服务包含违规内容，已被管理员屏蔽。";
			String detail = "[通知]您的服务包含违规内容，已被管理员屏蔽，请及时查看。\n服务名：" + businessService.getName();
			BaiChuanUtils.pushJson(businessService.getUser().getId(), BaiChuanUtils.PUBLISH_USERGETSERVICE, context,
					detail);

		} else {
			status = -1;
		}

		System.out.println("businessService-adminShield-status:" + status);
		inputStream = MsgUtil.sendString(status + "");

		return SUCCESS;
	}

	public String getById() {
		System.out.println("getById-serviceId:" + serviceId);

		String result = "-1";

		try {
			BusinessService businessService = businessServiceService.getByBServiceId(serviceId);
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			jsonConfig.setExcludes(new String[] { "password", "userId", "sign", "registTime", "registCheckCode", "job",
					"hobby", "birthday", "address", "age", "homePageBackgroundImage", "latestLocation_x",
					"latestLocation_y", "concernedPeople", "favoriteServices", "photos", "receivedRequest", "services",
					"todoRequest", "userComment", "userRequest", "favoritePeople", "greatPeople" });
			JSONObject jsResp = JSONObject.fromObject(businessService, jsonConfig);
			result = jsResp.toString();
		} catch (Exception e) {
			e.printStackTrace();
			result = "-2";
		}

		System.out.println("getById-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;
	}

	public String getByType() {
		String result = "-1";
		System.out.println("getByType-category:" + category);
		System.out.println("getByType-page:" + page);

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("getBalance-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		List<BusinessService> list = businessServiceService.getByType(page, category);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray ja = JSONArray.fromObject(list, jsonConfig);

		JSONObject jsResp = new JSONObject();
		jsResp.put("list", ja);

		result = result.replaceAll("/big/", "/small/");// 将原图转化为缩略图

		result = jsResp.toString();

		System.out.println("getByType-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;
	}

	public String addGreate() {
		String result = "-1";

		System.out.println("addGreate-userId:" + userId);
		System.out.println("addGreate-serviceId:" + serviceId);

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("getBalance-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		User u = new User();
		u.setId(userId);
		BusinessService bs = businessServiceService.getByBServiceId(serviceId);
		int great = bs.getGreat();
		Set<User> greatPeople = bs.getGreatPeople();
		greatPeople.add(u);

		bs.setGreat(++great);
		bs.setGreatPeople(greatPeople);
		boolean flag = businessServiceService.update(bs);

		if (flag) {
			result = "1";
		} else {
			result = "-1";
		}

		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	public String getFavoritePeople() {
		System.out.println("getFavoritePeople-serviceId:" + serviceId);
		String result = "-1";

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("getBalance-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		JSONObject jsResp = new JSONObject();

		try {
			List<User> list = businessServiceService.getFavoritePeople(serviceId);

			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setExcludes(new String[] { "canServiceDay", "category", "detail", "exchange", "favoriteNumber",
					"favoritePeople", "finishedPeople", "great", "greatPeople", "images", "location_x", "location_y",
					"name", "reward_money", "reward_thing", "reward_unit", "serviceCity", "serviceTime", "serviceType",
					"status", "time", "concernedPeople", "favoriteServices", "hobby", "homePageBackgroundImage", "job",
					"latestLocation_x", "latestLocation_y", "address", "age", "birthday", "password", "photos", "rank",
					"rank_credit", "receivedRequest", "registCheckCode", "registTime", "services", "sign",
					"todoRequest", "userComment", "userRequest", "verifyStatus" });
			jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			JSONArray ja = JSONArray.fromObject(list, jsonConfig);

			jsResp.put("list", ja);
			result = jsResp.toString();
		} catch (NumberFormatException e) {
			result = "-2";
			e.printStackTrace();
		}

		result = result.replaceAll("/big/", "/small/");// 将原图转化为缩略图

		System.out.println("getFavoritePeople-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	public String getLocalByCategory() {
		System.out.println("getLocalByCategory-jsonObj:" + jsonObj);
		String result = "-1";

		JSONObject jsResp = new JSONObject();

		try {
			JSONObject js = JSONObject.fromObject(jsonObj);
			double x = Double.parseDouble(js.getString("location_x"));
			double y = Double.parseDouble(js.getString("location_y"));
			int page = Integer.parseInt(js.getString("page"));
			int category = Integer.parseInt(js.getString("category"));
			int scope = 10000;

			List<BusinessService> list = businessServiceService.getLocalByCategory(x, y, scope, page, category);

			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			jsonConfig.setExcludes(new String[] { "canServiceDay", "category", "exchange", "finishedPeople",
					"favoritePeople", "sign", "greatPeople", "location_x", "location_y", "serviceCity", "serviceTime",
					"userId", "address", "age", "birthday", "concernedPeople", "favoriteServices", "hobby",
					"homePageBackgroundImage", "job", "latestLocation_x", "latestLocation_y", "password", "photos",
					"rank", "rank_credit", "receivedRequest", "registCheckCode", "registTime", "services",
					"todoRequest", "userComment", "userRequest" });
			JSONArray ja = JSONArray.fromObject(list, jsonConfig);

			jsResp.put("list", ja);
			result = jsResp.toString();
		} catch (Exception e) {
			result = "-2";
			e.printStackTrace();
		}

		result = result.replaceAll("/big/", "/small/");// 将原图转化为缩略图

		System.out.println("getLocalByCategory-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	public String getLatestByCategory() {
		System.out.println("getLatestByCategory-page:" + page);
		System.out.println("getLatestByCategory-category:" + category);
		String result = "-1";

		JSONObject jsResp = new JSONObject();

		try {
			List<BusinessService> list = businessServiceService.getLatestByCategory(page, category);

			// 删掉不用的数据
			for (BusinessService bs : list) {
				if (bs.getUser() != null) {
					if (bs.getUser().getServices() != null) {
						bs.getUser().setServices(null);
					}
				}
				if (bs.getImages() != null) {
					int i = 0;
					Set<ServiceImage> set = new HashSet<>();
					for (ServiceImage si : bs.getImages()) {
						if (i > 3) {
							break;
						}
						set.add(si);
					}
					bs.setImages(set);
				}
			}

			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setExcludes(new String[] { "canServiceDay", "category", "exchange", "finishedPeople",
					"favoritePeople", "sign", "greatPeople", "location_x", "location_y", "serviceCity", "serviceTime",
					"userId", "address", "age", "birthday", "concernedPeople", "favoriteServices", "hobby",
					"homePageBackgroundImage", "job", "latestLocation_x", "latestLocation_y", "password", "photos",
					"rank", "rank_credit", "receivedRequest", "registCheckCode", "registTime", "services",
					"todoRequest", "userComment", "userRequest" });
			jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			JSONArray ja = JSONArray.fromObject(list, jsonConfig);

			jsResp.put("list", ja);
			result = jsResp.toString();
		} catch (NumberFormatException e) {
			result = "-2";
			e.printStackTrace();
		}

		result = result.replaceAll("/big/", "/small/");// 将原图转化为缩略图

		System.out.println("getLatestByCategory-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	/**
	 * 已精简
	 * 
	 * @return
	 */
	public String getBriefByUserId() {
		String result = "-1";
		System.out.println("getBriefByUserId-page:" + page);
		System.out.println("getBriefByUserId-userId:" + userId);

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("getBalance-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.setExcludes(new String[] { "favoritePeople", "greatPeople", "user", "canServiceDay", "detail",
				"exchange", "favoriteNumber", "headIcon", "serviceCity", "serviceTime", "time", "userId", "username" });
		List<BusinessService> list = businessServiceService.getByUserId(page, userId);
		List<BusinessService> realList = new ArrayList<>();
		for (BusinessService bs : list) {// 移除其他images，只留第一张
			Set<ServiceImage> set = bs.getImages();
			Set<ServiceImage> realSet = new HashSet<>();
			for (ServiceImage si : set) {
				si.setService(null);
				realSet.add(si);
				break;
			}
			bs.setImages(realSet);
			realList.add(bs);
		}
		JSONArray jsonArray = JSONArray.fromObject(realList, jsonConfig);
		JSONObject jsResp = new JSONObject();
		jsResp.put("list", jsonArray);

		result = jsResp.toString();

		result = result.replaceAll("/big/", "/small/");// 将原图转化为缩略图

		System.out.println("getBriefByUserId-result" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	public String getLocalByPage() {
		System.out.println("getLocalByPage-jsonObj:" + jsonObj);
		String result = "-1";
		JSONObject jsResp = new JSONObject();

		try {
			JSONObject js = JSONObject.fromObject(jsonObj);
			double x = Double.parseDouble(js.getString("location_x"));
			double y = Double.parseDouble(js.getString("location_y"));
			int page = Integer.parseInt(js.getString("page"));
			double scope = 10000.0;

			List<BusinessService> list = businessServiceService.getLocalByPage(x, y, scope, page);

			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setExcludes(new String[] { "canServiceDay", "category", "exchange", "finishedPeople",
					"favoritePeople", "sign", "greatPeople", "location_x", "location_y", "serviceCity", "serviceTime",
					"userId", "address", "age", "birthday", "concernedPeople", "favoriteServices", "hobby",
					"homePageBackgroundImage", "job", "latestLocation_x", "latestLocation_y", "password", "photos",
					"rank", "rank_credit", "receivedRequest", "registCheckCode", "registTime", "services",
					"todoRequest", "userComment", "userRequest" });
			jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			JSONArray ja = JSONArray.fromObject(list, jsonConfig);

			jsResp.put("list", ja);
			result = jsResp.toString();
		} catch (NumberFormatException e) {
			result = "-2";
			e.printStackTrace();
		} catch (Exception e) {
			result = "-2";
			e.printStackTrace();
		}

		result = result.replaceAll("/big/", "/small/");// 将原图转化为缩略图

		System.out.println("getLocalByPage-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	/**
	 * 得到相同学校的服务
	 * 
	 * @return
	 */
	public String getSchoolByPage() {
		System.out.println("getSchoolByPage-page:" + page);
		String result = "-1";
		JSONObject jsResp = new JSONObject();

		try {

			List<BusinessService> list = businessServiceService.getSchoolByPage(page, school);

			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setExcludes(new String[] { "canServiceDay", "category", "exchange", "finishedPeople",
					"favoritePeople", "sign", "greatPeople", "location_x", "location_y", "serviceCity", "serviceTime",
					"userId", "address", "age", "birthday", "concernedPeople", "favoriteServices", "hobby",
					"homePageBackgroundImage", "job", "latestLocation_x", "latestLocation_y", "password", "photos",
					"rank", "rank_credit", "receivedRequest", "registCheckCode", "registTime", "services",
					"todoRequest", "userComment", "userRequest" });
			jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			JSONArray ja = JSONArray.fromObject(list, jsonConfig);

			jsResp.put("list", ja);
			result = jsResp.toString();
		} catch (NumberFormatException e) {
			result = "-2";
			e.printStackTrace();
		}

		result = result.replaceAll("/big/", "/small/");// 将原图转化为缩略图

		System.out.println("getSchoolByPage-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	/**
	 * 得到相同学校的服务
	 * 
	 * @return
	 */
	public String getSchoolByCategory() {
		System.out.println("getSchoolByCategory-page:" + page);
		System.out.println("getSchoolByCategory-category:" + category);
		System.out.println("getSchoolByCategory-school:" + school);
		String result = "-1";
		JSONObject jsResp = new JSONObject();

		try {

			List<BusinessService> list = businessServiceService.getSchoolByCategory(page, category, school);

			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setExcludes(new String[] { "canServiceDay", "category", "exchange", "finishedPeople",
					"favoritePeople", "sign", "greatPeople", "location_x", "location_y", "serviceCity", "serviceTime",
					"userId", "address", "age", "birthday", "concernedPeople", "favoriteServices", "hobby",
					"homePageBackgroundImage", "job", "latestLocation_x", "latestLocation_y", "password", "photos",
					"rank", "rank_credit", "receivedRequest", "registCheckCode", "registTime", "services",
					"todoRequest", "userComment", "userRequest" });
			jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			JSONArray ja = JSONArray.fromObject(list, jsonConfig);

			jsResp.put("list", ja);
			result = jsResp.toString();
		} catch (NumberFormatException e) {
			result = "-2";
			e.printStackTrace();
		}

		result = result.replaceAll("/big/", "/small/");// 将原图转化为缩略图

		System.out.println("getSchoolByCategory-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	public String getLatestByPage() {
		System.out.println("getLatestByPage-page:" + page);
		String result = "-1";
		JSONObject jsResp = new JSONObject();

		try {

			List<BusinessService> list = businessServiceService.getLatestByPage(page);

			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setExcludes(new String[] { "canServiceDay", "category", "exchange", "finishedPeople",
					"favoritePeople", "sign", "greatPeople", "location_x", "location_y", "serviceCity", "serviceTime",
					"userId", "address", "age", "birthday", "concernedPeople", "favoriteServices", "hobby",
					"homePageBackgroundImage", "job", "latestLocation_x", "latestLocation_y", "password", "photos",
					"rank", "rank_credit", "receivedRequest", "registCheckCode", "registTime", "services",
					"todoRequest", "userComment", "userRequest" });
			jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			JSONArray ja = JSONArray.fromObject(list, jsonConfig);

			jsResp.put("list", ja);
			result = jsResp.toString();
		} catch (NumberFormatException e) {
			result = "-2";
			e.printStackTrace();
		}

		result = result.replaceAll("/big/", "/small/");// 将原图转化为缩略图

		System.out.println("getLatestByPage-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	public String getHotByPage() {
		System.out.println("getHotByPage-page:" + page);
		String result = "-1";
		JSONObject jsResp = new JSONObject();

		try {

			List<BusinessService> list = businessServiceService.getHotByPage(page);

			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setExcludes(new String[] { "canServiceDay", "category", "exchange", "finishedPeople",
					"favoritePeople", "sign", "greatPeople", "location_x", "location_y", "serviceCity", "serviceTime",
					"userId", "address", "age", "birthday", "concernedPeople", "favoriteServices", "hobby",
					"homePageBackgroundImage", "job", "latestLocation_x", "latestLocation_y", "password", "photos",
					"rank", "rank_credit", "receivedRequest", "registCheckCode", "registTime", "services",
					"todoRequest", "userComment", "userRequest" });
			jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			JSONArray ja = JSONArray.fromObject(list, jsonConfig);

			jsResp.put("list", ja);
			result = jsResp.toString();
		} catch (NumberFormatException e) {
			result = "-2";
			e.printStackTrace();
		}

		result = result.replaceAll("/big/", "/small/");// 将原图转化为缩略图

		System.out.println("getHotByPage-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	/***************************/
	public BusinessServiceService getBusinessServiceService() {
		return businessServiceService;
	}

	public void setBusinessServiceService(BusinessServiceService businessServiceService) {
		this.businessServiceService = businessServiceService;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public BusinessService getBusinessService() {
		return businessService;
	}

	public void setBusinessService(BusinessService businessService) {
		this.businessService = businessService;
	}

	public int getServiceId() {
		return serviceId;
	}

	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getJsonObj() {
		return jsonObj;
	}

	public void setJsonObj(String jsonObj) {
		this.jsonObj = jsonObj;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public File getImage() {
		return image;
	}

	public void setImage(File image) {
		this.image = image;
	}

	public Set<File> getImages() {
		return images;
	}

	public void setImages(Set<File> images) {
		this.images = images;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

}
