package service;

import java.util.List;

import domain.Video;

public interface VideoService {

	public boolean save(Video video);

	public Video getVideoById(int id);

	public boolean addVideoGreat(int videoId);

	public List<Video> getAllVideo();

	public List<Video> getVideoByPage(int page);

}
