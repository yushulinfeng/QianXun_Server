package controller;

import java.io.InputStream;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.apache.struts2.interceptor.SessionAware;

import service.BusinessServiceService;
import service.UserAccountService;
import service.UserGetServiceService;
import service.UserService;
import util.BaiChuanUtils;
import util.MsgUtil;
import util.TimeUtil;

import com.opensymphony.xwork2.ActionSupport;

import domain.BusinessService;
import domain.User;
import domain.UserAccount;
import domain.UserGetService;

public class UserGetServiceAction extends ActionSupport implements SessionAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6364476277437431662L;
	private InputStream inputStream;
	private Map<String, Object> session;

	private UserGetServiceService userGetServiceService;
	private BusinessServiceService businessServiceService;
	private UserAccountService userAccountService;
	private UserService userService;

	private UserGetService userGetService;
	private int ugsId;
	private String status;

	private int page;
	private int userId;

	private int isSucceed;
	private String reason;

	/**
	 * 用户请求预约
	 * 
	 * @return
	 */
	public String save() {
		System.out.println("save-userId:" + userGetService.getUser().getId());
		System.out.println("save-serviceId:" + userGetService.getBusinessService().getId());

		String result = "-1";

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("save-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		try {
			int serviceId = userGetService.getBusinessService().getId();

			BusinessService bs = businessServiceService.getByBServiceId(serviceId);
			if (bs == null) {
				result = "-4"; // 服务不存在
				System.out.println("save-result" + result);
				inputStream = MsgUtil.sendString(result);
				return SUCCESS;
			}

			if (bs.getUser() != null) {
				if (userGetService.getUser().getId() == bs.getUser().getId()) {
					result = "-5";// 不能接自己的单！
					System.out.println("save-result" + result);
					inputStream = MsgUtil.sendString(result);
					return SUCCESS;
				}
			}

			if (bs.getStatus() != 0) {// 服务能用
				result = "-3"; // 服务是否能用
				System.out.println("save-result" + result);
				inputStream = MsgUtil.sendString(result);
				return SUCCESS;
			}
			int flag = userGetServiceService.save(userGetService);
			if (flag != -1) {
				result = String.valueOf(flag);
			} else {
				result = "-1";
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = "-2";
		}

		System.out.println("save-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;
	}

	/**
	 * 确认付款
	 * 
	 * @return
	 */
	public String confirmPayed() {
		System.out.println("confirmPayed-userId:" + userId);
		System.out.println("confirmPayed-ugsId:" + ugsId);
		System.out.println("confirmPayed-isSucceed:" + isSucceed);

		String result = "-1";

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("confirmPayed-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		try {

			if (isSucceed != 1) {// 不等于1,即不成功,donothing
				result = "1";
				System.out.println("confirmPayed-result" + result);
				inputStream = MsgUtil.sendString(result);
				return SUCCESS;
			}

			UserGetService ugs = userGetServiceService.getByUgServiceId(ugsId);

			if (ugs == null) {
				result = "-4"; // 订单不存在
				System.out.println("confirmPayed-result" + result);
				inputStream = MsgUtil.sendString(result);
				return SUCCESS;
			}

			if (ugs.getStatus() != UserGetService.WAITING_PAY_STATUS) {// 服务能用
				result = "-3"; // 服务是否能用
				System.out.println("confirmPayed-result" + result);
				inputStream = MsgUtil.sendString(result);
				return SUCCESS;
			}
			ugs.setStatus(UserGetService.PAY_FINISH_WAITING_STATUS);
			boolean flag = userGetServiceService.update(ugs);
			if (flag) {
				result = "1";
			} else {
				result = "-1";
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = "-2";
		}

		System.out.println("save-result" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;
	}

	/**
	 * 用户取消订单
	 * 
	 * @return
	 */
	public String delete() {
		System.out.println("save-userId:" + userId);
		System.out.println("save-ugsId:" + ugsId);

		String result = "-1";

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("delete-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		try {

			UserGetService ugs = userGetServiceService.getByUgServiceId(ugsId);
			if (ugs == null) {
				result = "-4"; // 服务不存在
				System.out.println("save-result" + result);
				inputStream = MsgUtil.sendString(result);
				return SUCCESS;
			}

			if (ugs.getStatus() == 0) {// 服务不能用
				result = "-3"; // 服务不能用
				System.out.println("save-result" + result);
				inputStream = MsgUtil.sendString(result);
				return SUCCESS;
			}
			boolean flag = userGetServiceService.delete(userGetService);
			if (flag) {
				UserAccount ua = userAccountService.getById(ugs.getUser().getId());
				double total = ua.getBalance() + ugs.getPrice();
				ua.setBalance(total);
				boolean addMoney = userAccountService.update(ua);
				if (addMoney) {
					result = "1"; // 成功

					// 加积分
					try {
						userService.addRank_Credit(ugs.getUser().getId(), -20);
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {// addMoney失败，重新设置
					ugs.setStatus(UserGetService.APPLY_REFUND_STATUS);
					userGetServiceService.update(ugs);
					userGetServiceService.save(userGetService);// 让userGet复原
					result = "-1";
				}

			} else {
				result = "-1";
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = "-2";
		}

		System.out.println("save-result" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	/**
	 * 商家同意预约，订单开始
	 * 
	 * @return
	 */
	public String takeOrder() {
		System.out.println("takerOrder-ugsId:" + ugsId);
		String result = "-1";

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("takeOrder-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		try {
			UserGetService ugs = userGetServiceService.getByUgServiceId(ugsId);

			if (ugs.getStatus() == UserGetService.PAY_FINISH_WAITING_STATUS) {
				ugs.setStatus(UserGetService.START_STATUS);
				String curtime = TimeUtil.currentTime();
				ugs.setStartTime(curtime);
				ugs.setStatusTime(curtime);
				boolean flag = userGetServiceService.update(ugs);
				if (flag) {
					result = "1"; // 成功
					// 给买家发通知
					try {
						User u = new User();
						if (ugs.getBusinessService().getUser() == null) {
							u = ugs.getBusinessService().getUser();
						} else {
							u = businessServiceService.getBSsUser(ugs.getBusinessService().getId());
						}
						String context = ugs.getUser().getNickName() + "您好，您在"
								+ TimeUtil.convertTo_yyyyMMdd(ugs.getStartTime()) + "预定的服务已有回复，请及时查收";
						String detail = ugs.getUser().getNickName() + "您好，"
								+ ugs.getBusinessService().getUser().getNickName() + "已同意您在"
								+ TimeUtil.convertTo_yyyyMMdd(ugs.getStartTime()) + "预定的"
								+ ugs.getBusinessService().getName() + "服务，如有特殊要求，请及时联系商家";
						BaiChuanUtils.pushJson(ugs.getUser().getId(), BaiChuanUtils.BOUGHT_USERGETSERVICE, context,
								detail);
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					result = "-1";// 失败
				}
			} else {
				result = "-3";// 该需求未处在等待接单 的状态，即服务状态错误！
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = "-2"; // 服务器出错
		}

		System.out.println("takerOrder-result：" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	// 用户催单
	public String reminder() {
		System.out.println("reminder-ugsId:" + ugsId);
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

		UserGetService ugs = userGetServiceService.getByUgServiceId(ugsId);

		if (ugs == null) {
			result = "-4";// 不存在此订单
			System.out.println("reminder-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		if (ugs.getUser().getId() != userId) {
			result = "-3";// 不是此人的订单，不能催单
			System.out.println("reminder-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		User u = new User();
		if (ugs.getBusinessService().getUser() != null) {
			u = ugs.getBusinessService().getUser();
		} else {
			u = businessServiceService.getBSsUser(ugs.getBusinessService().getId());
		}

		try {
			String context = "[用户催单]您的一个交易对象催单啦，请及时处理！";
			String detail = "[用户催单]用户您好，您的交易" + ugs.getBusinessService().getName() + " 进行终端，买家 "
					+ ugs.getUser().getNickName() + "于" + TimeUtil.convertTo_yyyyMMdd(System.currentTimeMillis() + "")
					+ " 进行了催单,请及时处理。";
			BaiChuanUtils.pushJson(u.getId(), BaiChuanUtils.BOUGHT_USERGETSERVICE, context, detail);
			result = "1";
		} catch (ParseException e) {
			e.printStackTrace();
			result = "-2";
		}

		System.out.println("reminder-result:" + result);
		inputStream = MsgUtil.sendString(result);
		return SUCCESS;

	}

	// 订单进行中，用户要求退款
	public String refundInProgress() {
		System.out.println("refundInProgress-ugsId:" + ugsId);
		System.out.println("refundInProgress-reason:" + reason);
		String result = "-1";

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("refundInProgress-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		try {
			UserGetService ugs = userGetServiceService.getByUgServiceId(ugsId);

			if (ugs.getStatus() == UserGetService.START_STATUS) {
				ugs.setStatus(UserGetService.APPLY_REFUND_STATUS);
				String curtime = TimeUtil.currentTime();
				ugs.setStatusTime(curtime);
				boolean flag = userGetServiceService.update(ugs);
				if (flag) {
					result = "1"; // 成功
					// 给卖家发通知
					try {
						User u = new User();
						if (ugs.getBusinessService().getUser() != null) {
							u = ugs.getBusinessService().getUser();
						} else {
							u = businessServiceService.getBSsUser(ugs.getBusinessService().getId());
						}

						String context = "[退款申请]您的一个交易对象提出退款申请，请及时处理";
						String detail = "[退款申请]用户您好，您的交易" + ugs.getBusinessService().getName() + " 进行终端，买家 "
								+ ugs.getUser().getNickName() + "于"
								+ TimeUtil.convertTo_yyyyMMdd(System.currentTimeMillis() + "") + " 提出退款申请，退款理由为 "
								+ reason + ",请及时处理。";
						BaiChuanUtils.pushJson(u.getId(), BaiChuanUtils.BOUGHT_USERGETSERVICE, context, detail);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					result = "-1";// 失败
				}
			} else {
				result = "-3";// 该需求未处在开始的状态，即服务状态错误！
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = "-2"; // 服务器出错
		}

		System.out.println("refundInProgress-result" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	/**
	 * 订单进行中，商家同意给用户退款
	 * 
	 * @return
	 */
	public String confirmRefundInProgress() {
		System.out.println("confirmRefundInProgress-ugsId:" + ugsId);
		String result = "-1";

		Object user = session.get("user");
		if (user == null) {
			result = "-4";// 无session，未登录
			System.out.println("confirmRefundInProgress-result" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		try {
			UserGetService ugs = userGetServiceService.getByUgServiceId(ugsId);

			if (ugs.getStatus() == UserGetService.APPLY_REFUND_STATUS) {
				ugs.setStatus(UserGetService.REFUND_STATUS);
				String curtime = TimeUtil.currentTime();
				ugs.setStatusTime(curtime);
				boolean flag = userGetServiceService.update(ugs);

				if (flag) {
					UserAccount ua = userAccountService.getById(ugs.getUser().getId());
					double total = ua.getBalance() + ugs.getPrice();
					ua.setBalance(total);
					boolean addMoney = userAccountService.update(ua);
					if (addMoney) {
						result = "1"; // 成功

						// 加积分
						try {
							userService.addRank_Credit(ugs.getUser().getId(), -100);
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {// addMoney失败，重新设置
						ugs.setStatus(UserGetService.APPLY_REFUND_STATUS);
						userGetServiceService.update(ugs);
						result = "-1";
					}
				} else {
					result = "-1";// 失败
				}
			} else {
				result = "-3";// 该需求未处在申请退款 的状态，即服务状态错误！
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = "-2"; // 服务器出错
		}

		System.out.println("confirmRefundInProgress-result" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	/**
	 * 订单进行中，商家拒绝给用户退款
	 * 
	 * @return
	 */
	public String refuseRefundInProgress() {
		System.out.println("refuseRefundInProgress-ugsId:" + ugsId);
		String result = "-1";

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("refuseRefundInProgress-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		try {
			UserGetService ugs = userGetServiceService.getByUgServiceId(ugsId);

			if (ugs.getStatus() == UserGetService.APPLY_REFUND_STATUS) {
				ugs.setStatus(UserGetService.START_STATUS);
				String curtime = TimeUtil.currentTime();
				ugs.setStatusTime(curtime);
				boolean flag = userGetServiceService.update(ugs);
				if (flag) {
					result = "1"; // 成功
					// 给买家发通知
					try {
						User u = new User();
						if (ugs.getBusinessService().getUser() != null) {
							u = ugs.getBusinessService().getUser();
						} else {
							u = businessServiceService.getBSsUser(ugs.getBusinessService().getId());
						}
						String context = "[退款失败]卖家拒绝了您的退款申请，点击查看";
						String detail = "[退款失败]" + ugs.getUser().getNickName() + "您好，卖家" + u.getNickName()
								+ "拒绝了您的退款申请，如有疑问，请私下协商，具体详情，请查看《服务协议》";
						BaiChuanUtils.pushJson(ugs.getUser().getId(), BaiChuanUtils.BOUGHT_USERGETSERVICE, context,
								detail);

					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					result = "-1";// 失败
				}
			} else {
				result = "-3";// 该需求未处在申请退款 的状态，即服务状态错误！
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = "-2"; // 服务器出错
		}

		System.out.println("refuseRefundInProgress-result" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	/**
	 * 商家拒绝预约，请求订单结束
	 * 
	 * @return
	 */
	public String refuseToTakeOrder() {
		System.out.println("refuseToTakerOrder-ugsId:" + ugsId);
		System.out.println("refuseToTakerOrder-reason:" + reason);
		String result = "-1";

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("refuseToTakeOrder-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		try {
			UserGetService ugs = userGetServiceService.getByUgServiceId(ugsId);

			if (ugs.getStatus() == UserGetService.PAY_FINISH_WAITING_STATUS) {
				ugs.setStatus(UserGetService.REFUSE_STATUS);
				String curtime = TimeUtil.currentTime();
				ugs.setStatusTime(curtime);
				boolean flag = userGetServiceService.update(ugs);
				if (flag) {
					UserAccount ua = userAccountService.getById(ugs.getUser().getId());
					double total = ua.getBalance() + ugs.getPrice();
					ua.setBalance(total);
					boolean addMoney = userAccountService.update(ua);
					if (addMoney) {
						result = "1"; // 成功

						// 给买家发通知
						try {
							User u = new User();
							if (ugs.getBusinessService().getUser() != null) {
								u = ugs.getBusinessService().getUser();
							} else {
								u = businessServiceService.getBSsUser(ugs.getBusinessService().getId());
							}

							String context = "[预约失败]用户您好，商家拒绝了您的预约，点击查看";
							String detail = "[预约失败]用户您好，商家" + u.getNickName() + "现在不方便接单或已有服务对象，请您及时查看订单详情";
							BaiChuanUtils.pushJson(ugs.getUser().getId(), BaiChuanUtils.BOUGHT_USERGETSERVICE, context,
									detail);

						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {// addMoney失败，重新设置
						ugs.setStatus(UserGetService.PAY_FINISH_WAITING_STATUS);
						userGetServiceService.update(ugs);
						result = "-1";
					}
				} else {
					result = "-1";// 失败
				}
			} else {
				result = "-3";// 该需求未处在等待接单 的状态，即服务状态错误！
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = "-2"; // 服务器出错
		}

		System.out.println("refuseToTakerOrder-result" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	/**
	 * 用户点击确定需求已完成,
	 * 
	 * 已测试
	 * 
	 * @return
	 */
	public String finishService() {
		System.out.println("finishService-ugsId:" + ugsId);
		String result = "-1";

		Object userSession = session.get("user");
		System.out.println("user is " + userSession);
		if (userSession == null) {
			result = "-6";// 未登录
			System.out.println("finishService-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		try {
			UserGetService ugs = userGetServiceService.getByUgServiceId(ugsId);

			System.out.println("ugs id null? " + ugs);
			if (ugs.getStatus() == UserGetService.START_STATUS) {
				ugs.setStatus(UserGetService.WAITING_TO_COMMENT_STATUS);
				String curtime = TimeUtil.currentTime();
				ugs.setStatusTime(curtime);
				System.out.println("ugs's id :" + ugs.getId());
				boolean flag = userGetServiceService.update(ugs);
				if (flag) {
					User user = ugs.getBusinessService().getUser();
					if (user == null) {
						user = businessServiceService.getBSsUser(ugs.getBusinessService().getId());
					}
					System.out.println("userId:" + user.getId());
					System.out.println("userAccountService:" + userAccountService == null);
					UserAccount ua = userAccountService.getById(user.getId());
					double total = ua.getBalance() + ugs.getPrice();
					ua.setBalance(total);
					boolean addMoney = userAccountService.update(ua);
					if (addMoney) {
						result = "1"; // 成功
						// 加积分
						try {
							userService.addRank_Credit(ugs.getUser().getId(), 10);
						} catch (Exception e) {
							e.printStackTrace();
						}
						// 给卖家发通知
						try {
							User u = new User();
							if (ugs.getBusinessService().getUser() != null) {
								u = ugs.getBusinessService().getUser();
							} else {
								u = businessServiceService.getBSsUser(ugs.getBusinessService().getId());
							}

							String context = "[订单完成]您有订单完成，点击查看";
							String detail = "[订单完成]用户" + ugs.getUser().getNickName() + "已确定订单完成，请及时查看订单详情。";
							BaiChuanUtils.pushJson(u.getId(), BaiChuanUtils.BOUGHT_USERGETSERVICE, context, detail);

						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {// addMoney失败，重新设置
						ugs.setStatus(UserGetService.START_STATUS);
						userGetServiceService.update(ugs);
						result = "-1";
					}
				} else {
					result = "-1";// 失败
				}
			} else {
				result = "-3";// 该需求未处在等待接单 的状态，即服务状态错误！
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = "-2"; // 服务器出错
		}

		System.out.println("finishService-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	// 店家看订单的详情
	public String getById() {
		System.out.println("getById-ugsId:" + ugsId);
		String result = "-1";

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("getById-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		try {
			UserGetService ugs = userGetServiceService.getByUgServiceId(ugsId);
			if (ugs != null) {
				ugs.getBusinessService().setUser(null);// 指定去除此级联，省流。
				JsonConfig jsonConfig = new JsonConfig();
				jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
				jsonConfig.setExcludes(new String[] { "password", "favoritePeople", "greatPeople", "concernedPeople",
						"favoriteServices", "photos", "receivedRequest", "services", "todoRequest", "userComment",
						"userRequest" });
				JSONObject jsResp = JSONObject.fromObject(ugs, jsonConfig);

				result = jsResp.toString();
			} else {
				result = "-4";// 无此订单
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = "-2";
		}

		System.out.println("getById-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;
	}

	// 买家看订单的详情
	public String getByIdFromUserAngles() {
		System.out.println("getById-ugsId:" + ugsId);
		String result = "-1";

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("getByIdFromUserAngles-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		try {
			UserGetService ugs = userGetServiceService.getByUgServiceId(ugsId);
			ugs.setUser(null);// 指定去除此级联，省流。
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			jsonConfig.setExcludes(new String[] { "password", "favoritePeople", "greatPeople", "concernedPeople",
					"favoriteServices", "photos", "receivedRequest", "services", "todoRequest", "userComment",
					"userRequest" });
			JSONObject jsResp = JSONObject.fromObject(ugs, jsonConfig);

			result = jsResp.toString();
		} catch (Exception e) {
			e.printStackTrace();
			result = "-2";
		}

		System.out.println("getById-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;
	}

	// 得到已经完成的订单
	public String getFinishedByStatusAndId() {
		System.out.println("getFinishedByStatusAndId-userId:" + userId);
		System.out.println("getFinishedByStatusAndId-page:" + page);
		String result = "-1";

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("getFinishedByStatusAndId-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		try {
			List<UserGetService> list = userGetServiceService.getFinished(page, userId);
			if (list != null) {
				JsonConfig jsonConfig = new JsonConfig();
				jsonConfig.setExcludes(new String[] { "status", "location_x", "location_y", "serviceTime", "time",
						"serviceCity", "canServiceDay", "favoriteNumber", "finishedPeople", "great" });
				jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
				JSONArray ja = JSONArray.fromObject(list, jsonConfig);

				JSONObject js = new JSONObject();
				js.put("list", ja);
				result = js.toString();
			} else {
				result = "-3";// 查无此人
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = "-2";
		}

		System.out.println("getBusinessSelled-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	public String getUgsByStatus() {
		String result = "-1";
		System.out.println("getUgsByStatus-status:" + status);
		int status = Integer.parseInt(this.status);
		List<UserGetService> list = userGetServiceService.getByStatus(page, status);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONObject js = JSONObject.fromObject(list, jsonConfig);

		result = js.toString();

		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	public String getBriefByUserId() {
		System.out.println("getBriefByUserId-userId:" + userId);
		System.out.println("getBriefByUserId-page:" + page);
		String result = "-1";

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("getBriefByUserId-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		List<UserGetService> list = userGetServiceService.getByUserId(page, userId);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String[] { "user", "status", "location_x", "location_y", "serviceTime", "time",
				"serviceCity", "canServiceDay", "favoriteNumber", "finishedPeople", "great" });
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray ja = JSONArray.fromObject(list, jsonConfig);

		JSONObject js = new JSONObject();
		js.put("list", ja);
		result = js.toString();

		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	public String getBusinessSelled() {
		System.out.println("getBusinessSelled-userId:" + userId);
		System.out.println("getBusinessSelled-page:" + page);
		System.out.println("getBusinessSelled-status:" + status);
		String result = "-1";

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("getBusinessSelled-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		// 防止传过来中文
		if (status.contains("，")) {
			status = status.replaceAll("，", ",");
		}
		List<UserGetService> list = userGetServiceService.getSelledServiceByUserIdAndStatus(page, userId, status);
		// 设置里面的user为空
		if (list != null) {
			for (UserGetService ugs : list) {
				if (ugs.getBusinessService() != null) {
					if (ugs.getBusinessService().getUser() != null) {
						ugs.getBusinessService().setUser(null);
					}
				}
			}
		}

		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String[] { "category", "location_x", "location_y", "serviceTime", "time",
				"serviceCity", "canServiceDay", "favoriteNumber", "finishedPeople", "great", "detail", "exchange",
				"favoritePeople", "greatPeople", "serviceType", "address", "age", "birthday", "concernedPeople",
				"favoriteServices", "gender", "hobby", "homePageBackgroundImage", "job", "latestLocation_x",
				"latestLocation_y", "password", "photos", "receivedRequest", "registCheckCode", "registTime",
				"services", "sign", "todoRequest", "userComment", "userRequest", "startTime", "service", "image",
				"userId", "rank", "rank_credit", "verifyStatus" });
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray ja = JSONArray.fromObject(list, jsonConfig);

		JSONObject js = new JSONObject();
		js.put("list", ja);
		result = js.toString();

		System.out.println("getBusinessSelled-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	/**
	 * 已精简
	 * 
	 * @return
	 */
	public String getUserBought() {
		System.out.println("getUserBought-userId:" + userId);
		System.out.println("getUserBought-page:" + page);
		System.out.println("getUserBought-status:" + status);
		String result = "-1";

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-6";// 未登录
			System.out.println("getUserBought-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		// 防止传过来中文
		if (status.contains("，")) {
			status = status.replaceAll("，", ",");
		}
		List<UserGetService> list = userGetServiceService.getBoughtServiceByUserIdAndStatus(page, userId, status);
		for (UserGetService ugs : list) {
			ugs.setUser(null);
		}

		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String[] { "category", "location_x", "location_y", "serviceTime", "time",
				"serviceCity", "canServiceDay", "favoriteNumber", "finishedPeople", "great", "detail", "exchange",
				"favoritePeople", "greatPeople", "serviceType", "address", "age", "birthday", "concernedPeople",
				"favoriteServices", "gender", "hobby", "homePageBackgroundImage", "job", "latestLocation_x",
				"latestLocation_y", "password", "photos", "receivedRequest", "registCheckCode", "registTime",
				"services", "sign", "todoRequest", "userComment", "userRequest", "startTime", "service", "image",
				"userId", "rank", "rank_credit", "verifyStatus" });
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray ja = JSONArray.fromObject(list, jsonConfig);

		JSONObject js = new JSONObject();
		js.put("list", ja);
		result = js.toString();

		System.out.println("getUserBought-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	/*******************/
	public UserGetServiceService getUserGetServiceService() {
		return userGetServiceService;
	}

	public void setUserGetServiceService(UserGetServiceService userGetServiceService) {
		this.userGetServiceService = userGetServiceService;
	}

	public UserGetService getUserGetService() {
		return userGetService;
	}

	public void setUserGetService(UserGetService userGetService) {
		this.userGetService = userGetService;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public int getUgsId() {
		return ugsId;
	}

	public void setUgsId(int ugsId) {
		this.ugsId = ugsId;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BusinessServiceService getBusinessServiceService() {
		return businessServiceService;
	}

	public void setBusinessServiceService(BusinessServiceService businessServiceService) {
		this.businessServiceService = businessServiceService;
	}

	public int getIsSucceed() {
		return isSucceed;
	}

	public void setIsSucceed(int isSucceed) {
		this.isSucceed = isSucceed;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	public UserAccountService getUserAccountService() {
		return userAccountService;
	}

	public void setUserAccountService(UserAccountService userAccountService) {
		this.userAccountService = userAccountService;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

}
