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

import dao.UserRequestDAO;
import domain.User;
import domain.UserRequest;

public class UserRequestDAOImpl extends HibernateDaoSupport implements UserRequestDAO {

	@Override
	public void save(UserRequest userRequest) {
		// this.getHibernateTemplate().save(userRequest);
		System.out.println("saving..");
		this.getHibernateTemplate().clear();
		Session session = this.getSession();
		Transaction tx = session.beginTransaction();
		try {
			tx.begin();
			session.save(userRequest);
			tx.commit();
			session.flush();
			session.close();
			// releaseSession(session);
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
	public List<UserRequest> getRequestsByType(double x, double y, double scope, int page, int type) {

		String hql = "from UserRequest ur where ur.id< ? and (ur.type = ?) and ((ROUND((2 * ASIN(SQRT(POW(SIN((ur.startLocation_y * PI() / 180.0 - ? * PI() / 180.0) / 2), 2) + COS(ur.startLocation_y * PI() / 180.0) * COS(? * PI() / 180.0 * PI() / 180.0) * POW(SIN((ur.startLocation_x * PI() / 180.0 - ? * PI() / 180.0) / 2), 2)))) * 6378.137 * 10000) / 10000)<(?*1.0)) and (ur.status in (0,1,2,3)) order by ur.id DESC";
		// 此处返回值以km为单位
		// String hql =
		// "from UserRequest ur where ur.id< ? and (ur.type = ?) and ((ROUND((2 * ASIN(SQRT(POW(SIN(ur.startLocation_y*PI()/180.0 - ?*PI()/180.0/2),2)+COS(ur.startLocation_y*PI()/180.0)*COS(?*PI()/180.0)*POW(SIN(ur.startLocation_x*PI()/180.0 - ?*PI()/180.0/2),2))))*6378.137*10000) / 10000)<(?*1.0)) and (ur.status in (0,1,2,3)) order by ur.id DESC ";

		System.out.println("x:" + x + " y:" + y + " scpoe:" + scope + " page:" + page + " type:" + type);
		if (page == -1) {
			hql = "from UserRequest ur where (ur.type = ?) and ((ROUND((2 * ASIN(SQRT(POW(SIN((ur.startLocation_y * PI() / 180.0 - ? * PI() / 180.0) / 2), 2) + COS(ur.startLocation_y * PI() / 180.0) * COS(? * PI() / 180.0 * PI() / 180.0) * POW(SIN((ur.startLocation_x * PI() / 180.0 - ? * PI() / 180.0) / 2), 2)))) * 6378.137 * 10000) / 10000)<(?*1.0)) and (ur.status in (0,1,2,3)) order by ur.id DESC";
			List<UserRequest> list = this.doSplitPage(hql, page, 5, type, y, y, x, scope);
			for (UserRequest u : list) {
				User user = u.getUser();
				System.out.println("userrequest " + u.getId() + " 's userId is " + user.getId());
			}
			return list;
		}
		List<UserRequest> list = this.doSplitPageById(hql, page, 10, page, type, y, y, x, scope);
		for (UserRequest u : list) {
			User user = u.getUser();
			System.out.println("userrequest " + u.getId() + " 's userId is " + user.getId());

		}
		return list;
	}

	@Override
	public void update(UserRequest userRequest) {
		System.out.println(userRequest.getId());
		// this.getHibernateTemplate().merge(userRequest);
		System.out.println("updating..");
		this.getHibernateTemplate().clear();
		Session session = this.getSession();
		Transaction tx = session.beginTransaction();
		try {
			tx.begin();
			session.update(userRequest);
			tx.commit();
			session.flush();
			session.close();
			// releaseSession(session);
		} catch (DataAccessResourceFailureException e) {
			tx.rollback();
			e.printStackTrace();
		} catch (DataAccessException e) {
			tx.rollback();
			e.printStackTrace();
		} catch (IllegalStateException e) {
			tx.rollback();
			e.printStackTrace();
		} catch (HibernateException e) {
			tx.rollback();
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
	public boolean delete(UserRequest userRequest) {
		Session session = this.getSession();// yong getSessionFactory baocuo
		Transaction tx = session.beginTransaction();
		try {
			tx.begin();
			session.delete(userRequest);
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
	public List<UserRequest> getByUserIdAndType(int page, int userId, int status) {
		String hql = "from UserRequest ur where ur.user.id = ? and ur.status = ? and ur.status != 4";
		try {
			return this.doSplitPage(hql, page, 9, userId, status);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<UserRequest> getAllByUserId(int page, int userId) {
		// String hql =
		// "from UserRequest ur where (ur.user.id = ? or ur.finalReceiver.id = ? or ((from User u where u.id = ?) in elements(ur.receivers))) and ur.status != 4";
		String hql = "from UserRequest ur where ur.user.id = ? and ur.status != 4 order by ur.id";
		try {
			return this.doSplitPage(hql, page, 9, userId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<UserRequest> getRelatedReqs(int page, int userId) {
		String hql = "from UserRequest ur where (ur.user.id = ? or ur.finalReceiver.id = ? or ((from User u where u.id = ?) in elements(ur.receivers))) and ur.status != 4";
		try {
			return this.doSplitPage(hql, page, 9, userId, userId, userId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<UserRequest> getRelatedReqs(int userId) {
		String hql = "from UserRequest ur where (ur.user.id = ? or ur.finalReceiver.id = ? or ((from User u where u.id = ?) in elements(ur.receivers))) and ur.status != 4";
		try {
			List<UserRequest> list = this.getHibernateTemplate().find(hql, userId, userId, userId);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public UserRequest getNewestUrByUsername(long username) {
		String hql = "from UserRequest ur where ur.user.username = ?  and (ur.status != 4) order by ur.id DESC ";
		UserRequest ur = (UserRequest) this.getHibernateTemplate().find(hql, username).get(0);
		ur.getUser().getConcernedPeople();// 用此语句让hibernate懒加载出来
		return ur;
	}

	// 接单的需求
	@Override
	public List<UserRequest> getUserToDoRequests(long username) {
		String hql = "from UserRequest ur where ur.finalReceiver.username = ? and (ur.status != 4) ";
		return this.getHibernateTemplate().find(hql, username);
	}

	// 正在报名中的需求
	@Override
	public List<UserRequest> getUserReceivedRequest(long username) {
		String hql = "select receivedRequest from domain.User u where u.username = ? ";
		return this.getHibernateTemplate().find(hql, username);
	}

	// 该用户发的需求
	public List<UserRequest> getUsersRequests(long username) {
		String hql = "from UserRequest ur where ur.user.username = ? and (ur.status != 4) ";
		return this.getHibernateTemplate().find(hql, username);
	}

	// 举报数+1
	@Override
	public void addReport(int requestId) {
		String hql = "update UserRequest ur set ur.report = ur.report + 1 where ur.id = ? and (ur.status != 4) ";
		this.getHibernateTemplate().bulkUpdate(hql, requestId);
	}

	@Override
	public List<UserRequest> findLocalRequest(double x, double y, double scope, int page) {
		// String hql =
		// "select new UserRequest(ur.id,ur.user.username,ur.user.headIcon,ur.user.nickName,ur.user.gender,ur.user.rank_credit,"
		// +
		// "ur.request_content,ur.request_key,ur.reward_money,ur.reward_thing,"
		// +
		// "ur.request_picture,ur.request_picture2,ur.request_picture3,ur.distance,ur.request_postTime)"
		// + " from UserRequest ur where "
		// + " (ACOS(sin(PI()/180*ur.startLocation_y)*sin(PI()/180*?) +"
		// + " cos(PI()/180*ur.startLocation_y)*cos(PI()/180*?)"
		// +
		// "*cos( PI()/180*? - PI()/180*ur.startLocation_x ))*6378.137) < ? ORDER BY ur.id";

		String hql = " from UserRequest ur where ur.id<? and (( ROUND( 2 * ASIN(SQRT(POW(SIN((ur.startLocation_y* PI() /180.0- ? * PI() / 180.0) / 2),2) +COS(ur.startLocation_y * PI() / 180.0)*COS( ? * PI() / 180.0)*POW(SIN((ur.startLocation_x*PI()/180.0-?*PI()/180.0)/2),2)))*6378.137*10000) / 10000) < (?*1.0) ) and (ur.status != 4) order by ur.id DESC ";

		if (page == -1) {
			hql = " from UserRequest ur where (( ROUND( 2 * ASIN(SQRT(POW(SIN((ur.startLocation_y* PI() /180.0- ? * PI() / 180.0) / 2),2) +COS(ur.startLocation_y * PI() / 180.0)*COS( ? * PI() / 180.0)*POW(SIN((ur.startLocation_x*PI()/180.0-?*PI()/180.0)/2),2)))*6378.137*10000) / 10000) < (?*1.0) ) and (ur.status != 4) order by ur.id DESC ";
			return this.doSplitPage(hql, page, 10, y, y, x, scope);
		}
		System.out.println(3600 * 30.887 + "|" + Math.cos(y));
		System.out.println(x + "," + y);

		return this.doSplitPageById(hql, page, 10, page, y, y, x, scope);
	}

	@Override
	public UserRequest getUserRequestById(int requestId) {
		String hql = "from UserRequest ur where id = ? and ur.user.username!=null and (ur.status != 4) ";
		UserRequest u = (UserRequest) this.getHibernateTemplate().find(hql, requestId).get(0);
		return u;
	}

	@Override
	public User getFinalReceiverByReqId(int requestId) {
		String hql = "select ur.finalReceiver from UserRequest ur where ur.id = ? and (ur.status != 4)";
		User u = (User) this.getHibernateTemplate().find(hql, requestId).get(0);
		return u;
	}

	@Override
	public UserRequest getUserRequestByComId(int comId) {
		String hql = "from UserRequest ur where ur.userComment.id = ? and (ur.status != 4) ";
		return (UserRequest) this.getHibernateTemplate().find(hql, comId).get(0);
	}

	@Override
	public List<UserRequest> getUserRequestByPage(int page) {
		String hql = "from UserRequest ur where ur.id<? and (ur.status != 4) order by ur.id DESC";

		if (page == -1) {
			hql = "from UserRequest ur where (ur.status != 4) order by ur.id DESC";
			return this.doSplitPage(hql, page, 10);
		}
		return this.doSplitPageById(hql, page, 10, page);
	}

	@Override
	public List<User> getReceivers(int id) {
		String hql = "select receivers from UserRequest ur where ur.id = ? and (ur.status != 4) ";
		Session session = this.getSession();
		Transaction tx = session.beginTransaction();
		List<User> users = null;
		try {
			tx.begin();
			Query q = session.createQuery(hql);
			q.setParameter(0, id);
			users = (List) q.list();
			tx.commit();
			session.flush();
			session.clear();
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
		return users;
		// return this.getHibernateTemplate().find(hql, id);
	}

	/**
	 * @param hql传入的hql语句
	 * @param curPage当前页
	 * @param pageSize每页显示大小
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<UserRequest> doSplitPage(final String hql, final int curPage, final int pageSize,
			final Object... values) {
		// 调用模板的execute方法，参数是实现了HibernateCallback接口的匿名类，
		return (List<UserRequest>) super.getHibernateTemplate().execute(new HibernateCallback() {
			// 重写其doInHibernate方法返回一个object对象
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				// 创建query对象
				Query query = session.createQuery(hql);
				for (int i = 0; i < values.length; i++) {
					query.setParameter(i, values[i]);
				}
				// 返回其执行了分布方法的list
				query.setFirstResult((curPage - 1) * pageSize).setMaxResults(pageSize);
				List<UserRequest> list = query.list();
				return list;
			}
		});
	}

	@SuppressWarnings("unchecked")
	private List<UserRequest> doSplitPageById(final String hql, final int curPage, final int pageSize,
			final Object... values) {
		// 调用模板的execute方法，参数是实现了HibernateCallback接口的匿名类，
		return (List<UserRequest>) super.getHibernateTemplate().execute(new HibernateCallback() {
			// 重写其doInHibernate方法返回一个object对象
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				// 创建query对象
				Query query = session.createQuery(hql);
				for (int i = 0; i < values.length; i++) {
					query.setParameter(i, values[i]);
				}
				// 返回其执行了分布方法的list
				query.setFirstResult(0).setMaxResults(pageSize);
				List<UserRequest> list = query.list();
				return list;
			}
		});
	}

}
