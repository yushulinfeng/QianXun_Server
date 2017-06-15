package service;

import java.util.List;

import domain.UserComment;

public interface UserCommentService {

	public boolean save(UserComment userComment);

	public void update(UserComment userComment);

	public void delete(UserComment userComment);

	public UserComment getUserCommentById(int id);

	public List<UserComment> getBriefUserCommentByPage(int page);

	UserComment getDetailByRequestId(int id);

	public boolean addCommentGreat(int commentId);

}
