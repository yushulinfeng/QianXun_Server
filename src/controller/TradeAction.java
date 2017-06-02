package controller;

import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.apache.struts2.interceptor.SessionAware;

import service.TradeRecordService;
import service.UserAccountService;
import service.UserService;
import util.MsgUtil;
import util.TimeUtil;

import com.opensymphony.xwork2.ActionSupport;

import config.BasicInfoConfig;
import domain.TradeRecord;
import domain.User;
import domain.UserAccount;

public class TradeAction extends ActionSupport implements SessionAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2123563920964033115L;
	// 提现周期
	private long period = BasicInfoConfig.CASH_PERIOD;

	private Map<String, Object> session;

	private InputStream inputStream;

	private UserAccount account;
	private TradeRecord tradeRecord;
	private int userId;

	private UserAccountService userAccountService;
	private TradeRecordService tradeRecordService;
	private UserService userService;

	public String getBalance() {
		System.out.println("getBalance-userId:" + userId);

		String result = "-1";
		// Map<String, Object> session =
		// ActionContext.getContext().getSession();
		if (session != null) {
			System.out.println("session:" + session);
			System.out.println(session.size());
			System.out.println(session.get("user"));
		}

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-3";// 未登录
			System.out.println("getBalance-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		try {
			User u = userService.getById(userId);
			if (u == null) {
				result = "-4";// 没有此用户
			} else {
				System.out.println("user's id is:" + u.getId());
				UserAccount ua = userAccountService.getById(userId);
				if (ua == null) {
					UserAccount userAccount = new UserAccount();
					userAccount.setUserId(userId);
					userAccount.setBalance((float) 0.00);
					userAccount.setUsername(u.getUsername() + "");
					userAccountService.save(userAccount);
					result = userAccount.getBalance() + "";
				} else {
					result = ua.getBalance() + "";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = "-2";
		}

		System.out.println("getBalance-result:" + result);
		inputStream = MsgUtil.sendString(result);
		return SUCCESS;

	}

	/**
	 * 提现
	 */
	public String getCash() {
		System.out.println("getCash-account_name:" + tradeRecord.getAccount_name());
		System.out.println("getCash-account_userId：" + tradeRecord.getUserId());
		System.out.println("getCash-account_email:" + tradeRecord.getAccount_email());

		String result = "-1";

		Object u = session.get("user");
		System.out.println("user is " + u);
		if (u == null) {
			result = "-3";// 未登录
			System.out.println("getCash-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}
		try {
			tradeRecord.setAccount_date(TimeUtil.formatCurrentTime());
			String verify = tradeRecord.hasNull();

			if (!verify.equals("no_Null")) {
				result = "-4";// 某个值为空
				System.out.println("getCash-result:" + result + " ,the object is null:" + verify);
				inputStream = MsgUtil.sendString(result);
				return SUCCESS;
			}

			UserAccount ua = userAccountService.getById(tradeRecord.getUserId());
			if (ua == null) {
				result = "-5";// 无此用户
				System.out.println("getCash-result:" + result);
				inputStream = MsgUtil.sendString(result);
				return SUCCESS;
			} else {
				long time = 0;
				if (ua.getLastGetCashTime() == null || ua.getLastGetCashTime().equals("")) {
					time = 0;
				} else {
					String stime = ua.getLastGetCashTime();
					Date date = TimeUtil.convertToTimestamp(stime);
					time = date.getTime();
				}
				long dis = System.currentTimeMillis() - time;
				if (dis < period) {
					result = "-7";// 1天之内不能再次体提现
					System.out.println("getCash-result:" + result);
					inputStream = MsgUtil.sendString(result);
					return SUCCESS;
				}

				double balance = ua.getBalance();
				double fee = Float.parseFloat(tradeRecord.getAccount_fee());
				double relase = balance - fee;
				if (relase < 0) {
					result = "-6";// 余额不足
					System.out.println("getCash-result:" + result);
					inputStream = MsgUtil.sendString(result);
					return SUCCESS;
				} else {
					boolean saved = tradeRecordService.save(tradeRecord);
					if (saved) {
						ua.setBalance(relase);
						ua.setLastGetCashTime(TimeUtil.formatCurrentTime());
						userAccountService.update(ua);
						result = "1";
					} else {
						tradeRecordService.delete(tradeRecord);
						result = "-1";// 失败
					}
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			result = "-2";
		} catch (ParseException e) {
			e.printStackTrace();
			result = "-2";
		}

		System.out.println("getCash-result:" + result);
		inputStream = MsgUtil.sendString(result);
		return SUCCESS;
	}

	public String getRecordByUserId() {
		System.out.println("getRecordByUserId-userId:" + userId);
		String result = "-1";

		Object user = session.get("user");
		System.out.println("user is " + user);
		if (user == null) {
			result = "-3";// 未登录
			System.out.println("getBalance-result:" + result);
			inputStream = MsgUtil.sendString(result);
			return SUCCESS;
		}

		try {
			List<TradeRecord> list = tradeRecordService.getByUserId(userId);

			JSONObject js = new JSONObject();
			JsonConfig jsConfig = new JsonConfig();
			jsConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			JSONArray ja = JSONArray.fromObject(list, jsConfig);
			js.put("list", ja.toString());
			result = js.toString();
		} catch (Exception e) {
			e.printStackTrace();
			result = "-2";
		}

		System.out.println("getRecordByUserId-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	public UserAccount getAccount() {
		return account;
	}

	public void setAccount(UserAccount account) {
		this.account = account;
	}

	public TradeRecord getTradeRecord() {
		return tradeRecord;
	}

	public void setTradeRecord(TradeRecord tradeRecord) {
		this.tradeRecord = tradeRecord;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public TradeRecordService getTradeRecordService() {
		return tradeRecordService;
	}

	public void setTradeRecordService(TradeRecordService tradeRecordService) {
		this.tradeRecordService = tradeRecordService;
	}

	public UserAccountService getUserAccountService() {
		return userAccountService;
	}

	public void setUserAccountService(UserAccountService userAccountService) {
		this.userAccountService = userAccountService;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

}
