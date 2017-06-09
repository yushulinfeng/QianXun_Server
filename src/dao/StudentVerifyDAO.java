package dao;

import java.util.List;

import domain.StudentVerify;

public interface StudentVerifyDAO {

	public void save(StudentVerify sv);

	public void update(StudentVerify sv);

	public StudentVerify getById(int id);

	public List<StudentVerify> getByPage(int page);

	void delete(StudentVerify sv);

	boolean delete(int uvId);
}
