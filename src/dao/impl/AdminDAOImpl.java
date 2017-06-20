package dao.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import dao.AdminDAO;
import domain.Admin;

public class AdminDAOImpl extends HibernateDaoSupport implements AdminDAO {

	@Override
	public Admin getByUsername(String username) {
		String hql = "from Admin a where a.username = ?";
		try {
			return (Admin) this.getHibernateTemplate().find(hql, username).get(0);
		} catch (DataAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long getRegistUserNumberByTime(String startTime, String endTime) {
		String hql = "select count(u.id) from User u where u.registTime >= CONCAT(UNIX_TIMESTAMP(CONCAT(?,' 00:00:00')),000) and u.registTime <= CONCAT(UNIX_TIMESTAMP(CONCAT(?,' 00:00:00')),000)";
		try {
			return (long) this.getHibernateTemplate().find(hql, startTime, endTime).get(0);
		} catch (DataAccessException e) {
			e.printStackTrace();
			return -1;
		}
	}

}
