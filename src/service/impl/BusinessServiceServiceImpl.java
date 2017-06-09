package service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.tomcat.util.http.fileupload.IOUtils;

import service.BusinessServiceService;
import util.ImageUtil;
import config.BaseConfiger;
import config.BasicInfoConfig;
import dao.BusinessServiceDAO;
import domain.BusinessService;
import domain.ServiceImage;
import domain.User;

public class BusinessServiceServiceImpl implements BusinessServiceService {

	private BusinessServiceDAO businessServiceDAO;

	private static String root = BaseConfiger.FileSavePath;
	private static String imagePath = BasicInfoConfig.BusinessServiceImageDir;
	private static String smallImagePath = BasicInfoConfig.SmallBusinessServiceImageDir;

	@Override
	public boolean save(BusinessService businessService) {

		businessService.setStatus(0);
		businessService.setFavoriteNumber(0);
		businessService.setGreat(0);

		// 原图
		File dir = new File(root + imagePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		// 缩略图
		File smalldir = new File(root + smallImagePath);
		if (!smalldir.exists()) {
			smalldir.mkdirs();
		}

		Set<ServiceImage> realImages = new HashSet<>();

		Set<ServiceImage> images = businessService.getImages();
		if (images != null) {
			Iterator<ServiceImage> it = images.iterator();
			int index = 1;
			while (it.hasNext()) {
				ServiceImage key = it.next();
				if (key != null) {
					File img = key.getImage();
					key.setService(businessService);
					try {
						String picName = businessService.getId() + System.currentTimeMillis() + index + ".jpg";
						IOUtils.copy(new FileInputStream(img), new FileOutputStream(
								new File(root + imagePath + picName)));
						System.out.println("image save in " + root + imagePath + picName);
						key.setLink("/qianxun" + imagePath + picName);
						ImageUtil.zipTo400(root + imagePath + picName, root + smallImagePath + picName);
					} catch (IOException e) {
						e.printStackTrace();
						return false;
					}
					index++;
					realImages.add(key);
				}
			}
		}
		businessService.setImages(realImages);

		return businessServiceDAO.save(businessService);
	}

	@Override
	public boolean updateStatus(int serviceId, int status) {
		return businessServiceDAO.updateStatus(serviceId, status);
	}

	@Override
	public boolean update(BusinessService businessService) {
		return businessServiceDAO.update(businessService);
	}

	@Override
	public boolean delete(BusinessService businessService) {
		return businessServiceDAO.delete(businessService);
	}

	@Override
	public BusinessService getByBServiceId(int id) {
		return businessServiceDAO.getById(id);
	}

	@Override
	public User getBSsUser(int businessId) {
		return businessServiceDAO.getBSsUser(businessId);
	}

	@Override
	public List<BusinessService> getByType(int page, int category) {
		return businessServiceDAO.getByType(page, category);
	}

	@Override
	public List<User> getFavoritePeople(int serviceId) {
		return businessServiceDAO.getFavoritePeople(serviceId);
	}

	@Override
	public List<BusinessService> getByUserId(int page, int userId) {
		return businessServiceDAO.getByUserId(page, userId);
	}

	@Override
	public List<BusinessService> getLocalByCategory(double x, double y, double scope, int page, int category) {
		return businessServiceDAO.getLocalByCategory(x, y, scope, page, category);
	}

	@Override
	public List<BusinessService> getLatestByCategory(int page, int category) {
		return businessServiceDAO.getLatestByCategory(page, category);
	}

	@Override
	public List<BusinessService> getSchoolByCategory(int page, int category, String school) {
		return businessServiceDAO.getSchoolByCategory(page, category, school);
	}

	@Override
	public List<BusinessService> getSchoolByPage(int page, String school) {
		return businessServiceDAO.getSchoolByPage(page, school);
	}

	@Override
	public List<BusinessService> getLocalByPage(double x, double y, double scope, int page) {
		return businessServiceDAO.getLocalByPage(x, y, scope, page);
	}

	@Override
	public List<BusinessService> getHotByPage(int page) {
		return businessServiceDAO.getHotByPage(page);
	}

	@Override
	public List<BusinessService> getLatestByPage(int page) {
		return businessServiceDAO.getLatestByPage(page);
	}

	/*******************/

	public void setBusinessServiceDAO(BusinessServiceDAO businessServiceDAO) {
		this.businessServiceDAO = businessServiceDAO;
	}

	public BusinessServiceDAO getBusinessServiceDAO() {
		return businessServiceDAO;
	}

}
