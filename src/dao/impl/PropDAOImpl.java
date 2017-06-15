package dao.impl;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import dao.PropDAO;
import domain.PropImage;

public class PropDAOImpl extends HibernateDaoSupport implements PropDAO {

	@Override
	public void save() {

	}

	@Override
	public List<PropImage> getLinks() {
		String sql = " from PropImage pi where pi.id<=5";
		Session session = this.getSession();
		Transaction tx = session.beginTransaction();
		List<PropImage> list = null;
		try {
			list = session.createQuery(sql).list();
			session.flush();
			tx.commit();
			session.close();
			// releaseSession(session);
		} catch (HibernateException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (session.isOpen()) {
				session.close();
			}
			tx = null;
			session = null;
		}
		return list;
	}

	@Override
	public List<PropImage> getSmallLinks() {
		String sql = " from PropImage pi where pi.id>5 and pi.id<=9";
		Session session = this.getSession();
		Transaction tx = session.beginTransaction();
		List<PropImage> list;
		try {
			list = session.createQuery(sql).list();
			session.flush();
			tx.commit();
			session.close();
			// releaseSession(session);
		} catch (HibernateException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (session.isOpen()) {
				session.close();
			}
			tx = null;
			session = null;
		}
		return list;

	}

	@Override
	public boolean update(PropImage prop) {
		Session session = this.getSession();
		Transaction tx = session.beginTransaction();
		try {
			session.update(prop);
			session.flush();
			tx.commit();
			session.close();
			// releaseSession(session);
			return true;
		} catch (HibernateException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (session.isOpen()) {
				session.close();
			}
			tx = null;
			session = null;
		}

	}

}
