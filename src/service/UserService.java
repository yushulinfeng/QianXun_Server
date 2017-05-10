package service;

import java.io.File;
import java.util.List;
import java.util.Map;

import domain.BusinessService;
import domain.User;
import domain.UserRequest;

public interface UserService {

	public Map<String, Object> login(long username, String password);

	public int regist(User user);

	public Map<String, Object> regist2(User user);

	public Map<String, Object> getRegistCheckCode(long username);

	public Map<String, Object> getLoginCheckCode(long username);

	public Map<String, Object> loginByCheckCode(long username);

	public Map<String, Object> check(long username);

	public Map<String, Object> changePwd(long username, String password);

	public Map<String, Object> getMyRequest(User user);

	public boolean hasPhoneNumber(long username);

	public User findByUsername(long username);

	void update(User user);

	void updateLocation(long username, String x, String y);

	User getById(int userId);

	List<BusinessService> getAllFBByUserId(int page, int userId);

	List<User> getAllConcernPeople(int userId);

	boolean updateUserBackGroundImage(int userId, File pic);

	boolean updateUserHeadIcon(int userId, File pic);

	int getIdByUsername(long username);

	boolean isFavorite(int userId, int serviceId);

	boolean addRank_Credit(int userId, int n);

	List<UserRequest> getReceivedRequests(int userId);
}
