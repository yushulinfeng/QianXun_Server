package dao;

import java.util.List;

import domain.Dynamic;

public interface DynamicDAO {

	boolean save(Dynamic dynamic);

	boolean update(Dynamic dynamic);

	boolean delete(Dynamic dynamic);

	Dynamic getById(int id);

	List<Dynamic> getByUserIdAndPage(int id, int page);

	List<Dynamic> getBriefByPage(int page, int pageSize);

	boolean trample(int dynamicId);

	boolean support(int dynamicId);

	List<Dynamic> getBriefByUserId(int page, int userId);

}
