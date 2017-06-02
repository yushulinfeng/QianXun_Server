package service;

import java.util.List;

import domain.User;
import domain.UserRequest;

public interface UserRequestService {

	public boolean save(UserRequest userRequest);

	public List<UserRequest> getRequestByType(double x, double y, double scope, int page, int type);

	public List<UserRequest> findLocalRequest(double x, double y, double scope, int page);

	public UserRequest getUserRequestById(int id);

	public List<UserRequest> getUserRequestByPage(int page);

	public List<UserRequest> getUserToDoRequests(long username);

	public List<UserRequest> getUserReceivedRequest(long username);

	public List<UserRequest> getUsersRequests(long username);

	public List<User> getReceiversByReqId(int id);

	public void update(UserRequest userRequest);

	public boolean delete(UserRequest serRequest);

	UserRequest getUserRequestByComId(int id);

	UserRequest getNewestRequestByUsername(long username);

	boolean addReport(int requestId);

	List<UserRequest> getByUserIdAndType(int page, int userId, int status);

	List<UserRequest> getAllByUserId(int userId, int page);

	List<UserRequest> getRelatedById(int userId, int page);

	List<UserRequest> getRelatedById(int userId);

	User getFinalReceiverByReqId(int id);
}
