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

import dao.BusinessServiceDAO;
import domain.BusinessService;
import domain.User;

public class BusinessServiceDAOImpl extends HibernateDaoSupport implements BusinessServiceDAO {

	@Override
	public boolean save(BusinessService businessService) {
		// this.getHibernateTemplate().update(businessService);
		Session session = this.getSession();
		Transaction tx = session.beginTransaction();
		try {
			tx.begin();
			session.save(businessService);
			session.flush();
			tx.commit();
			session.close();
			// releaseSession(session);
			System.out.println("businessService saving...");
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
	public boolean updateStatus(int serviceId, int status) {
		String hql = "update BusinessService bs set bs.status= ? where bs.id = ?";
		Session session = this.getSession();
		Transaction tx = session.beginTransaction();
		try {
			tx.begin();
			session.createQuery(hql).setParameter(0, status).setParameter(1, serviceId).executeUpdate();
			session.flush();
			tx.commit();
			session.close();
			// releaseSession(session);
			System.out.println("businessService updateStatus...");
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
	public boolean update(BusinessService businessService) {
		Session session = this.getSession();
		Transaction tx = session.beginTransaction();
		try {
			// this.getHibernateTemplate().update(businessService);
			tx.begin();
			session.update(businessService);
			session.flush();
			tx.commit();
			session.close();
			// releaseSession(session);
			System.out.println("businessService updating...");
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
	public boolean delete(BusinessService businessService) {
		try {
			this.getHibernateTemplate().delete(businessService);
			return true;
		} catch (DataAccessException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public BusinessService getById(int id) {
		String hql = "from BusinessService bs where bs.id = ? ";
		try {
			return (BusinessService) this.getHibernateTemplate().find(hql, id).get(0);
		} catch (DataAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public User getBSsUser(int businessId) {
		String hql = "select user from BusinessService bs where bs.id = ?";
		try {
			return (User) this.getHibernateTemplate().find(hql, businessId).get(0);
		} catch (DataAccessException e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public List<BusinessService> getByType(int page, int category) {
		String hql = "from BusinessService bs where bs.id< ? and bs.category = ? and (bs.status != 2) order by bs.id DESC";
		try {
			if (page == -1) {
				hql = "from BusinessService bs where and bs.category = ? order by bs.id DESC";
				return this.doSplitPageById(hql, page, 9, category);
			}
			return this.doSplitPageById(hql, page, 9, page, category);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<BusinessService> getByUserId(int page, int userId) {
		String hql = "from BusinessService bs where bs.user.id = ? and ( bs.status != 2) order by bs.id DESC";
		try {
			return this.doSplitPage(hql, page, 9, userId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<BusinessService> getLocalByCategory(double x, double y, double scope, int page, int category) {
		String hql = " from BusinessService bs where bs.id< ? and ( ROUND( 2 * ASIN(SQRT(POW(SIN((bs.location_y* PI() /180.0- ? * PI() / 180.0) / 2),2) +COS(bs.location_y * PI() / 180.0)*COS( ? * PI() / 180.0)*POW(SIN((bs.location_x*PI()/180.0-?*PI()/180.0)/2),2)))*6378.137*10000) / 10000) < (?*1.0) and (bs.status != 2) and bs.category = ?  order by bs.id DESC ";
		try {
			if (page == -1) {
				hql = " from BusinessService bs where ( ROUND( 2 * ASIN(SQRT(POW(SIN((bs.location_y* PI() /180.0- ? * PI() / 180.0) / 2),2) +COS(bs.location_y * PI() / 180.0)*COS( ? * PI() / 180.0)*POW(SIN((bs.location_x*PI()/180.0-?*PI()/180.0)/2),2)))*6378.137*10000) / 10000) < (?*1.0) and (bs.status != 2) and bs.category = ?  order by bs.id DESC ";
				return this.doSplitPageById(hql, page, 10, y, y, x, scope, category);
			}
			return this.doSplitPageById(hql, page, 10, page, y, y, x, scope, category);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<BusinessService> getLatestByCategory(int page, int category) {
		String hql = "from BusinessService bs where bs.category = ? and ( bs.status != 2) order by bs.time DESC ";
		try {
			return this.doSplitPage(hql, page, 10, category);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<BusinessService> getSchoolByCategory(int page, int category, String school) {
		String hql = "from BusinessService bs where bs.category = ? and bs.user.school = ? and ( bs.status != 2) order by bs.time DESC ";
		try {
			return this.doSplitPage(hql, page, 10, category, school);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<BusinessService> getSchoolByPage(int page, String school) {
		String hql = "from BusinessService bs where bs.user.school = ? and (bs.status != 2) order by bs.time DESC";

		try {
			return this.doSplitPage(hql, page, 10, school);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<BusinessService> getLocalByPage(double x, double y, double scope, int page) {
		String hql = " from BusinessService bs where bs.id<? and (ROUND((2 * ASIN(SQRT(POW(SIN(bs.location_y*PI()/180.0 - ?*PI()/180.0/2),2)+COS(bs.location_y*PI()/180.0)*COS(?*PI()/180.0)*POW(SIN(bs.location_x*PI()/180.0 - ?*PI()/180.0/2),2))))*6378.137*10000) / 10000)<(?*1.0) and (bs.status != 2) order by bs.id DESC ";
		try {
			if (page == -1) {
				hql = " from BusinessService bs where (ROUND((2 * ASIN(SQRT(POW(SIN(bs.location_y*PI()/180.0 - ?*PI()/180.0/2),2)+COS(bs.location_y*PI()/180.0)*COS(?*PI()/180.0)*POW(SIN(bs.location_x*PI()/180.0 - ?*PI()/180.0/2),2))))*6378.137*10000) / 10000)<(?*1.0) and (bs.status != 2) order by bs.id DESC ";
				return this.doSplitPageById(hql, page, 10, y, y, x, scope);
			}
			return this.doSplitPageById(hql, page, 10, page, y, y, x, scope);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<BusinessService> getHotByPage(int page) {
		String hql = "from BusinessService bs where ( bs.status != 2) order by bs.favoriteNumber DESC";

		try {
			return this.doSplitPage(hql, page, 10);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<BusinessService> getLatestByPage(int page) {
		String hql = "from BusinessService bs where (bs.status != 2) order by bs.time DESC";

		try {
			return this.doSplitPage(hql, page, 10);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<User> getFavoritePeople(int serviceId) {
		// String hql =
		// "select new BusinessService(u.id,u.headIcon,u.username,u.nickName) from User u ,BusinessService bs where u.id = bs.user.id and bs.id = ? ";
		String hql = "select bs.favoritePeople from BusinessService bs where bs.id = ?";
		Transaction tx = null;
		List<User> list = null;
		Session session = this.getSession();
		tx = session.beginTransaction();
		try {
			tx.begin();
			list = (List<User>) session.createQuery(hql).setParameter(0, serviceId).list();
			session.flush();
			tx.commit();
			session.close();
			// releaseSession(session);
		} catch (DataAccessResourceFailureException e) {
			tx.rollback();
			list = null;
			e.printStackTrace();
		} catch (IllegalStateException e) {
			tx.rollback();
			list = null;
			e.printStackTrace();
		} catch (HibernateException e) {
			tx.rollback();
			list = null;
			e.printStackTrace();
		} finally {
			if (session.isOpen()) {
				session.close();
			}
			tx = null;
			session = null;
		}

		return list;
	}

	/**
	 * 
	 * @param hql传入的hql语句
	 * @param curPage当前页
	 * @param pageSize每页显示大小
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<BusinessService> doSplitPage(final String hql, final int curPage, final int pageSize,
			final Object... values) {
		// 调用模板的execute方法，参数是实现了HibernateCallback接口的匿名类，
		return (List<BusinessService>) super.getHibernateTemplate().execute(new HibernateCallback() {
			// 重写其doInHibernate方法返回一个object对象
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				// 创建query对象
				Query query = session.createQuery(hql);
				for (int i = 0; i < values.length; i++) {
					query.setParameter(i, values[i]);
				}
				return query.setFirstResult((curPage - 1) * pageSize).setMaxResults(pageSize).list();
			}
		});
	}

	@SuppressWarnings("unchecked")
	private List<BusinessService> doSplitPageById(final String hql, final int curPage, final int pageSize,
			final Object... values) {
		// 调用模板的execute方法，参数是实现了HibernateCallback接口的匿名类，
		return (List<BusinessService>) super.getHibernateTemplate().execute(new HibernateCallback() {
			// 重写其doInHibernate方法返回一个object对象
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				// 创建query对象
				Query query = session.createQuery(hql);
				for (int i = 0; i < values.length; i++) {
					query.setParameter(i, values[i]);
				}
				// 返回其执行了分布方法的list
				// if (curPage != -1) {//逻辑不对
				// query.setFirstResult((curPage - 1) * pageSize);
				// }
				query.setFirstResult(0);
				return query.setMaxResults(pageSize).list();
			}
		});
	}

}
