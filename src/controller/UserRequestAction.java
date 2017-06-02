package controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.apache.struts2.interceptor.SessionAware;

import service.UserCommentService;
import service.UserRequestService;
import service.UserService;
import util.BaiChuanUtils;
import util.CheckUtil;
import util.MsgUtil;
import util.TimeUtil;

import com.opensymphony.xwork2.ActionSupport;

import domain.User;
import domain.UserComment;
import domain.UserRequest;

public class UserRequestAction extends ActionSupport implements SessionAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2716175830433628548L;
	private InputStream inputStream;// = new
									// ByteArrayInputStream("Never init...".getBytes());
	private UserService userService;
	private UserRequestService userRequestService;
	private UserCommentService userCommentService;

	private Map<String, Object> session;

	// 直接接收nameValuePair
	private UserRequest userRequest = new UserRequest();

	// 接收json
	private String jsonObj;

	// 接收requestId
	private String requestId;

	private String username;

	private String commentId;

	private int page;
	private int userId;
	private int status;

	public String save() {
		System.out.println("user's id :" + userRequest.getUser().getId());
		System.out.println("id:" + userRequest.getId());
		System.out.println("endAddredd:" + userRequest.getEndAddress());
		System.out.println("type:" + userRequest.getType());
		System.out.println("pic:" + userRequest.getRequest_picture());
		System.out.println("startLocation_x:" + userRequest.getStartLocation_x());
		int status = -1;

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			status = -6;// 未登录
			System.out.println("reminder-result:" + status);
			inputStream = MsgUtil.sendString(status + "");
			return SUCCESS;
		}

		boolean hasIllegalWords = CheckUtil.hasIllegalWords(userRequest.getRequest_content());

		if (hasIllegalWords) {
			status = -3;

			// 加积分
			try {
				userService.addRank_Credit(userRequest.getUser().getId(), -50);
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("save-status:" + status);
			inputStream = MsgUtil.sendString(status + "");
			return SUCCESS;
		}

		try {

			User u = userService.getById(userRequest.getUser().getId());
			userRequest.setUser(u);

			boolean isSucceed = userRequestService.save(userRequest);
			if (isSucceed) {
				status = 1;
				// 给好友发通知
				try {
					UserRequest ur = userRequestService.getNewestRequestByUsername(u.getUsername());
					if (ur != null && u.getConcernedPeople() != null) {
						Set<User> friends = u.getConcernedPeople();
						System.out.println("size:" + friends.size());
						Iterator<User> it = friends.iterator();
						while (it.hasNext()) {
							User f = it.next();
							String context = "有人@你，您的好友又有新需求啦，点击查看";
							String detail = "您关注的" + u.getNickName() + "新发了 一条需求，别忘了去看看！";
							String extra = userRequest.getId() + "";
							BaiChuanUtils.pushJson(f.getId(), BaiChuanUtils.CONCERNED_PEOPLE_REQUEST, context, detail,
									extra);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			status = -2;
			e.printStackTrace();
		}

		System.out.println("save-result:" + status);
		inputStream = MsgUtil.sendString(status + "");
		return SUCCESS;
	}

	public String getBriefByUserIdAndType() {
		System.out.println("getByUserIdAndType-userId:" + userId);
		System.out.println("getByUserIdAndType-status:" + status);
		String result = "-1";

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("reminder-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		List<UserRequest> list = userRequestService.getByUserIdAndType(page, userId, status);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray jsonArray = JSONArray.fromObject(list, jsonConfig);
		JSONObject jsResp = new JSONObject();
		result = jsonArray.toString();

		jsResp.put("list", result);

		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	public String delete() {
		int status = -1;
		String result = "-1";

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("reminder-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		System.out.println("delete-requestId:" + requestId);
		try {
			int reqId = Integer.parseInt(requestId);

			UserRequest ur = userRequestService.getUserRequestById(reqId);
			if (ur == null) {
				status = -3;// 不存在
			} else {
				if (ur.getStatus() == 1) {
					status = -4;// 任务正在进行中，不能删除
				} else {
					UserComment userComment = ur.getUserComment();
					if (userComment != null) {
						userCommentService.update(userComment);
						userCommentService.delete(userComment);
					}
					List<User> list = userRequestService.getReceiversByReqId(reqId);
					if (list != null) {
						for (User u : list) {
							Set<UserRequest> set = u.getReceivedRequest();
							set.remove(ur);
							u.setReceivedRequest(set);
							userService.update(u);
						}
					}
					Set<User> set = ur.getReceivers();
					set.removeAll(set);

					System.out.println("deleting user who has signed up.");
					ur.setUserComment(null);
					ur.setReceivers(null);
					userRequestService.update(ur);
					boolean flag = userRequestService.delete(ur);

					if (flag) {
						status = 1;// 删除完成
					} else {
						status = -1;
					}

				}
			}

		} catch (Exception e) {
			status = -2;
			e.printStackTrace();
		}
		inputStream = MsgUtil.sendString(status + "");

		return SUCCESS;
	}

	// 用户催单
	public String reminder() {
		System.out.println("reminder-requestId:" + requestId);
		System.out.print("reminder-userId:" + userId);
		String result = "-1";

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("reminder-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		int reqId = Integer.parseInt(requestId);
		UserRequest userRequest = userRequestService.getUserRequestById(reqId);

		if (userRequest == null) {
			result = "-4";// 不存在此订单
			System.out.println("reminder-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		if (userRequest.getUser().getId() != userId) {
			result = "-3";// 不是此人的订单，不能催单
			System.out.println("reminder-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		User u = new User();
		if (userRequest.getFinalReceiver() != null) {
			u = userRequest.getFinalReceiver();
		} else {
			u = userRequestService.getFinalReceiverByReqId(reqId);
		}

		try {
			String context = "[用户催单]您的需求交易对象催单啦，请及时处理！";
			String detail = "[用户催单]用户您好，您的需求"
					+ (userRequest.getRequest_content().length() > 6 ? userRequest.getRequest_content().substring(0, 6)
							: userRequest.getRequest_content()) + "... ，买家 " + userRequest.getUser().getNickName()
					+ "于" + TimeUtil.convertTo_yyyyMMdd(System.currentTimeMillis() + "") + " 进行了催单,请及时处理。";
			BaiChuanUtils.pushJson(u.getId(), BaiChuanUtils.BOUGHT_USERGETSERVICE, context, detail);
			result = "1";
		} catch (ParseException e) {
			e.printStackTrace();
			result = "-2";
		} catch (Exception e) {
			e.printStackTrace();
			result = "-2";
		}

		System.out.println("reminder-result:" + result);
		inputStream = MsgUtil.sendString(result);
		return SUCCESS;

	}

	/**
	 * username
	 * 
	 * 得到最近一次发的需求
	 * 
	 * @return
	 */
	public String getLatest() {
		String result = "-1";

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("reminder-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		UserRequest u = userRequestService.getNewestRequestByUsername(Long.parseLong(username));
		System.out.println("getLatest-userId:" + u.getId());
		inputStream = MsgUtil.sendString("id:" + u.getId());
		return SUCCESS;
	}

	/**
	 * 举报 requestId
	 * 
	 * @return
	 */
	public String report() {
		int status = -1;
		String result = "-1";
		System.out.println("report-requestId:" + requestId);

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("reminder-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		try {
			int reqId = Integer.parseInt(requestId);
			boolean flag = userRequestService.addReport(reqId);
			UserRequest userRequest = userRequestService.getUserRequestById(reqId);
			if (userRequest == null || userRequest.getStatus() == 4) {
				status = -3;// 该需求已删除
			} else if (flag) {
				status = 1;
				if (userRequest.getReport() > 3) {
					JSONObject js = new JSONObject();
					String context = "[需求被举报]您的需求已被删除，点击查看";
					String detail = "[需求被举报]您的需求" + userRequest.getRequest_content().substring(0, 6)
							+ "...已被用户举报，该条需求已被强制删除";
					BaiChuanUtils.pushJson(userRequest.getUser().getId(), BaiChuanUtils.NOTICE_WITHOUT_REDIRECT,
							context, detail);
					userRequest.setStatus(4);
					userRequestService.update(userRequest);
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			status = -2;
		}

		System.out.println("report-status:" + status);
		inputStream = MsgUtil.sendString(status + "");
		return SUCCESS;
	}

	// 得到所有关于此用户的需求
	/**
	 * TODO 改写！！！对服务器造成压力很大！
	 * 
	 * @return
	 */
	public String getRequests() {
		String result = "failed";
		long id = 0;

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("reminder-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		try {
			System.out.println("getRequests-username:" + username);
			id = Long.parseLong(username);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}
		List<UserRequest> toDoRequest = userRequestService.getUserToDoRequests(id);
		List<UserRequest> receivedRequest = userRequestService.getUserReceivedRequest(id);
		List<UserRequest> userRequest = userRequestService.getUsersRequests(id);

		JSONObject jsResp = new JSONObject();
		// 使用setCycleDetectionStrategy防止自包含
		// 即防止出现There is a circle in 。。。
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String[] { "receivers" });

		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

		JSONArray toDoRequestJs = JSONArray.fromObject(toDoRequest, jsonConfig);
		JSONArray receivedRequestJs = JSONArray.fromObject(receivedRequest, jsonConfig);
		JSONArray userRequestJs = JSONArray.fromObject(userRequest, jsonConfig);

		jsResp.put("toDoRequest", toDoRequestJs);
		jsResp.put("receivedRequest", receivedRequestJs);
		jsResp.put("userRequest", userRequestJs);

		result = jsResp.toString();

		result = result.replaceAll("/big/", "/small/");// 将原图转化为缩略图

		System.out.println("getRequests-jsResp:" + jsResp);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;
	}

	public String getRelated() {
		String result = "-1";

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("reminder-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		try {
			System.out.println("getRelated-userId:" + userId);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		List<UserRequest> list = userRequestService.getRelatedById(userId);

		JSONObject jsResp = new JSONObject();
		// 使用setCycleDetectionStrategy防止自包含
		// 即防止出现There is a circle in 。。。
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String[] { "receivers", "concernedPeople", "favoriteServices", "photos",
				"receivedRequest", "services", "todoRequest", "userComment", "userRequest" });

		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

		JSONArray ja = JSONArray.fromObject(list, jsonConfig);

		jsResp.put("list", ja);

		result = jsResp.toString();

		result = result.replaceAll("/big/", "/small/");// 将原图转化为缩略图

		System.out.println("getRelated-jsResp:" + jsResp);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;
	}

	// 得到所有与此用户相关的内容
	public String getRelatedByPage() {
		String result = "-1";

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("reminder-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		try {
			System.out.println("getRelated-userId:" + userId);
			System.out.println("getRelated-page:" + page);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		List<UserRequest> list = userRequestService.getRelatedById(userId, page);

		JSONObject jsResp = new JSONObject();
		// 使用setCycleDetectionStrategy防止自包含
		// 即防止出现There is a circle in 。。。
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String[] { "receivers", "user" });

		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

		JSONArray ja = JSONArray.fromObject(list, jsonConfig);

		jsResp.put("list", ja);

		result = jsResp.toString();

		result = result.replaceAll("/big/", "/small/");// 将原图转化为缩略图

		System.out.println("getRelated-jsResp:" + jsResp);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;
	}

	public String getAllByUserId() {
		String result = "-1";
		System.out.println("getAllByUserId-page:" + page);
		System.out.println("getAllByUserId-userId:" + userId);

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("reminder-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		List<UserRequest> list = userRequestService.getAllByUserId(userId, page);
		JSONObject jsResp = new JSONObject();
		// 使用setCycleDetectionStrategy防止自包含
		// 即防止出现There is a circle in 。。。
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String[] { "receivers", "finalReceiver", "user", "endLocation_x", "endLocation_y",
				"startLocation_x", "startLocation_y", "status", "report", "rank_credit", "nickName", "headIcon",
				"gender", "friendsNotify", "endLocation_remark", "startAddress", "userComment", "username",
				"endAddress", "request_limitTime", "request_endTime", "startLocation_remark" });
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray ja = JSONArray.fromObject(list, jsonConfig);
		jsResp.put("list", ja);
		result = jsResp.toString();

		System.out.println("getAllByUserId-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;
	}

	// 提交完成订单，请发单者确认
	public String pushFinish() {
		String result = "failed";
		int status = -1;

		System.out.println("pushFinish-username:" + username);
		System.out.println("pushFinish-requestId:" + requestId);

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("reminder-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		long username = Long.parseLong(this.username);
		int requestId = Integer.parseInt(this.requestId);

		UserRequest userRequest = userRequestService.getUserRequestById(requestId);
		try {
			if (userRequest.getFinalReceiver() != null) {
				if (userRequest.getFinalReceiver().getUsername() != username) {
					status = -3; // 未接此单
				} else {
					userRequest.setStatus(3);
					userRequestService.update(userRequest);
					status = 1;
					String context = "[需求完成]亲，你发布的需求已经完成啦，点击查看";
					String detail = "[需求完成]亲，您与" + userRequest.getFinalReceiver().getNickName() + "在"
							+ TimeUtil.convertTo_yyyyMMdd(userRequest.getRequest_postTime())
							+ "达成的交易已经完成，请及时核验！有关更多服务的细节，请查收《服务协议》";
					BaiChuanUtils.pushJson(userRequest.getUser().getId(), BaiChuanUtils.MY_REQUEST, context, detail);
				}
			}
		} catch (Exception e) {
			status = -2;
			e.printStackTrace();
		}
		result = status + "";

		System.out.println("pushFinish-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;
	}

	/**
	 * 根据需求类型获取相应需求
	 * 
	 * 已精简
	 * 
	 * @return
	 */
	public String getLocalByType() {
		String result = "failed";
		JSONObject js = JSONObject.fromObject(jsonObj);
		System.out.println("getLocalByType-jsonObj:" + js.toString());

		int page = 1;
		double x = 0;
		double y = 0;
		int type = 1;
		try {
			page = Integer.parseInt(js.getString("page"));
			x = Double.parseDouble(js.getString("x"));
			y = Double.parseDouble(js.getString("y"));
			type = Integer.parseInt(js.getString("type"));
		} catch (Exception e) {
			inputStream = MsgUtil.sendString(result);
			e.printStackTrace();
		}
		double scope = 10000.0;
		List<UserRequest> list = userRequestService.getRequestByType(x, y, scope, page, type);
		if (list != null) {
			JsonConfig jsConfig = new JsonConfig();
			jsConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			jsConfig.setExcludes(new String[] { "userComment", "receivers", "finalReceiver", "password", "endAddress",
					"endLocation_remark", "endLocation_x", "endLocation_y", "friendsNotify", "report",
					"request_endTime", "request_limitTime", "startAddress", "startLocation_remark", "address", "age",
					"birthday", "concernedPeople", "favoriteServices", "hobby", "homePageBackgroundImage", "job",
					"latestLocation_x", "latestLocation_y", "photos", "receivedRequest", "registCheckCode",
					"registTime", "services", "sign", "todoRequest", "userRequest", "" });
			JSONArray listObj = JSONArray.fromObject(list, jsConfig);
			JSONObject jsResp = new JSONObject();
			jsResp.put("list", listObj);
			result = jsResp.toString();

			result = result.replaceAll("/big/", "/small/");// 将原图转化为缩略图

			System.out.println("getLocalByType-result:" + result);
		}

		inputStream = MsgUtil.sendString(result);
		return SUCCESS;
	}

	/**
	 * 接单 username，requestId
	 * 
	 * @return
	 */
	public String takeOrder() {
		String result = "failed";
		int status = -1;

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("reminder-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		System.out.println("takeOrder-username:" + username);
		System.out.println("takeOrder-requestId:" + requestId);

		long username = Long.parseLong(this.username);
		int requestId = Integer.parseInt(this.requestId);

		UserRequest userRequest = userRequestService.getUserRequestById(requestId);
		if (userRequest.getFinalReceiver() != null) {
			status = -3; // 已经有人接单
		} else {
			try {
				if (username == userRequest.getUser().getUsername()) {
					status = -4;// 不能自己接自己的单!
				} else {
					User finalReceiver = userService.findByUsername(username);
					userRequest.setFinalReceiver(finalReceiver);
					userRequest.setStatus(1);
					userRequestService.update(userRequest);
					status = 1;
					try {
						// 给那些已经报名但是没选为接单的用户发推送。
						String context = "[接单啦]恭喜！需求方已选择您接单！点击查看！";
						String detail = "[接单啦]恭喜！需求方已选择您接 " + userRequest.getRequest_content().substring(0, 6)
								+ "...这个单子，赶紧去查看详情吧！";
						BaiChuanUtils.pushJson(finalReceiver.getId(), BaiChuanUtils.HIS_REQUEST, context, detail);
						List<User> list = userRequestService.getReceiversByReqId(requestId);
						for (User u : list) {
							if (u.getUsername() != username) {
								context = "[接单失败]亲，橙子" + userRequest.getUser().getNickName() + "...已被他人接单，点击查看详情";
								detail = "[接单失败]亲，需求"
										+ (userRequest.getRequest_content().length() > 6 ? userRequest
												.getRequest_content().substring(0, 6) : userRequest
												.getRequest_content()) + "...已被他人接单，赶快去寻找其他订单吧";
								BaiChuanUtils.pushJson(u.getId(), BaiChuanUtils.HIS_REQUEST, context, detail);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			} catch (Exception e) {
				status = -2;
				e.printStackTrace();
			}
		}
		result = status + "";

		System.out.println("takeOrder-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;
	}

	// 报名
	public String signUp() {
		String result = "failed";

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("signUp-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		JSONObject js = JSONObject.fromObject(jsonObj);
		String name = js.getString("username");
		String requestId = js.getString("requestId");

		System.out.println("signUp-jsonObj:" + jsonObj);
		int status = -1;
		List<User> list = null;
		try {
			int userReqeustId = Integer.parseInt(requestId);
			long username = Long.parseLong(name);

			UserRequest userRequest = userRequestService.getUserRequestById(userReqeustId);
			if (userRequest.getUser().getUsername() == username) {
				status = -4;// 不能自己报自己的名！
			} else {
				list = userRequestService.getReceiversByReqId(userReqeustId);
				boolean flag = false;
				for (User u : list) {
					System.out.println(u.getUsername() + "," + u.getId());
					if (u.getUsername() == username) {
						flag = true;
						break;
					}
				}
				if (flag) {// 如果已经报名
					status = -3;
				} else {
					if (list.size() < 8) {
						// 能报名，则保存
						UserRequest ur = userRequestService.getUserRequestById(userReqeustId);
						Set<User> receivers = ur.getReceivers();
						System.out.println("r1:" + receivers.size());
						User u = userService.findByUsername(username);
						receivers.add(u);
						Set<UserRequest> receivedRequests = u.getReceivedRequest();
						receivedRequests.add(ur);
						ur.setReceivers(receivers);
						u.setReceivedRequest(receivedRequests);

						System.out.println("r2:" + ur.getReceivers().size());
						userRequestService.update(ur);
						userService.update(u);

						list.add(u);

						try {
							// 发推送
							String context = "[需求报名]您发的需求有人报名！点击查看";
							String detail = "[需求报名]您的需求"
									+ (userRequest.getRequest_content().length() > 6 ? userRequest.getRequest_content()
											.substring(0, 6) : userRequest.getRequest_content()) + "...有用户 "
									+ u.getNickName() + "报名了！";
							BaiChuanUtils.pushJson(ur.getUser().getId(), BaiChuanUtils.MY_REQUEST, context, detail);
						} catch (Exception e) {
							e.printStackTrace();
						}

						status = 1;
					}
				}
			}
		} catch (Exception e) {
			status = -2;
			list = null;
			e.printStackTrace();
		}
		JSONObject jsResp = new JSONObject();
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.setExcludes(new String[] { "receivedRequest", "concernedPeople", "todoRequest", "userRequest",
				"userComment", "favoriteServices", "services" });
		JSONArray listJs = JSONArray.fromObject(list, jsonConfig);
		jsResp.put("status", status);
		jsResp.put("list", listJs);

		result = jsResp.toString();
		System.out.println("signUp-result:" + result);
		inputStream = MsgUtil.sendString(result);
		return SUCCESS;
	}

	// 取消报名
	public String cancelSign() {
		String result = "failed";

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("reminder-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		JSONObject js = JSONObject.fromObject(jsonObj);
		String name = js.getString("username");
		String reqId = js.getString("requestId");

		System.out.println("cancelSign-jsonObj:" + jsonObj);
		int status = -1;
		List<User> list = null;
		boolean flag = false;
		try {
			long username = Long.parseLong(name);
			int requestId = Integer.parseInt(reqId);
			User u = userService.findByUsername(username);
			UserRequest ur = userRequestService.getUserRequestById(requestId);
			Set<UserRequest> uSet = u.getReceivedRequest();
			Iterator<UserRequest> it = uSet.iterator();
			while (it.hasNext()) {
				UserRequest urt = it.next();
				if (urt.getId() == requestId) {
					flag = true;
				}
			}
			if (flag) {// 如果存在此报名
				uSet.remove(ur);
				u.setReceivedRequest(uSet);
				Set<User> urSet = ur.getReceivers();
				urSet.remove(u);
				ur.setReceivers(urSet);
				userService.update(u);
				userRequestService.update(ur);
				status = 1;

			} else {
				status = -3;
			}
		} catch (Exception e) {
			e.printStackTrace();
			status = -2;
		}
		result = status + "";
		System.out.println("cancelSign-result:" + result);
		inputStream = MsgUtil.sendString(result);
		return SUCCESS;
	}

	// 完成订单
	public String finishRequest() {
		String result = "failed";
		int status = -1;
		System.out.println("finishRequest-jsonObj:" + jsonObj);
		JSONObject js = JSONObject.fromObject(jsonObj);

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("reminder-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		try {
			int reqId = js.getInt("requestId");
			int isFinish = js.getInt("status");
			UserRequest userRequest = userRequestService.getUserRequestById(reqId);
			if (userRequest == null) {
				status = -4;// 没有此需求
			} else {
				if (userRequest.getStatus() == 2) {
					status = -3;// 需求已完成
				} else {
					if (isFinish == 0) {// 驳回
						userRequest.setStatus(1);
						userRequestService.update(userRequest);
						status = -5;
						try {
							String context = "[需求驳回]您完成的需求被驳回，点击查看.";
							String detail = "[需求驳回]您完成的需求"
									+ (userRequest.getRequest_content().length() > 6 ? userRequest.getRequest_content()
											.substring(0, 6) : userRequest.getRequest_content()) + "...被驳回。";
							BaiChuanUtils.pushJson(userRequest.getFinalReceiver().getId(), BaiChuanUtils.HIS_REQUEST,
									context, detail);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (isFinish == 1) {
						userRequest.setStatus(2);
						userRequestService.update(userRequest);
						status = 1;
						try {
							String context = "[需求完成]您的一个需求已完成！点击查看";
							String detail = "[需求完成]亲，您与" + userRequest.getFinalReceiver().getNickName()
									+ " 达成的需求交易已经完成，请及时核验哦！有关更多服务细节，请查看《服务协议》";
							BaiChuanUtils.pushJson(userRequest.getUser().getId(), BaiChuanUtils.MY_REQUEST, context,
									detail);
						} catch (Exception e) {
							e.printStackTrace();
						}
						// 加积分
						try {
							userService.addRank_Credit(userRequest.getFinalReceiver().getId(), 5);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (NumberFormatException e) {
			status = -2;
			e.printStackTrace();
		}
		result = status + "";

		System.out.println("finishRequest-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;
	}

	public String getLocal() {
		String result = "failed";
		JSONObject js = JSONObject.fromObject(jsonObj);
		System.out.println(js.toString());
		int page = 1;
		double x = 0;
		double y = 0;
		try {
			page = Integer.parseInt(js.getString("page"));
			x = Double.parseDouble(js.getString("x"));
			y = Double.parseDouble(js.getString("y"));
		} catch (Exception e) {
			inputStream = MsgUtil.sendString(result);
			e.printStackTrace();
		}
		double scope = 1000;
		List<UserRequest> list = userRequestService.findLocalRequest(x, y, scope, page);
		if (list != null) {
			JsonConfig jsConfig = new JsonConfig();
			jsConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			jsConfig.setExcludes(new String[] { "userComment", "receivers", "finalReceiver", "password" });
			JSONArray listObj = JSONArray.fromObject(list, jsConfig);
			JSONObject jsResp = new JSONObject();
			jsResp.put("list", listObj);
			result = jsResp.toString();
		}
		result = result.replaceAll("/big/", "/small/");// 将原图转化为缩略图

		System.out.println("getLocal:" + result);
		inputStream = MsgUtil.sendString(result);
		return SUCCESS;
	}

	/**
	 * 已精简
	 * 
	 * @return
	 */
	public String getByCommentId() {
		int id = -1;
		String result = "-1";
		System.out.println("commentId:" + commentId);

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("reminder-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		try {
			id = Integer.parseInt(commentId);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			inputStream = MsgUtil.sendString("failed");
			return SUCCESS;
		}
		UserRequest userRuquest = userRequestService.getUserRequestByComId(id);
		if (userRuquest == null) {
			result = "failed";
		} else {
			if (userRuquest.getUserComment() != null) {
				userRuquest.getUserComment().setUser(null);
			}

			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			jsonConfig.setExcludes(new String[] { "address", "password", "concernedPeople", "photos",
					"receivedRequest", "age", "birthday", "favoriteServices", "hobby", "homePageBackgroundImage",
					"job", "latestLocation_x", "latestLocation_y", "registCheckCode", "registTime", "services",
					"friendsNotify", "sign", "verifyStatus", "todoRequest", "userRequest", "receivers", "distance",
					"endAddress", "endLocation_remark", "endLocation_x", "endLocation_y", "report", "request_endTime",
					"request_key", "request_limitTime", "request_picture", "request_picture2", "request_picture3",
					"request_postTime", "startAddress", "startLocation_remark", "startLocation_x", "startLocation_y",
					"status", "type" });
			JSONObject userRequestJs = JSONObject.fromObject(userRuquest, jsonConfig);
			JSONObject jsResp = new JSONObject();
			jsResp.put("userRequest", userRequestJs);
			result = jsResp.toString();
		}
		System.out.println("getByCommentId-result:" + result);
		inputStream = MsgUtil.sendString(result);
		return SUCCESS;
	}

	public String getById() {
		int id = -1;
		String result = "-1";
		System.out.println("getById-jsonObj:" + jsonObj);

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("getById-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		JSONObject js = JSONObject.fromObject(jsonObj);
		try {
			id = Integer.parseInt(js.getString("requestId"));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			inputStream = MsgUtil.sendString("failed");
			return SUCCESS;
		}
		UserRequest userRuquest = userRequestService.getUserRequestById(id);
		if (userRuquest == null) {
			result = "failed";
		} else {
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			jsonConfig.setExcludes(new String[] { "password", "concernedPeople", "photos", "receivedRequest",
					"todoRequest", "receivers", "friendsNotify", "img1", "img2", "img3", "address", "birthday",
					"favoriteServices", "homePageBackgroundImage", "hobby", "homePageBackgroundImage", "iconfile",
					"job", "latestLocation_x", "latestLocation_y", "registCheckCode", "services", "userComment",
					"userRequest" });
			JSONObject userRequestJs = JSONObject.fromObject(userRuquest, jsonConfig);
			JSONObject jsResp = new JSONObject();
			jsResp.put("userRuquest", userRequestJs);
			result = jsResp.toString();
		}
		System.out.println("getById-result:" + result);
		inputStream = MsgUtil.sendString(result);
		return SUCCESS;
	}

	public String getByPage() {
		String result;
		List<UserRequest> list = userRequestService.getUserRequestByPage(page);
		if (list == null) {
			result = "failed";
		} else {
			JsonConfig jsConfig = new JsonConfig();
			jsConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			jsConfig.setExcludes(new String[] { "userComment", "receivers", "finalReceiver", "password" });
			JSONArray listObj = JSONArray.fromObject(list, jsConfig);
			JSONObject jsResp = new JSONObject();
			jsResp.put("list", listObj);
			result = jsResp.toString();
			result = result.replaceAll("/big/", "/small/");// 将原图转化为缩略图
		}

		inputStream = MsgUtil.sendString(result);
		return SUCCESS;
	}

	public String getMyRequest() {
		User user = (User) session.get("user");
		String result = "-1";

		if (user == null) {
			result = "-6";// 未登录
			System.out.println("reminder-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		if (user != null) {
			Map<String, Object> infoMap = userService.getMyRequest(user);
			Set<UserRequest> userRequests = (Set<UserRequest>) infoMap.get("userRequest");
			JSONArray ja = JSONArray.fromObject(userRequests);
			result = ja.toString();
			result = result.replaceAll("/big/", "/small/");// 将原图转化为缩略图
		} else {
			result = "-1";
		}
		inputStream = new ByteArrayInputStream(result.getBytes());
		return SUCCESS;
	}

	/**
	 * 得到用户的报名详情
	 * 
	 * @return
	 */
	public String getReceivedUsersByReqId() {
		int id = -1;
		String result = "-1";
		System.out.println("getReceivedUsersByReqId-requestId:" + requestId);

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("reminder-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		try {
			id = Integer.parseInt(requestId);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			inputStream = MsgUtil.sendString("failed");
			return SUCCESS;
		}
		List<User> list = userRequestService.getReceiversByReqId(id);
		if (list == null) {
			result = "failed";
		} else {
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setExcludes(new String[] { "address", "password", "concernedPeople", "photos",
					"receivedRequest", "age", "birthday", "favoriteServices", "hobby", "homePageBackgroundImage",
					"job", "latestLocation_x", "latestLocation_y", "registCheckCode", "registTime", "services",
					"friendsNotify", "sign", "verifyStatus", "todoRequest", "userRequest", "receivers", "distance",
					"endAddress", "endLocation_remark", "endLocation_x", "endLocation_y", "report", "request_endTime",
					"request_key", "request_limitTime", "request_picture", "request_picture2", "request_picture3",
					"request_postTime", "startAddress", "startLocation_remark", "startLocation_x", "startLocation_y",
					"status", "type", "iconfile", "userComment" });
			jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			JSONArray receiversJa = JSONArray.fromObject(list, jsonConfig);
			JSONObject jsResp = new JSONObject();
			jsResp.put("receivers", receiversJa);
			result = jsResp.toString();
		}

		System.out.println("getReceivedUsersByReqId-reveivers:" + result);
		inputStream = MsgUtil.sendString(result);
		return SUCCESS;
	}

	/**********************************/
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public UserRequestService getUserRequestService() {
		return userRequestService;
	}

	public void setUserRequestService(UserRequestService userRequestService) {
		this.userRequestService = userRequestService;
	}

	public UserRequest getUserRequest() {
		return userRequest;
	}

	public void setUserRequest(UserRequest userRequest) {
		this.userRequest = userRequest;
	}

	public String getJsonObj() {
		return jsonObj;
	}

	public void setJsonObj(String jsonObj) {
		this.jsonObj = jsonObj;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public UserCommentService getUserCommentService() {
		return userCommentService;
	}

	public void setUserCommentService(UserCommentService userCommentService) {
		this.userCommentService = userCommentService;
	}

	public String getCommentId() {
		return commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setPage(int page) {
		this.page = page;
	}

}
