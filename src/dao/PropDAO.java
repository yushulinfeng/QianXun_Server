package dao;

import java.util.List;

import domain.PropImage;

public interface PropDAO {

	void save();

	List<PropImage> getLinks();

	boolean update(PropImage prop);

	List<PropImage> getSmallLinks();

}
