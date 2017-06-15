package dao;

import java.util.List;

import domain.Video;

public interface VideoDAO {

	public void save(Video video);

	public Video getVideo(int id);

	public void addVideoGreat(int videoId);

	public List<Video> getAll();

	List<Video> getVideoByPage(int page);

}
