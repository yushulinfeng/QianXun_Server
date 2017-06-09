package dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import dao.StudentVerifyDAO;
import domain.StudentVerify;

public class StudentVerifyDAOImpl extends HibernateDaoSupport implements StudentVerifyDAO {

	@Override
	public void save(StudentVerify sv) {
		Session session = this.getSession();
		Transaction tx = session.beginTransaction();
		try {
			tx.begin();
			session.save(sv);
			session.flush();
			tx.commit();
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
	public void delete(StudentVerify sv) {
		this.getHibernateTemplate().delete(sv);
	}

	@Override
	public boolean delete(int uvId) {
		String sql = "delete from StudentVerify where id = ?";
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
	public void update(StudentVerify sv) {
		this.getHibernateTemplate().update(sv);
	}

	@Override
	public StudentVerify getById(int id) {
		String hql = "from StudentVerify where id = ?";
		return (StudentVerify) this.getHibernateTemplate().find(hql, id).get(0);
	}

	@Override
	public List<StudentVerify> getByPage(int page) {
		String hql = "from StudentVerify sv,User u where sv.phone=u.username and u.verifyStatus=-2";
		List<StudentVerify> list = this.doSplitPage(hql, page, 15);

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
	private List<StudentVerify> doSplitPage(final String hql, final int curPage, final int pageSize,
			final Object... values) {
		// 调用模板的execute方法，参数是实现了HibernateCallback接口的匿名类，
		return (List<StudentVerify>) super.getHibernateTemplate().execute(new HibernateCallback() {
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
