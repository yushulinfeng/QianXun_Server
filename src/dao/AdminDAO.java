package dao;

import domain.Admin;

public interface AdminDAO {

	public Admin getByUsername(String username);

	long getRegistUserNumberByTime(String startTime, String endTime);

}
