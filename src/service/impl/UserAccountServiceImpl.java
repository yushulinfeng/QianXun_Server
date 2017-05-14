package service.impl;

import service.UserAccountService;
import dao.UserAccountDAO;
import domain.UserAccount;

public class UserAccountServiceImpl implements UserAccountService {

	private UserAccountDAO userAccountDAO;

	@Override
	public UserAccount getById(int userId) {
		return userAccountDAO.getById(userId);
	}

	@Override
	public boolean save(UserAccount userAccount) {
		return userAccountDAO.save(userAccount);
	}

	@Override
	public boolean update(UserAccount userAccount) {
		return userAccountDAO.update(userAccount);
	}

	/****************************/
	public UserAccountDAO getUserAccountDAO() {
		return userAccountDAO;
	}

	public void setUserAccountDAO(UserAccountDAO userAccountDAO) {
		this.userAccountDAO = userAccountDAO;
	}
}
