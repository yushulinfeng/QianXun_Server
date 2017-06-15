package controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.apache.struts2.interceptor.SessionAware;

import service.UserCommentService;
import service.UserRequestService;
import service.UserService;
import util.MsgUtil;

import com.opensymphony.xwork2.ActionSupport;

import domain.User;
import domain.UserComment;
import domain.UserRequest;

public class UserCommentAction extends ActionSupport implements SessionAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1816323953482060842L;

	private Map<String, Object> session;
	private InputStream inputStream = new ByteArrayInputStream("fail init...".getBytes());

	private UserCommentService userCommentService;
	private UserService userService;
	private UserRequestService userRequestService;

	private UserComment userComment;
	private String page;
	private String requestId;
	private String commentId;

	public String save() {
		String result = "failed";
		int status = -1;
		System.out.println("save-username:" + userComment.getUser().getUsername());
		System.out.println("save-comment_context:" + userComment.getComment_context());
		System.out.println("save-user.userRequest.id:" + userComment.getUserRequest().getId());
		try {
			User u = userService.findByUsername(userComment.getUser().getUsername());
			UserRequest userRequest = userRequestService.getUserRequestById(userComment.getUserRequest().getId());
			userComment.setUser(u);
			userComment.setUserRequest(userRequest);

			boolean isSucceed = userCommentService.save(userComment);
			if (isSucceed) {
				status = 1;
			}
		} catch (Exception e) {
			status = -2;
			e.printStackTrace();
		}

		result = status + "";
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	public String getBrief() {
		System.out.println("getBrief-page:" + page);
		String result = "failed";
		int page = 1;
		try {
			page = Integer.parseInt(this.page);
		} catch (Exception e) {
			inputStream = MsgUtil.sendString(result);
			e.printStackTrace();
		}
		List<UserComment> list = userCommentService.getBriefUserCommentByPage(page);
		if (list != null) {
			JSONObject jsResp = new JSONObject();
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			jsonConfig.setExcludes(new String[] { "comment_finishTime", "comment_great", "comment_picture2",
					"comment_picture3", "concernedPeople", "photos", "receivedRequest", "todoRequest", "userComment",
					"userRequest", "verifyStatus", "registTime", "registCheckCode", "rank_credit", "rank", "password",
					"nickName", "birthday", "age", "address", "favoriteServices", "gender", "hobby",
					"homePageBackgroundImage", "job", "latestLocation_x", "latestLocation_y", "services", "sign",
					"username" });
			JSONArray jo = JSONArray.fromObject(list, jsonConfig);
			jsResp.put("list", jo);
			result = jsResp.toString();
		}
		result = result.replaceAll("/big/", "/small/");// 将原图转化为缩略图

		System.out.println("getBrief-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;
	}

	public String addGreat() {
		System.out.println("addGreat-commentId:" + commentId);
		String result = "failed";
		int id = -1;
		try {
			id = Integer.parseInt(commentId);
		} catch (Exception e) {
			inputStream = MsgUtil.sendString(result);
			e.printStackTrace();
		}
		boolean flag = userCommentService.addCommentGreat(id);
		if (flag) {
			result = "success";
		}
		inputStream = MsgUtil.sendString(result);
		return SUCCESS;
	}

	public String getDetail() {
		String result = "failed";
		int id = -1;
		System.out.println("getDetail-requestId:" + requestId);
		try {
			id = Integer.parseInt(requestId);
		} catch (Exception e) {
			inputStream = MsgUtil.sendString(result);
			e.printStackTrace();
		}
		UserComment userComment = userCommentService.getDetailByRequestId(id);
		if (userComment != null) {
			JSONObject jsResp = new JSONObject();

			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			jsonConfig.setExcludes(new String[] { "comment_finishTime", "comment_great", "comment_picture2",
					"comment_picture3", "concernedPeople", "photos", "receivedRequest", "todoRequest", "userComment",
					"userRequest", "verifyStatus", "registTime", "registCheckCode", "rank_credit", "rank", "password",
					"nickName", "birthday", "age", "address" });
			JSONObject jo = JSONObject.fromObject(userComment, jsonConfig);
			jsResp.put("userComment", jo);

			result = jsResp.toString();
		}

		System.out.println("getDetail-result:" + result);
		inputStream = MsgUtil.sendString(result);
		return SUCCESS;
	}

	/*******************/
	public void setUserCommentService(UserCommentService userCommentService) {
		this.userCommentService = userCommentService;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public UserComment getUserComment() {
		return userComment;
	}

	public void setUserComment(UserComment userComment) {
		this.userComment = userComment;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public UserCommentService getUserCommentService() {
		return userCommentService;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getCommentId() {
		return commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public UserRequestService getUserRequestService() {
		return userRequestService;
	}

	public void setUserRequestService(UserRequestService userRequestService) {
		this.userRequestService = userRequestService;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

}
