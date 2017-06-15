package controller;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.struts2.interceptor.SessionAware;

import service.VideoService;
import util.MsgUtil;

import com.opensymphony.xwork2.ActionSupport;

import domain.Video;

public class VideoAction extends ActionSupport implements SessionAware {

	private Map<String, Object> session;
	private InputStream inputStream;

	private VideoService videoService;
	private String videoId;
	private Video video;
	private int page;

	public String save() {
		String result = "false";
		boolean isSuccess = videoService.save(video);
		if (isSuccess) {
			result = "success";
		}
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;
	}

	public String addVideoGreat() {
		String result = "false";
		int id = -1;
		try {
			id = Integer.parseInt(videoId);
		} catch (Exception e) {
			inputStream = MsgUtil.sendString(result);
		}
		boolean flag = videoService.addVideoGreat(id);
		if (flag) {
			result = "success";
		}
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;
	}

	public String getVideoById() {
		String result = "failed";
		System.out.println("getVideoById-videoId:" + videoId);
		Video video = videoService.getVideoById(Integer.parseInt(videoId));

		JSONObject jsResp = new JSONObject();
		jsResp.put("video", video);
		result = jsResp.toString();

		System.out.println("getVideoById-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;
	}

	public String getVideoByPage() {
		String result = "failed";
		System.out.println("getVideoByPage-page:" + page);
		List<Video> videos = videoService.getVideoByPage(page);

		JSONObject jsResp = new JSONObject();
		jsResp.put("list", videos);
		result = jsResp.toString();

		System.out.println("getVideoByPage-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;
	}

	public String getAllVideo() {
		String result = "failed";
		List<Video> list = videoService.getAllVideo();
		if (list != null) {
			JSONObject jsResp = new JSONObject();
			jsResp.put("list", list);
			result = jsResp.toString();
		}

		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	/************************/

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public VideoService getVideoService() {
		return videoService;
	}

	public void setVideoService(VideoService videoService) {
		this.videoService = videoService;
	}

	public Video getVideo() {
		return video;
	}

	public void setVideo(Video video) {
		this.video = video;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

}
