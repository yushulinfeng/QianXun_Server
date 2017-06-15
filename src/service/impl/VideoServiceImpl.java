package service.impl;

import java.util.List;

import service.VideoService;
import config.BaseConfiger;
import dao.VideoDAO;
import domain.Video;

public class VideoServiceImpl implements VideoService {

	private VideoDAO videoDAO;
	private static String root = BaseConfiger.FileSavePath;

	@Override
	public boolean save(Video video) {
		try {
			videoDAO.save(video);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public Video getVideoById(int id) {
		return videoDAO.getVideo(id);
	}

	@Override
	public boolean addVideoGreat(int videoId) {
		try {
			videoDAO.addVideoGreat(videoId);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public List<Video> getVideoByPage(int page) {
		try {
			return videoDAO.getVideoByPage(page);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Video> getAllVideo() {
		return videoDAO.getAll();
	}

	public VideoDAO getVideoDAO() {
		return videoDAO;
	}

	public void setVideoDAO(VideoDAO videoDAO) {
		this.videoDAO = videoDAO;
	}

}
