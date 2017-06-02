package service;

import java.util.List;

import domain.UserGetService;

public interface UserGetServiceService {

	int save(UserGetService userGetService);

	boolean update(UserGetService userGetService);

	boolean delete(UserGetService userGetService);

	UserGetService getByUgServiceId(int id);

	List<UserGetService> getByStatus(int page, int status);

	List<UserGetService> getByUserId(int page, int userId);

	List<UserGetService> getFinished(int page, int userId);

	boolean changeStatus(int ugsId, int status);

	List<UserGetService> getBoughtServiceByUserIdAndStatus(int page, int userId, String status);

	List<UserGetService> getSelledServiceByUserIdAndStatus(int page, int userId, String status);

}
