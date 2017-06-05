package util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoRequest;
import com.taobao.api.TaobaoResponse;
import com.taobao.api.domain.Userinfos;
import com.taobao.api.request.OpenimCustmsgPushRequest;
import com.taobao.api.request.OpenimCustmsgPushRequest.CustMsg;
import com.taobao.api.request.OpenimUsersAddRequest;
import com.taobao.api.request.OpenimUsersUpdateRequest;
import com.taobao.api.response.OpenimCustmsgPushResponse;
import com.taobao.api.response.OpenimUsersAddResponse;
import com.taobao.api.response.OpenimUsersUpdateResponse;

public class BaiChuanUtils {

	/**
	 * 0：通知，不跳转
	 */
	public static final int NOTICE_WITHOUT_REDIRECT = 0;
	/**
	 * 1：通知，跳转网页
	 */
	public static final int NOTICE_WITH_REDIRECT = 1;
	/**
	 * 2：我的需求
	 */
	public static final int MY_REQUEST = 2;
	/**
	 * 3：他的需求
	 */
	public static final int HIS_REQUEST = 3;
	/**
	 * 4：发布的服务
	 */
	public static final int PUBLISH_USERGETSERVICE = 4;
	/**
	 * 5：购买的服务
	 */
	public static final int BOUGHT_USERGETSERVICE = 5;
	/**
	 * 6：审核状态
	 */
	public static final int CHECKING = 6;
	/**
	 * 7：审核状态
	 */
	public static final int CONCERNED_PEOPLE_REQUEST = 7;

	private static final String URL = "http://gw.api.taobao.com/router/rest";// 必须使用正式环境
	private static final String APP_KEY = "23352754";
	private static final String APP_SECRET = "4b205589d59038312f3d51dbb04d0f85";
	private static final String USER_SYSTEM = "90000000000";// 系统用户

	private static String getUserName(long userId) {// 11位字符串，前方补充0。Android调用
		String id = userId + "";
		while (id.length() < 11) {
			id = "0" + id;
		}
		return id;
	}

	private static List<Userinfos> getUserInfos(long userId, String nickName, String iconPath) {
		Userinfos info = new Userinfos();
		info.setUserid(getUserName(userId));
		info.setPassword(getUserPass(userId));
		if (nickName != null)
			info.setNick(nickName);
		if (iconPath != null)
			info.setIconUrl(iconPath);
		List<Userinfos> list = new ArrayList<Userinfos>();
		list.add(info);
		return list;
	}

	private static List<String> getUserList(long userId) {
		List<String> list = new ArrayList<String>();
		list.add(getUserName(userId));
		return list;
	}

	private static TaobaoResponse connectServer(TaobaoRequest<?> request) {
		try {
			return new DefaultTaobaoClient(URL, APP_KEY, APP_SECRET).execute(request);
		} catch (ApiException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static boolean pushJson(long userId, String json) {
		OpenimCustmsgPushRequest push = new OpenimCustmsgPushRequest();
		CustMsg msg = new CustMsg();
		msg.setFromUser(USER_SYSTEM);
		msg.setToUsers(getUserList(userId));
		msg.setSummary("系统消息");// 客户端最近消息里面显示的消息摘要，建议从json中解析
		msg.setData(json);
		push.setCustmsg(msg);
		OpenimCustmsgPushResponse resp = (OpenimCustmsgPushResponse) connectServer(push);
		if (resp == null)
			return false;
		return resp.isSuccess();
	}

	// public static boolean delUser(long userId) {
	// OpenimUsersDeleteRequest del = new OpenimUsersDeleteRequest();
	// del.setUserids(getUserName(userId));
	// OpenimUsersDeleteResponse resp = (OpenimUsersDeleteResponse)
	// connectServer(del);
	// if (resp == null)
	// return false;
	// return resp.isSuccess();
	// }
	//
	// public static boolean pushText(long userId, String text) {
	// OpenimImmsgPushRequest push = new OpenimImmsgPushRequest();
	// ImMsg msg = new ImMsg();
	// msg.setFromUser(USER_SYSTEM);
	// msg.setToUsers(getUserList(userId));
	// msg.setMsgType(0L);
	// msg.setContext(text);
	// push.setImmsg(msg);
	// OpenimImmsgPushResponse resp = (OpenimImmsgPushResponse)
	// connectServer(push);
	// if (resp == null)
	// return false;
	// return resp.isSuccess();
	// }

	/** 获取用户密码 */
	public static String getUserPass(long userId) {// key+username+secret,md5,first16
		String pass = APP_KEY + getUserName(userId) + APP_SECRET;
		try {
			pass = new String(DigestUtils.md5Hex(pass));
			pass = pass.substring(0, 16);
		} catch (Exception e) {
			pass = getUserName(userId);// 出错，则与用户名相同
		}
		return pass;
	}

	/** 添加用户 */
	public static boolean addUser(long userId, String nickName, String iconPath) {
		OpenimUsersAddRequest add = new OpenimUsersAddRequest();
		add.setUserinfos(getUserInfos(userId, nickName, iconPath));
		OpenimUsersAddResponse resp = (OpenimUsersAddResponse) connectServer(add);
		if (resp == null)
			return false;
		List<String> result = resp.getUidSucc();
		if (result != null)
			for (String item : result)
				if (item.equals(getUserName(userId)))
					return true;
		// 添加失败，有可能是用户已经存在，尝试修改
		return updateUser(userId, nickName, iconPath);
	}

	/** 更新用户数据 */
	public static boolean updateUser(long userId, String nickName, String iconPath) {
		OpenimUsersUpdateRequest update = new OpenimUsersUpdateRequest();
		update.setUserinfos(getUserInfos(userId, nickName, iconPath));
		OpenimUsersUpdateResponse resp = (OpenimUsersUpdateResponse) connectServer(update);
		if (resp == null)
			return false;
		List<String> result = resp.getUidSucc();
		if (result != null)
			for (String item : result)
				if (item.equals(getUserName(userId)))
					return true;
		return false;
	}

	/** 推送数据 */
	public static boolean pushJson(long userId, int type, String context, String detail) {
		return pushJson(userId, type, context, detail, null);
	}

	/** 推送附加数据 */
	public static boolean pushJson(long userId, int type, String context, String detail, String extra) {
		context = (context == null) ? "" : context;
		detail = (detail == null) ? "" : detail;
		extra = (extra == null) ? "" : extra;
		String json = "{\"type\":" + type + ",\"context\":\"" + context + "\",\"detail\":\"" + detail
				+ "\",\"extra\":\"" + extra + "\"}";
		return pushJson(userId, json);
	}

	// //////////////////main//////////////////

	// public static void main(String[] args) {
	// System.out.println("HELLO");
	// // System.out.println(new BaiChuanUtils().getUserName(2));
	// // System.out.println(new BaiChuanUtils().getUserPass(2));
	// // System.out.println(new BaiChuanUtils().delUser(1));
	// // System.out.println(new BaiChuanUtils().addUser(90000000000L, "系统通知",
	// // null));
	// // System.out.println(new BaiChuanUtils().pushText(1, "你好"));
	// System.out
	// .println(pushJson(
	// 1,
	// "{\"type\":4,\"context\":\"直接展示到通知栏\",\"detail\":\"可以显示给用户的详细信息2\",\"extra\":\"\"}"));
	// }

}
