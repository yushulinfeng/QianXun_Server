package dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import dao.UserCommentDAO;
import domain.UserComment;

public class UserCommentDAOImpl extends HibernateDaoSupport implements UserCommentDAO {

	@Override
	public void save(UserComment userComment) {
		this.getHibernateTemplate().save(userComment);
	}

	@Override
	public void update(UserComment userComment) {
		this.getHibernateTemplate().update(userComment);
	}

	@Override
	public boolean delete(UserComment userComment) {
		Session session = this.getSession();// yong getSessionFactory baocuo
		Transaction tx = session.beginTransaction();
		try {
			tx.begin();
			session.delete(userComment);
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
	public UserComment getUserCommentById(int id) {
		String hql = "from UserComment uc join User u join UserRequest ur where uc.id = ?"
				+ "	and uc.userRequest.id = ur.id ";
		return (UserComment) this.getHibernateTemplate().find(hql, id).get(0);
	}

	@Override
	public UserComment getDetailByRequestId(int id) {
		System.out.println("detailId:" + id);
		String hql = "from UserComment uc where uc.userRequest.id = ?";
		// String hql = "from UserRequest ur where ur.id = ?";
		return (UserComment) this.getHibernateTemplate().find(hql, id).get(0);
	}

	@Override
	public List<UserComment> getBriefByPage(int page) {
		String hql = " from domain.UserComment uc order by uc.comment_great DESC";
		// String hql =
		// "from domain.UserRequest ur where ur.userComment != null ";
		// DetachedCriteria dc = DetachedCriteria.forClass(UserComment.class);
		// dc.add(Restrictions.sqlRestriction(hql));
		System.out.println(page);
		List<UserComment> list = this.doSplitPage(hql, page, 15);
		System.out.println(list.size());
		return list;
	}

	@Override
	public void addCommentGreat(int commentId) {
		String hql = "update UserComment set comment_great = comment_great + 1" + " where id = ?";
		this.getHibernateTemplate().bulkUpdate(hql, commentId);
	}

	/**
	 * 
	 * @param hql传入的hql语句
	 * @param curPage当前页
	 * @param pageSize每页显示大小
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<UserComment> doSplitPage(final String hql, final int curPage, final int pageSize) {
		// 调用模板的execute方法，参数是实现了HibernateCallback接口的匿名类，
		return (List<UserComment>) super.getHibernateTemplate().execute(new HibernateCallback() {
			// 重写其doInHibernate方法返回一个object对象
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				// 创建query对象
				Query query = session.createQuery(hql);
				// 返回其执行了分布方法的list
				return query.setFirstResult((curPage - 1) * pageSize).setMaxResults(pageSize).list();

			}

		});

	}

}
