package dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import dao.UserVerifyDAO;
import domain.UserVerify;

public class UserVerifyDAOImpl extends HibernateDaoSupport implements UserVerifyDAO {

	@Override
	public void save(UserVerify uv) {
		this.getHibernateTemplate().save(uv);
	}

	@Override
	public void update(UserVerify uv) {
		this.getHibernateTemplate().update(uv);
	}

	@Override
	public boolean delete(int uvId) {
		String sql = "delete from UserVerify where id = ?";
		Session session = this.getSession();
		Transaction tx = session.beginTransaction();
		try {
			tx.begin();
			session.createQuery(sql);
			tx.commit();
			session.flush();
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
	public UserVerify getById(int id) {
		String hql = "from UserVerify where id = ?";
		return (UserVerify) this.getHibernateTemplate().find(hql, id).get(0);
	}

	@Override
	public List<UserVerify> getByPage(int page) {
		String hql = "from UserVerify uv ,User u where uv.phone=u.username and u.verifyStatus=-1";
		List<UserVerify> list = this.doSplitPage(hql, page, 15);

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
	private List<UserVerify> doSplitPage(final String hql, final int curPage, final int pageSize,
			final Object... values) {
		// 调用模板的execute方法，参数是实现了HibernateCallback接口的匿名类，
		return (List<UserVerify>) super.getHibernateTemplate().execute(new HibernateCallback() {
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
