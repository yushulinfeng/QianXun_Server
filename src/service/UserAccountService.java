package service;

import domain.UserAccount;

public interface UserAccountService {

	UserAccount getById(int userId);

	boolean save(UserAccount userAccount);

	boolean update(UserAccount userAccount);

}
