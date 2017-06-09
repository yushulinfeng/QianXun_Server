package service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.IOUtils;

import service.UserRequestService;
import util.ImageUtil;
import config.BaseConfiger;
import config.BasicInfoConfig;
import dao.UserRequestDAO;
import domain.User;
import domain.UserRequest;

public class UserRequestServiceImpl implements UserRequestService {

	private UserRequestDAO userRequestDAO;

	private static final String ImagePath = BasicInfoConfig.UserRequestImageDir;
	private static final String SmallImagePath = BasicInfoConfig.SmallUserRequestImageDir;
	private String root = BaseConfiger.FileSavePath;

	@Override
	public boolean save(UserRequest userRequest) {

		System.out.println(userRequest.getImg1());
		System.out.println(userRequest.getImg2());
		System.out.println(userRequest.getImg3());

		// 原图
		File dir = new File(root + ImagePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		// 缩略图
		File smalldir = new File(root + SmallImagePath);
		if (!smalldir.exists()) {
			smalldir.mkdirs();
		}

		try {
			userRequest.setStatus(0);
			userRequest.setReport(0);
			// 处理图片
			if (!"".equals(userRequest.getImg1()) && !"null".equals(userRequest.getImg1())
					&& userRequest.getImg1() != null) {
				String picName = userRequest.getId() + System.currentTimeMillis() + "1.jpg";
				IOUtils.copy(new FileInputStream(userRequest.getImg1()), new FileOutputStream(new File(root + ImagePath
						+ picName)));
				userRequest.setRequest_picture("/qianxun" + ImagePath + picName);
				;
				boolean flag = ImageUtil.zipTo420x240(root + ImagePath + picName, root + SmallImagePath + picName);
				if (!flag) {
					throw new Exception("image1 zip failed!");
				}
			}
			if (!"".equals(userRequest.getImg2()) && !"null".equals(userRequest.getImg2())
					&& userRequest.getImg2() != null) {
				String picName2 = userRequest.getId() + System.currentTimeMillis() + "2.jpg";
				IOUtils.copy(new FileInputStream(userRequest.getImg2()), new FileOutputStream(new File(root + ImagePath
						+ picName2)));
				userRequest.setRequest_picture2("/qianxun" + ImagePath + picName2);
				;
				boolean flag = ImageUtil.zipTo420x240(root + ImagePath + picName2, root + SmallImagePath + picName2);
				if (!flag) {
					throw new Exception("image2 zip failed!");
				}
			}
			if (!"".equals(userRequest.getImg3()) && !"null".equals(userRequest.getImg3())
					&& userRequest.getImg3() != null) {
				String picName3 = userRequest.getId() + System.currentTimeMillis() + "3.jpg";
				IOUtils.copy(new FileInputStream(userRequest.getImg3()), new FileOutputStream(new File(root + ImagePath
						+ picName3)));
				userRequest.setRequest_picture3("/qianxun" + ImagePath + picName3);
				;
				boolean flag = ImageUtil.zipTo420x240(root + ImagePath + picName3, root + SmallImagePath + picName3);
				if (!flag) {
					throw new Exception("image3 zip failed!");
				}
			}
			userRequestDAO.save(userRequest);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean delete(UserRequest userRequest) {
		return userRequestDAO.delete(userRequest);
	}

	@Override
	public List<UserRequest> getByUserIdAndType(int page, int userId, int status) {
		return userRequestDAO.getByUserIdAndType(page, userId, status);
	}

	@Override
	public List<UserRequest> findLocalRequest(double x, double y, double scope, int page) {
		List<UserRequest> list = null;
		try {
			list = userRequestDAO.findLocalRequest(x, y, scope, page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public boolean addReport(int requestId) {
		try {
			userRequestDAO.addReport(requestId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<UserRequest> getAllByUserId(int userId, int page) {
		return userRequestDAO.getAllByUserId(page, userId);
	}

	@Override
	public List<UserRequest> getRelatedById(int userId, int page) {
		return userRequestDAO.getRelatedReqs(page, userId);
	}

	@Override
	public List<UserRequest> getRelatedById(int userId) {
		return userRequestDAO.getRelatedReqs(userId);
	}

	@Override
	public List<UserRequest> getRequestByType(double x, double y, double scope, int page, int type) {
		List<UserRequest> list = null;
		try {
			list = userRequestDAO.getRequestsByType(x, y, scope, page, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public void update(UserRequest userRequest) {
		userRequestDAO.update(userRequest);
	}

	@Override
	public List<UserRequest> getUsersRequests(long username) {
		List<UserRequest> list = null;
		try {
			list = userRequestDAO.getUsersRequests(username);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public UserRequest getUserRequestById(int id) {
		UserRequest u = null;
		try {
			u = userRequestDAO.getUserRequestById(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return u;
	}

	@Override
	public User getFinalReceiverByReqId(int id) {
		User u = null;
		try {
			u = userRequestDAO.getFinalReceiverByReqId(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return u;
	}

	@Override
	public UserRequest getUserRequestByComId(int id) {
		UserRequest u = null;
		try {
			u = userRequestDAO.getUserRequestByComId(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return u;
	}

	@Override
	public List<UserRequest> getUserToDoRequests(long username) {
		List<UserRequest> list = null;
		try {
			list = userRequestDAO.getUserToDoRequests(username);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public UserRequest getNewestRequestByUsername(long username) {
		try {
			return userRequestDAO.getNewestUrByUsername(username);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<UserRequest> getUserReceivedRequest(long username) {
		List<UserRequest> list = null;
		try {
			list = userRequestDAO.getUserReceivedRequest(username);
			for (UserRequest u : list) {// 删除已经删除的东西
				if (u.getStatus() == 4) {
					list.remove(u);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<UserRequest> getUserRequestByPage(int page) {
		List<UserRequest> list = null;
		try {
			list = userRequestDAO.getUserRequestByPage(page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<User> getReceiversByReqId(int id) {
		List<User> list = null;
		try {
			list = userRequestDAO.getReceivers(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/*******************************************/
	public void setUserRequestDAO(UserRequestDAO userRequestDAO) {
		this.userRequestDAO = userRequestDAO;
	}

	public UserRequestDAO getUserRequestDAO() {
		return userRequestDAO;
	}

}
