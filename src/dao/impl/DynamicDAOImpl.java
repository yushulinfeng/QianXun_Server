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

import dao.DynamicDAO;
import domain.Dynamic;

public class DynamicDAOImpl extends HibernateDaoSupport implements DynamicDAO {

	@Override
	public boolean save(Dynamic dynamic) {
		try {
			this.getHibernateTemplate().save(dynamic);
			return true;
		} catch (DataAccessException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean update(Dynamic dynamic) {
		try {
			this.getHibernateTemplate().update(dynamic);
			return true;
		} catch (DataAccessException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean delete(Dynamic dynamic) {
		// this.getHibernateTemplate().delete(dynamic);
		Session session = this.getSession();
		Transaction tx = session.beginTransaction();
		try {
			tx.begin();
			session.delete(dynamic);
			session.flush();
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
	public Dynamic getById(int id) {
		String hql = "from Dynamic d where d.id = ?";
		try {
			// return this.getHibernateTemplate().get(Dynamic.class,
			// id);//会级联所有操作
			return (Dynamic) this.getHibernateTemplate().find(hql, id).get(0);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean support(int dynamicId) {
		String hql = "update Dynamic d set d.support = d.support + 1 where d.id = ?";
		try {
			this.getHibernateTemplate().bulkUpdate(hql, dynamicId);
			return true;
		} catch (DataAccessException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean trample(int dynamicId) {
		String hql = "update Dynamic d set d.trample = d.trample + 1 where d.id = ?";
		try {
			this.getHibernateTemplate().bulkUpdate(hql, dynamicId);
			return true;
		} catch (DataAccessException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<Dynamic> getByUserIdAndPage(int id, int page) {
		String hql = "from Dynamic d where d.user.id = ? order by d.id DESC ";
		return doSplitPage(hql, page, 9, id);
	}

	@Override
	public List<Dynamic> getBriefByUserId(int page, int userId) {
		String hql = "select new Dynamic(d.id, d.pic1, d.pic2, d.pic3, d.content, d.publishTime) from Dynamic d where d.user.id = ? order by d.id DESC ";
		try {
			return doSplitPage(hql, page, 9, userId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Dynamic> getBriefByPage(int page, int pageSize) {
		// String hql =
		// "select d.user.id,d.user.headIcon,d.user.username,d.user.nickName,d.id,d.pic1,d.content from Dynamic d order by d.support DESC";
		String hql = "select new Dynamic(d.user.id,d.user.headIcon,d.user.username,d.user.nickName,d.id,d.pic1,d.content) from Dynamic d where d.pic1 != NULL and d.pic1 != '' order by d.publishTime";
		return this.doSplitPage(hql, page, pageSize);
	}

	/**
	 * 
	 * @param hql 传入的hql语句
	 * @param curPage 当前页
	 * @param pageSize 每页显示大小
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Dynamic> doSplitPage(final String hql, final int curPage, final int pageSize, final Object... values) {
		// 调用模板的execute方法，参数是实现了HibernateCallback接口的匿名类，
		return (List<Dynamic>) super.getHibernateTemplate().execute(new HibernateCallback() {
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
