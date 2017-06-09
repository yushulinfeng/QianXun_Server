package dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import dao.UserGetServiceDAO;
import domain.UserGetService;

public class UserGetServiceDAOImpl extends HibernateDaoSupport implements UserGetServiceDAO {

	@Override
	public int save(UserGetService userGetService) {
		Session session = this.getSession();
		Transaction tx = session.beginTransaction();
		try {
			tx.begin();
			session.save(userGetService);
			tx.commit();
			session.flush();
			session.close();
			// releaseSession(session);
			return userGetService.getId();
		} catch (DataAccessException e) {
			e.printStackTrace();
			tx.rollback();
			return -1;
		} finally {
			if (session.isOpen()) {
				session.close();
			}
			tx = null;
			session = null;
		}
	}

	@Override
	public boolean update(UserGetService userGetService) {
		Session session = this.getSession();
		Transaction tx = session.beginTransaction();
		try {
			tx.begin();
			session.update(userGetService);
			tx.commit();
			session.flush();
			session.close();
			// releaseSession(session);
			return true;
		} catch (DataAccessException e) {
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
	public boolean delete(UserGetService userGetService) {
		try {
			this.getHibernateTemplate().delete(userGetService);
			return true;
		} catch (DataAccessException e) {
			e.printStackTrace();
			return false;
		}
	}

	// 得到用户已购买的服务
	@Override
	public List<UserGetService> getBoughtServiceByUserIdAndStatus(int page, int userId, String status) {
		String hql = "from UserGetService ugs where ugs.user.id = ? and ugs.status in (" + status
				+ ") order by ugs.id DESC ";
		try {
			return this.doSplitPage(hql, page, 9, userId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// 得到用户已经完成的订单
	@Override
	public List<UserGetService> getFinishedService(int page, int userId) {
		String hql = "from UserGetService ugs where ugs.businessService.user.id = ? and ugs.status in (4,5)";
		try {
			return this.doSplitPage(hql, page, 15, userId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// 得到店家（自己）已售出的服务
	@Override
	public List<UserGetService> getSelledServiceByUserIdAndStatus(int page, int userId, String status) {
		String hql = "from UserGetService ugs where ugs.businessService.user.id = ? and ugs.status in (" + status
				+ ") order by ugs.id DESC";
		try {
			return this.doSplitPage(hql, page, 15, userId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public UserGetService getById(int id) {
		String hql = "from UserGetService ugs where ugs.id = ?";
		try {
			return (UserGetService) this.getHibernateTemplate().find(hql, id).get(0);
		} catch (DataAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean changeStatus(int ugsId, int status) {
		String sql = "update UserGetService set status = ? where id = ?";
		Session session = this.getSession();
		Transaction tx = session.beginTransaction();
		try {
			tx.begin();
			session.createQuery(sql);
			session.flush();
			tx.commit();
			session.close();
			// releaseSession(session);
			return true;
		} catch (DataAccessResourceFailureException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return false;
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

	@Override
	public List<UserGetService> getByStatus(int page, int status) {
		String hql = "from UserGetService ugs where ugs.status = ?";
		try {
			return this.doSplitPage(hql, page, 8, status);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<UserGetService> getByUserId(int page, int userId) {
		String hql = "from UserGetService ugs where ugs.user.id = ?";
		try {
			return this.doSplitPage(hql, page, 9, userId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param hql传入的hql语句
	 * @param curPage当前页
	 * @param pageSize每页显示大小
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<UserGetService> doSplitPage(final String hql, final int curPage, final int pageSize,
			final Object... values) {
		// 调用模板的execute方法，参数是实现了HibernateCallback接口的匿名类，
		return (List<UserGetService>) super.getHibernateTemplate().execute(new HibernateCallback() {
			// 重写其doInHibernate方法返回一个object对象
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				// 创建query对象
				Query query = session.createQuery(hql);
				for (int i = 0; i < values.length; i++) {
					query.setParameter(i, values[i]);
				}
				// 返回其执行了分布方法的list
				return query.setFirstResult((curPage - 1) * pageSize).setMaxResults(pageSize).list();
			}
		});
	}

}
