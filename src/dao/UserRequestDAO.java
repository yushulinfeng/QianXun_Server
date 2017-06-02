package dao;

import java.util.List;

import domain.User;
import domain.UserRequest;

public interface UserRequestDAO {

	public void save(UserRequest userRequest);

	public List<UserRequest> getRequestsByType(double x, double y, double scope, int page, int type);

	// 得到附近scope的的需求
	/** 单位假设xy为度，scope为米 */
	public List<UserRequest> findLocalRequest(double x, double y, double scope, int page);

	public UserRequest getUserRequestById(int requestId);

	public List<UserRequest> getUserRequestByPage(int page);

	public List<User> getReceivers(int id);

	public void update(UserRequest userRequest);

	public boolean delete(UserRequest userRequest);

	UserRequest getUserRequestByComId(int comId);

	List<UserRequest> getUserToDoRequests(long username);

	List<UserRequest> getUserReceivedRequest(long username);

	public List<UserRequest> getUsersRequests(long username);

	public UserRequest getNewestUrByUsername(long username);

	void addReport(int requestId);

	List<UserRequest> getByUserIdAndType(int page, int userId, int status);

	List<UserRequest> getAllByUserId(int page, int userId);

	List<UserRequest> getRelatedReqs(int page, int userId);

	List<UserRequest> getRelatedReqs(int userId);

	User getFinalReceiverByReqId(int requestId);

}
