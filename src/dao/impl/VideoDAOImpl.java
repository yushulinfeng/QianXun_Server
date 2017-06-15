package dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import dao.VideoDAO;
import domain.Video;

public class VideoDAOImpl extends HibernateDaoSupport implements VideoDAO {

	@Override
	public void save(Video video) {
		this.getHibernateTemplate().save(video);
	}

	@Override
	public Video getVideo(int id) {
		String hql = "from Video where id = ?";
		return (Video) this.getHibernateTemplate().find(hql, id).get(0);
	}

	@Override
	public void addVideoGreat(int videoId) {
		String hql = "update Video set great = great + 1" + " where id = ?";
		this.getHibernateTemplate().bulkUpdate(hql, videoId);
	}

	@Override
	public List<Video> getAll() {
		String hql = "from Video";
		return this.getHibernateTemplate().find(hql);
	}

	@Override
	public List<Video> getVideoByPage(int page) {
		String hql = "from Video ";// order by id DESC
		List<Video> list = this.doSplitPage(hql, page, 15);

		System.out.println("pageSize:" + list.size());

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
	private List<Video> doSplitPage(final String hql, final int curPage, final int pageSize, final Object... values) {
		// 调用模板的execute方法，参数是实现了HibernateCallback接口的匿名类，
		return (List<Video>) super.getHibernateTemplate().execute(new HibernateCallback() {
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
