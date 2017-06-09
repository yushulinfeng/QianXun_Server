package service.impl;

import java.util.List;

import service.UserGetServiceService;
import util.TimeUtil;
import dao.UserGetServiceDAO;
import domain.UserGetService;

public class UserGetServiceServiceImpl implements UserGetServiceService {

	private UserGetServiceDAO userGetServiceDAO;

	@Override
	public int save(UserGetService userGetService) {
		userGetService.setStatus(UserGetService.WAITING_PAY_STATUS);
		String curTime = TimeUtil.currentTime();
		userGetService.setStartTime(curTime);
		userGetService.setStatusTime(curTime);

		return userGetServiceDAO.save(userGetService);
	}

	@Override
	public boolean update(UserGetService userGetService) {
		return userGetServiceDAO.update(userGetService);
	}

	@Override
	public boolean delete(UserGetService userGetService) {
		return userGetServiceDAO.delete(userGetService);
	}

	@Override
	public UserGetService getByUgServiceId(int id) {
		return userGetServiceDAO.getById(id);
	}

	@Override
	public boolean changeStatus(int ugsId, int status) {
		return userGetServiceDAO.changeStatus(ugsId, status);
	}

	@Override
	public List<UserGetService> getByStatus(int page, int status) {
		return userGetServiceDAO.getByStatus(page, status);
	}

	@Override
	public List<UserGetService> getFinished(int page, int userId) {
		return userGetServiceDAO.getFinishedService(page, userId);
	}

	@Override
	public List<UserGetService> getByUserId(int page, int userId) {
		return userGetServiceDAO.getByUserId(page, userId);
	}

	// 得到用户已购买的服务
	@Override
	public List<UserGetService> getBoughtServiceByUserIdAndStatus(int page, int userId, String status) {
		return userGetServiceDAO.getBoughtServiceByUserIdAndStatus(page, userId, status);
	}

	// 得到店家（自己）已售出的服务
	@Override
	public List<UserGetService> getSelledServiceByUserIdAndStatus(int page, int userId, String status) {
		return userGetServiceDAO.getSelledServiceByUserIdAndStatus(page, userId, status);
	}

	/********************/

	public void setUserGetServiceDAO(UserGetServiceDAO userGetServiceDAO) {
		this.userGetServiceDAO = userGetServiceDAO;
	}

	public UserGetServiceDAO getUserGetServiceDAO() {
		return userGetServiceDAO;
	}

}
