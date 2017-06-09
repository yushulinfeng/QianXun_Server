package dao.impl;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import dao.TradeRecordDAO;
import domain.TradeRecord;

public class TradeRecordDAOImpl extends HibernateDaoSupport implements TradeRecordDAO {

	@Override
	public boolean save(TradeRecord tr) {
		Session session = this.getSession();
		Transaction tx = session.beginTransaction();
		try {
			tx.begin();
			session.save(tr);
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
	public boolean delete(TradeRecord tr) {
		Session session = this.getSession();
		Transaction tx = session.beginTransaction();
		try {
			tx.begin();
			session.delete(tr);
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
	public boolean updateBatch(List<TradeRecord> list) {
		Session session = this.getSession();
		Transaction tx = session.beginTransaction();
		try {
			tx.begin();
			for (TradeRecord tr : list) {
				session.update(tr);
				session.flush();
				session.clear();
			}
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
	public List<TradeRecord> getTheDay(String today) {
		try {
			String hql = "from TradeRecord tr where account_date = ?";
			return this.getHibernateTemplate().find(hql, today);
		} catch (DataAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<TradeRecord> getByUserId(int userId) {
		try {
			String hql = "from TradeRecord tr where tr.userId = ? order by id DESC";
			return this.getHibernateTemplate().find(hql, userId);
		} catch (DataAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

}
