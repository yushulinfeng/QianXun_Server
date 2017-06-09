package dao;

import java.util.List;

import domain.UserVerify;

public interface UserVerifyDAO {

	public void save(UserVerify uv);

	public void update(UserVerify uv);

	public UserVerify getById(int id);

	public List<UserVerify> getByPage(int page);

	boolean delete(int uvId);
}
