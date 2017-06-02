package service;

import java.util.List;

import domain.BusinessService;
import domain.User;

public interface BusinessServiceService {

	boolean save(BusinessService businessService);

	boolean update(BusinessService businessService);

	boolean delete(BusinessService businessService);

	BusinessService getByBServiceId(int id);

	List<BusinessService> getLocalByPage(double x, double y, double scope, int page);

	List<BusinessService> getHotByPage(int page);

	List<BusinessService> getLatestByPage(int page);

	List<User> getFavoritePeople(int serviceId);

	List<BusinessService> getByType(int page, int category);

	List<BusinessService> getByUserId(int page, int userId);

	List<BusinessService> getLocalByCategory(double x, double y, double scope, int page, int category);

	List<BusinessService> getLatestByCategory(int page, int category);

	User getBSsUser(int businessId);

	boolean updateStatus(int serviceId, int status);

	List<BusinessService> getSchoolByCategory(int page, int category, String school);

	List<BusinessService> getSchoolByPage(int page, String school);

}
