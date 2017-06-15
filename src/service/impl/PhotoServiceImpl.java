package service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.transaction.annotation.Transactional;

import service.PhotoService;
import dao.PhotoDAO;
import domain.Photo;
import domain.User;

public class PhotoServiceImpl implements PhotoService {

	private final String bigPath = "/photo/bigPath";
	private final String smallPath = "/photo/smallPath";
	private String root;
	private PhotoDAO photoDAO;

	public PhotoServiceImpl() {
		root = this.getClass().getResource("/").getPath();
		root = root.substring(0, root.length() - 17);
	}

	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> add(User user, File photoFile) {
		Map<String, Object> infoMap = new HashMap<>();
		try {
			Photo photo = new Photo();

			photo.setUser(user);
			photoDAO.save(photo);
			photo.setBigPath(root + bigPath + "/" + photo.getId() + ".png");
			photo.setSmallPath(root + smallPath + "/" + photo.getId() + ".png");
			photoDAO.update(photo);
			IOUtils.copy(new FileInputStream(photoFile), new FileOutputStream(new File(photo.getBigPath())));
			infoMap.put("status", 1);
		} catch (Exception e) {
			e.printStackTrace();
			infoMap.put("status", -1);
		}
		return infoMap;
	}

	public PhotoDAO getPhotoDAO() {
		return photoDAO;
	}

	public void setPhotoDAO(PhotoDAO photoDAO) {
		this.photoDAO = photoDAO;
	}

}
