package dao.impl;

import java.util.List;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import dao.PhotoDAO;
import domain.Photo;

public class PhotoDAOImpl extends HibernateDaoSupport implements PhotoDAO {

	public void save(Photo photo) {
		this.getHibernateTemplate().save(photo);
	}

	public void delete(Photo photo) {
		this.getHibernateTemplate().delete(photo);
	}

	public void update(Photo photo) {
		this.getHibernateTemplate().update(photo);
	}

	public Photo findById(int id) {
		Photo photo = this.getHibernateTemplate().get(Photo.class, id);
		return photo;
	}

	public List<Photo> findAll() {
		String hql = "from Photo";
		List<Photo> photos = this.getHibernateTemplate().find(hql);
		return photos;
	}

}
