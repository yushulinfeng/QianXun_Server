package service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.IOUtils;

import service.DynamicService;
import util.ImageUtil;
import config.BaseConfiger;
import config.BasicInfoConfig;
import dao.DynamicDAO;
import domain.Dynamic;

public class DynamicServiceImpl implements DynamicService {

	private DynamicDAO dynamicDAO;

	private static String root = BaseConfiger.FileSavePath;
	private static String imagePath = BasicInfoConfig.DynamicImageDir;
	private static String smallInagePath = BasicInfoConfig.SmallDynamicImageDir;

	@Override
	public boolean save(Dynamic dynamic) {

		// 原图
		File dir = new File(root + imagePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		// 缩略图
		File smalldir = new File(root + smallInagePath);
		if (!smalldir.exists()) {
			smalldir.mkdirs();
		}
		// 处理图片
		try {
			dynamic.setStatus(0);
			dynamic.setTrample(0);
			dynamic.setSupport(0);
			if (!"".equals(dynamic.getImg1()) && dynamic.getImg1() != null && !"null".equals(dynamic.getImg1())) {
				String picName = dynamic.getId() + System.currentTimeMillis() + "1.jpg";
				IOUtils.copy(new FileInputStream(dynamic.getImg1()), new FileOutputStream(new File(root + imagePath
						+ picName)));
				dynamic.setPic1("/qianxun" + imagePath + picName);
				boolean flag = ImageUtil.zipTo420x240(root + imagePath + picName, root + smallInagePath + picName);
				if (!flag) {
					throw new Exception("image1 zip failed!");
				}
			}
			if (!"".equals(dynamic.getImg2()) && dynamic.getImg2() != null && !"null".equals(dynamic.getImg2())) {
				String picName2 = dynamic.getId() + System.currentTimeMillis() + "2.jpg";
				IOUtils.copy(new FileInputStream(dynamic.getImg2()), new FileOutputStream(new File(root + imagePath
						+ picName2)));
				dynamic.setPic2("/qianxun" + imagePath + picName2);
				boolean flag = ImageUtil.zipTo400(root + imagePath + picName2, root + smallInagePath + picName2);
				if (!flag) {
					throw new Exception("image2 zip failed!");
				}
			}
			if (!"".equals(dynamic.getImg3()) && dynamic.getImg3() != null && !"null".equals(dynamic.getImg3())) {
				String picName3 = dynamic.getId() + System.currentTimeMillis() + "3.jpg";
				IOUtils.copy(new FileInputStream(dynamic.getImg3()), new FileOutputStream(new File(root + imagePath
						+ picName3)));
				dynamic.setPic3("/qianxun" + imagePath + picName3);
				boolean flag = ImageUtil.zipTo400(root + imagePath + picName3, root + smallInagePath + picName3);
				if (!flag) {
					throw new Exception("image3 zip failed!");
				}
			}
			return dynamicDAO.save(dynamic);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public boolean update(Dynamic dynamic) {
		return dynamicDAO.update(dynamic);
	}

	@Override
	public boolean delete(Dynamic dynamic) {
		return dynamicDAO.delete(dynamic);
	}

	@Override
	public Dynamic getDynamicById(int id) {
		return dynamicDAO.getById(id);
	}

	@Override
	public boolean addSupport(int dynamicId) {
		return dynamicDAO.support(dynamicId);
	}

	@Override
	public boolean addTrample(int dynamicId) {
		return dynamicDAO.trample(dynamicId);
	}

	@Override
	public List<Dynamic> getDynamicsByIdAndPage(int userId, int page) {
		try {
			return dynamicDAO.getByUserIdAndPage(userId, page);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Dynamic> getBriefByUserId(int page, int userId) {
		return dynamicDAO.getBriefByUserId(page, userId);
	}

	@Override
	public List<Dynamic> getBriefByPage(int page, int pageSize) {
		return dynamicDAO.getBriefByPage(page, pageSize);
	}

	/***********************/

	public DynamicDAO getDynamicDAO() {
		return dynamicDAO;
	}

	public void setDynamicDAO(DynamicDAO dynamicDAO) {
		this.dynamicDAO = dynamicDAO;
	}
}
