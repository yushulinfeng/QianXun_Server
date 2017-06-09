package service;

import java.util.List;

import domain.UserVerify;

public interface UserVerifyService {

	public boolean saveUserVerify(UserVerify uv);

	public void updateUserVerify(UserVerify uv);

	public UserVerify getUserVerifyInfoById(int id);

	public List<UserVerify> getUserInfoByPage(int page);

	boolean delete(int uvId);
}
