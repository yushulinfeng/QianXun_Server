package dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import dao.UserDAO;
import domain.BusinessService;
import domain.User;
import domain.UserRequest;

public class UserDAOImpl extends HibernateDaoSupport implements UserDAO {

	@Override
	public void save(User user) {
		this.getHibernateTemplate().save(user);
	}

	@Override
	public void delete(User user) {
		this.getHibernateTemplate().delete(user);
	}

	@Override
	public int getIdByUsername(long username) {
		String hql = "select id from User where username = ?";
		int id;
		Session session = this.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		try {
			tx.begin();
			id = (int) session.createQuery(hql).setParameter(1, username)
					.uniqueResult();

			tx.commit();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
			return -1;
		} finally {
			if (session.isOpen()) {
				session.close();
			}
			tx = null;
			session = null;
		}
		return id;
	}

	@Override
	public void updateLocation(long username, String x, String y) {
		String sql = "update qx_user u set u.latestLocation_x = ?,u.latestLocation_y = ? where u.username = ?";
		Session session = this.getSession();
		Transaction tx = session.beginTransaction();
		try {
			tx.begin();
			session.createQuery(sql).setParameter(0, x).setParameter(1, y)
					.setParameter(2, username);
			session.flush();
			tx.commit();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			if (session.isOpen()) {
				session.close();
			}
			tx = null;
			session = null;
		}
	}

	@Override
	public boolean updateUserBackGroundImage(int userId, String pic) {
		String hql = "update User u set u.homePageBackgroundImage = ? where u.id = ?";
		// this.getHibernateTemplate().bulkUpdate(hql, pic, userId);
		Session session = this.getSession();
		Transaction tx = session.beginTransaction();
		try {
			tx.begin();
			session.createQuery(hql).setParameter(0, pic)
					.setParameter(1, userId).executeUpdate();
			tx.commit();
			session.close();
			// releaseSession(session);
			return true;
		} catch (DataAccessException e) {
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

	@Override
	public boolean updateUserHeadIcon(int userId, String pic) {
		String hql = "update User u set u.headIcon = ? where u.id = ?";
		// this.getHibernateTemplate().bulkUpdate(hql, pic, userId);
		Session session = this.getSession();
		Transaction tx = session.beginTransaction();
		try {
			tx.begin();
			session.createQuery(hql).setParameter(0, pic)
					.setParameter(1, userId).executeUpdate();
			tx.commit();
			session.close();
			// releaseSession(session);
			return true;
		} catch (DataAccessException e) {
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

	@Override
	public void update(User user) {
		// this.getHibernateTemplate().merge(user);
		Session session = this.getSession();
		Transaction tx = session.beginTransaction();
		try {
			tx.begin();
			session.update(user);
			session.flush();
			tx.commit();
			session.close();
			System.out.println("user updating...");
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			if (session.isOpen()) {
				session.close();
			}
			tx = null;
			session = null;
		}
	}

	@Override
	public boolean addRank_Credit(int userId, int n) {
		String sql = "update qx_user u set u.rank_credit = if((u.rank_credit + ?)<0,0,(u.rank_credit + ?)) ,u.rank = if(?>0,(u.rank+?),u.rank) where u.id = ? ";
		Session session = this.getSession();
		Transaction tx = session.beginTransaction();
		try {
			tx.begin();
			Query q = session.createSQLQuery(sql);
			q.setParameter(0, n);
			q.setParameter(1, n);
			q.setParameter(2, n);
			q.setParameter(3, n);
			q.setParameter(4, userId);
			q.executeUpdate();

			session.flush();
			tx.commit();
			session.close();
			// releaseSession(session);
			return true;
		} catch (HibernateException e) {
			tx.rollback();
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

	@Override
	public User findByUsername(long username) {
		String hql = "from User u where u.username = ?";
		User user = null;
		try {
			user = (User) this.getHibernateTemplate().find(hql, username)
					.get(0);
		} catch (DataAccessException e) {
			e.printStackTrace();
			user = null;
		} catch (Exception e) {
			e.printStackTrace();
			user = null;
		}
		return user;
	}

	@Override
	public boolean isFavorite(int userId, int serviceId) {
		String hql = "select bs from BisunessService bs where bs.id = ? and bs in (select u.favoriteServices from User u where u.id = ?)";
		return this.getHibernateTemplate().find(hql, serviceId, userId).size() == 1 ? true
				: false;
	}

	@Override
	public List<UserRequest> getReceivedRequests(int userId) {
		String hql = "select u.receivedRequest from User u where u.id = ?";
		return this.getHibernateTemplate().find(hql, userId);
	}

	@Override
	public List<BusinessService> getAllFBByUserId(int page, int userId) {
		String hql = "select u.favoriteServices from User u where u.id = ?";
		try {
			return this.doSplitPage(hql, page, 9, userId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public User getById(int id) {
		String hql = "from User where id = ?";
		try {
			User user = (User) this.getHibernateTemplate().find(hql, id).get(0);
			return user;
		} catch (DataAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<User> getAllConcernPeople(int userId) {
		String hql = "select u.concernedPeople from User u where u.id = ?";
		return this.getHibernateTemplate().find(hql, userId);
	}

	@Override
	public User login(long username) {
		String sql = "select u.username and u.password from User u where u.username = ?";
		Session session = this.getSession();
		return (User) session.createQuery(sql).setParameter(0, username)
				.uniqueResult();
	}

	@Override
	public User findById(int id) {
		User user = this.getHibernateTemplate().get(User.class, id);
		return user;
	}

	@Override
	public List<User> findAll() {
		String hql = "from User";
		List<User> users = this.getHibernateTemplate().find(hql);
		return users;
	}

	/**
	 * @param hql传入的hql语句
	 * @param curPage当前页
	 * @param pageSize每页显示大小
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<BusinessService> doSplitPage(final String hql,
			final int curPage, final int pageSize, final Object... values) {
		// 调用模板的execute方法，参数是实现了HibernateCallback接口的匿名类，
		return (List<BusinessService>) super.getHibernateTemplate().execute(
				new HibernateCallback() {
					// 重写其doInHibernate方法返回一个object对象
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						// 创建query对象
						Query query = session.createQuery(hql);
						for (int i = 0; i < values.length; i++) {
							query.setParameter(i, values[i]);
						}
						// 返回其执行了分布方法的list
						query.setFirstResult((curPage - 1) * pageSize)
								.setMaxResults(pageSize);
						List<UserRequest> list = query.list();
						return list;
					}
				});
	}

}
