package service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import service.StudentVerifyService;
import util.DecodeUtil;
import config.BaseConfiger;
import config.BasicInfoConfig;
import dao.StudentVerifyDAO;
import domain.StudentVerify;

public class StudentVerifyServiceImpl implements StudentVerifyService {

	private StudentVerifyDAO studentVerifyDAO;

	private String root = BaseConfiger.FileSavePath;
	private String stuIDImageDir = BasicInfoConfig.stuIDImageDir;

	@Override
	public boolean saveStudentVerify(StudentVerify sv) {
		File dir = new File(root + stuIDImageDir);
		System.out.println(dir.getAbsoluteFile());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		try {
			if (!"null".equals(sv.getStuIDImage()) && sv.getStuIDImage() != null && !"".equals(sv.getStuIDImage())) {
				String picName = sv.getPhone() + System.currentTimeMillis() + ".jpg";
				FileUtils.writeByteArrayToFile(new File(root + stuIDImageDir + picName),
						DecodeUtil.decodeImageString(sv.getStuIDImage()));
				sv.setStuIDImage("/qianxun" + stuIDImageDir + picName);
			} else if (sv.getImageFile() != null) {
				String picName = sv.getId() + System.currentTimeMillis() + ".jpg";
				IOUtils.copy(new FileInputStream(sv.getImageFile()), new FileOutputStream(new File(root + stuIDImageDir
						+ picName)));
				sv.setStuIDImage("/qianxun" + stuIDImageDir + picName);
			}

			studentVerifyDAO.save(sv);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	@Override
	public void updateStudentVerify(StudentVerify sv) {
		studentVerifyDAO.update(sv);
	}

	@Override
	public StudentVerify getStuVerifyInfoById(int id) {
		return studentVerifyDAO.getById(id);
	}

	@Override
	public List<StudentVerify> getByPage(int page) {
		return studentVerifyDAO.getByPage(page);
	}

	/******************/
	public StudentVerifyDAO getStudentVerifyDAO() {
		return studentVerifyDAO;
	}

	public void setStudentVerifyDAO(StudentVerifyDAO studentVerifyDAO) {
		this.studentVerifyDAO = studentVerifyDAO;
	}

}
