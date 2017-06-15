package dao;

import java.util.List;

import domain.Photo;

public interface PhotoDAO {
	void save(Photo photo);

	void delete(Photo photo);

	void update(Photo photo);

	Photo findById(int id);

	List<Photo> findAll();
}
