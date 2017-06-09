package service;

import java.util.List;

import domain.StudentVerify;

public interface StudentVerifyService {

	public boolean saveStudentVerify(StudentVerify sv);

	public void updateStudentVerify(StudentVerify uv);

	public StudentVerify getStuVerifyInfoById(int id);

	public List<StudentVerify> getByPage(int page);
}
