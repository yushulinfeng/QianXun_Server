package dao.impl;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import dao.UserAccountDAO;
import domain.UserAccount;

public class UserAccountDAOImpl extends HibernateDaoSupport implements UserAccountDAO {

	@Override
	public boolean save(UserAccount ua) {
		Session session = this.getSession();
		Transaction tx = session.beginTransaction();
		try {
			tx.begin();
			session.save(ua);
			session.flush();
			tx.commit();
			session.close();
			// releaseSession(session);
			return true;
		} catch (HibernateException e) {
			e.printStackTrace();
			tx.rollback();
			return false;
		} finally {
			if (session.isOpen()) {
				session.close();
			}
			tx = null;
			session = null;
		}
	}

	@Override
	public boolean update(UserAccount ua) {
		Session session = this.getSession();
		Transaction tx = session.beginTransaction();
		try {
			tx.begin();
			session.update(ua);
			session.flush();
			tx.commit();
			session.close();
			// releaseSession(session);
			return true;
		} catch (HibernateException e) {
			e.printStackTrace();
			tx.rollback();
			return false;
		} finally {
			if (session.isOpen()) {
				session.close();
			}
			tx = null;
			session = null;
		}
	}

	@Override
	public UserAccount getById(int userId) {
		String sql = "from UserAccount ua where ua.userId = ?";
		try {
			return (UserAccount) this.getHibernateTemplate().find(sql, userId).get(0);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
