package service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import service.UserVerifyService;
import util.DecodeUtil;
import config.BaseConfiger;
import config.BasicInfoConfig;
import dao.UserVerifyDAO;
import domain.UserVerify;

public class UserVerifyServiceImpl implements UserVerifyService {

	private UserVerifyDAO userVerifyDAO;
	private String root = BaseConfiger.FileSavePath;
	private String IDImageDir = BasicInfoConfig.IDImageDir;

	@Override
	public boolean saveUserVerify(UserVerify uv) {
		File dir = new File(root + IDImageDir);
		System.out.println(dir.getAbsoluteFile());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		try {
			System.out.println("IDImage:" + uv.getIDImage());
			if (!"null".equals(uv.getIDImage()) && uv.getIDImage() != null && !"".equals(uv.getIDImage())) {
				String picName = uv.getPhone() + System.currentTimeMillis() + ".jpg";
				FileUtils.writeByteArrayToFile(new File(root + IDImageDir + picName),
						DecodeUtil.decodeImageString(uv.getIDImage()));
				uv.setIDImage("/qianxun" + IDImageDir + picName);
				System.out.println("IDImage save Dir:" + root + IDImageDir + picName);
			} else if (uv.getImageFile() != null) {
				String picName = uv.getId() + System.currentTimeMillis() + ".jpg";
				IOUtils.copy(new FileInputStream(uv.getImageFile()), new FileOutputStream(new File(root + IDImageDir
						+ picName)));
				uv.setIDImage("/qianxun" + IDImageDir + picName);
			}
			userVerifyDAO.save(uv);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	@Override
	public void updateUserVerify(UserVerify uv) {
		userVerifyDAO.update(uv);
	}

	@Override
	public boolean delete(int uvId) {
		return userVerifyDAO.delete(uvId);
	}

	@Override
	public UserVerify getUserVerifyInfoById(int id) {
		return userVerifyDAO.getById(id);
	}

	@Override
	public List<UserVerify> getUserInfoByPage(int page) {
		try {
			return userVerifyDAO.getByPage(page);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/******************/
	public UserVerifyDAO getUserVerifyDAO() {
		return userVerifyDAO;
	}

	public void setUserVerifyDAO(UserVerifyDAO userVerifyDAO) {
		this.userVerifyDAO = userVerifyDAO;
	}

}
