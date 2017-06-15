package dao;

import java.util.List;

import domain.UserComment;

public interface UserCommentDAO {

	public void save(UserComment userComment);

	public void update(UserComment userComment);

	public boolean delete(UserComment userComment);

	public UserComment getUserCommentById(int id);

	public List<UserComment> getBriefByPage(int page);

	UserComment getDetailByRequestId(int id);

	public void addCommentGreat(int commentId);

}
