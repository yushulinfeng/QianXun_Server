package dao;

import java.util.List;

import domain.BusinessService;
import domain.User;
import domain.UserRequest;

public interface UserDAO {
	void save(User user);

	void delete(User user);

	void update(User user);

	User findByUsername(long username);

	User findById(int id);

	List<User> findAll();

	void updateLocation(long username, String x, String y);

	User login(long username);

	boolean updateUserBackGroundImage(int userId, String pic);

	User getById(int id);

	List<BusinessService> getAllFBByUserId(int page, int userId);

	List<User> getAllConcernPeople(int userId);

	boolean updateUserHeadIcon(int userId, String pic);

	int getIdByUsername(long username);

	boolean isFavorite(int userId, int serviceId);

	boolean addRank_Credit(int userId, int n);

	List<UserRequest> getReceivedRequests(int userId);

}
