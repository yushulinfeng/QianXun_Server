package service;

import java.util.List;

import domain.Dynamic;

public interface DynamicService {

	boolean save(Dynamic dynamic);

	boolean update(Dynamic dynamic);

	boolean delete(Dynamic dynamic);

	Dynamic getDynamicById(int id);

	List<Dynamic> getDynamicsByIdAndPage(int userId, int page);

	List<Dynamic> getBriefByPage(int page, int pageSize);

	boolean addSupport(int dynamicId);

	boolean addTrample(int dynamicId);

	List<Dynamic> getBriefByUserId(int page, int userId);

}
