package dao;

import domain.UserAccount;

public interface UserAccountDAO {

	UserAccount getById(int userId);

	boolean save(UserAccount ua);

	boolean update(UserAccount ua);

}
