package service.impl;

import config.BaseConfiger;
import config.BasicInfoConfig;
import dao.UserAccountDAO;
import dao.UserDAO;
import domain.BusinessService;
import domain.User;
import domain.UserAccount;
import domain.UserRequest;
import service.UserService;
import util.BaiChuanUtils;
import util.CheckUtil;
import util.ImageUtil;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserServiceImpl implements UserService {

	private UserDAO userDAO;
	private UserAccountDAO userAccountDAO;

	private String root = BaseConfiger.FileSavePath;
	private final String dir = "/photos";
	private final String headIconDir = BasicInfoConfig.headIconDir;
	private final String SmallheadIconDir = BasicInfoConfig.SmallheadIconDir;

	private final String homePageImageDir = BasicInfoConfig.homePageImageDir;
	private final String SmallhomePageImageDir = BasicInfoConfig.SmallhomePageImageDir;

	@Override
	public void update(User user) {
		userDAO.update(user);
	}

	@Override
	public void updateLocation(long username, String x, String y) {
		userDAO.updateLocation(username, x, y);
	}

	@Override
	public int getIdByUsername(long username) {
		return userDAO.getIdByUsername(username);
	}

	@Override
	public boolean updateUserHeadIcon(int userId, File pic) {

		File userDir = new File(root + headIconDir);
		if (!userDir.exists()) {
			userDir.mkdirs();
		}
		userDir = new File(root + SmallheadIconDir);
		if (!userDir.exists()) {
			userDir.mkdirs();
		}

		try {
			if (!"".equals(pic) && !"null".equals(pic) && pic != null) {
				String picName = userId + System.currentTimeMillis() + ".png";
				String picPath = "/qianxun" + headIconDir + picName;

				File file = new File(root + headIconDir + picName);
				// 得到图片保存的位置(根据root来得到图片保存的路径在tomcat下的该工程里)
				System.out.println("headIcon saved path:" + root + headIconDir + picName);

				InputStream is = new FileInputStream(pic);

				// 把图片写入到上面设置的路径里
				OutputStream os = new FileOutputStream(file);
				byte[] buffer = new byte[400];
				int length = 0;
				while ((length = is.read(buffer)) > 0) {
					os.write(buffer, 0, length);
				}
				is.close();
				os.close();

				ImageUtil.zipTo400(root + headIconDir + picName, root + SmallheadIconDir + picName);
				boolean flag = userDAO.updateUserHeadIcon(userId, picPath);
				if (flag) {
					BaiChuanUtils.updateUser(userId, null, BasicInfoConfig.HOST + picPath);
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	@Override
	public boolean updateUserBackGroundImage(int userId, File pic) {

		File userDir = new File(root + homePageImageDir);
		if (!userDir.exists()) {
			userDir.mkdirs();
		}
		userDir = new File(root + SmallhomePageImageDir);
		if (!userDir.exists()) {
			userDir.mkdirs();
		}

		try {
			if (!"".equals(pic) && !"null".equals(pic) && pic != null) {
				String picName = userId + System.currentTimeMillis() + "_background.jpg";
				String picPath = "/qianxun" + homePageImageDir + picName;
				////IOUtils.copy(new FileInputStream(pic), new FileOutputStream(root + homePageImageDir + picName));
				ImageUtil.zipTo400(root + homePageImageDir + picName, root + SmallheadIconDir + picName);
				boolean flag = userDAO.updateUserBackGroundImage(userId, picPath);
				if (flag) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	@Override
	public Map<String, Object> login(long username, String password) {
		Map<String, Object> infoMap = new HashMap<>();
		User user = userDAO.findByUsername(username);
		if (user == null) {
			infoMap.put("status", -1);
		} else if (!password.equals(user.getPassword())) {
			infoMap.put("status", -1);
		} else {
			infoMap.put("status", 1);
			infoMap.put("user", user);
			infoMap.put("userId", user.getId());
		}
		return infoMap;
	}

	@Override
	public List<BusinessService> getAllFBByUserId(int page, int userId) {
		return userDAO.getAllFBByUserId(page, userId);
	}

	@Override
	public List<User> getAllConcernPeople(int userId) {
		return userDAO.getAllConcernPeople(userId);
	}

	@Override
	public List<UserRequest> getReceivedRequests(int userId) {
		return userDAO.getReceivedRequests(userId);
	}

	@Override
	public User findByUsername(long username) {
		try {
			return userDAO.findByUsername(username);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean isFavorite(int userId, int serviceId) {
		return userDAO.isFavorite(userId, serviceId);
	}

	@Override
	public User getById(int userId) {
		return userDAO.getById(userId);
	}

	@Override
	public int regist(User user) {
		File userDir = new File(root + dir + "/" + "big/");
		if (!userDir.exists()) {
			userDir.mkdirs();
		}
		userDir = new File(root + dir + "/" + "small/");
		if (!userDir.exists()) {
			userDir.mkdirs();
		}

		User existUser = userDAO.findByUsername(user.getUsername());
		if (existUser != null) {
			return -1; // 已注册
		}

		// 判断是否是正确的手机号
		String susername = user.getUsername() + "";
		if (susername.length() != 11) {
			return -1;// 手机号格式0错误
		}
		try {
			File headInonDir = new File(root + headIconDir);
			if (!headInonDir.exists()) {
				headInonDir.mkdirs();
			}
			headInonDir = new File(root + SmallheadIconDir);
			if (!headInonDir.exists()) {
				headInonDir.mkdirs();
			}
			user.setVerifyStatus(0);
			user.setRegistTime(System.currentTimeMillis() + "");
			user.setRank(0);
			user.setVerifyStatus(User.NO_VERIFY);
			user.setRank_credit(0);
			user.setAlipay_verify(User.NO_ALIPAY_STATUS);

			System.out.println("IconFile:" + user.getIconfile());
			if (user.getIconfile() != null) {
				String picName = user.getUsername() + System.currentTimeMillis() + ".png";
				// 得到图片保存的位置(根据root来得到图片保存的路径在tomcat下的该工程里)
				File file = new File(root + headIconDir + picName);
				System.out.println("headIcon saved path:" + root + headIconDir + picName);

				InputStream is = new FileInputStream(user.getIconfile());

				// 把图片写入到上面设置的路径里
				OutputStream os = new FileOutputStream(file);
				byte[] buffer = new byte[400];
				int length = 0;
				while ((length = is.read(buffer)) > 0) {
					os.write(buffer, 0, length);
				}
				is.close();
				os.close();
				if (file.exists() && file != null) {
					ImageUtil.zipTo400(root + headIconDir + picName, root + SmallheadIconDir + picName);
				}
				user.setHeadIcon("/qianxun" + headIconDir + picName);
			} else {
				user.setHeadIcon(BasicInfoConfig.default_headIcon);
			}

			// Caused by: java.sql.SQLException: Incorrect string value:
			// '\xF0\x9F\x98\x8A' for column 'nickName' at row 1
			userDAO.save(user);
			// 初始化userAccount
			UserAccount userAccount = new UserAccount();
			userAccount.setBalance(0.0);
			userAccount.setUserId(user.getId());
			userAccount.setUsername(user.getUsername() + "");
			userAccountDAO.save(userAccount);

			// 注册即时通讯：阿里百川
			BaiChuanUtils.addUser(user.getId(), user.getNickName(), BasicInfoConfig.HOST + user.getHeadIcon());

			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return -2;
		}
	}

	@Override
	public Map<String, Object> regist2(User user) {
		Map<String, Object> infoMap = new HashMap<>();
		User existedUser = userDAO.findByUsername(user.getUsername());
		if (existedUser != null) {
			infoMap.put("status", -3);
		}
		try {
			userDAO.save(user);
			File userDir = new File("root" + dir + user.getId() + "/" + "bigPath/");
			userDir.mkdirs();
			userDir = new File("root" + dir + user.getId() + "/" + "smallPath/");
			userDir.mkdirs();
			infoMap.put("status", 1);
		} catch (Exception e) {
			infoMap.put("status", -1);
			e.printStackTrace();
		}
		return infoMap;
	}

	@Override
	public Map<String, Object> getRegistCheckCode(long username) {
		Map<String, Object> infoMap = new HashMap<>();
		User user = userDAO.findByUsername(username);
		if (user != null) {
			infoMap.put("status", -2);
		} else {
			String checkCode = CheckUtil.sendCheckCode(username);
			infoMap.put("status", 1);
			infoMap.put("checkCode", checkCode);
		}
		return infoMap;
	}

	@Override
	public Map<String, Object> getLoginCheckCode(long username) {
		Map<String, Object> infoMap = new HashMap<>();
		User user = userDAO.findByUsername(username);
		if (user == null) {
			infoMap.put("status", -2);
		} else {
			String checkCode = CheckUtil.sendCheckCode(username);
			infoMap.put("status", 1);
			infoMap.put("checkCode", checkCode);
		}
		return infoMap;
	}

	@Override
	public Map<String, Object> loginByCheckCode(long username) {
		Map<String, Object> infoMap = new HashMap<>();
		User user = userDAO.findByUsername(username);
		if (user == null) {
			infoMap.put("status", -3);
		} else {
			infoMap.put("status", 1);
			infoMap.put("user", user);
		}
		return infoMap;
	}

	// @Override
	// public boolean addRank_Credit(User u) {
	// int rank = RankUtil.convertCreditToRank(u.getRank_credit());
	// u.setRank(rank < u.getRank() ? u.getRank() : rank);
	// return userDAO.addRank_Credit(u.getId(), u.getRank_credit(),
	// u.getRank());
	// }

	@Override
	public boolean addRank_Credit(int userId, int n) {
		return userDAO.addRank_Credit(userId, n);
	}

	@Override
	public Map<String, Object> check(long username) {
		Map<String, Object> infoMap = new HashMap<>();
		User user = userDAO.findByUsername(username);
		if (user == null) {
			infoMap.put("status", -2);// 未注册
		} else {
			infoMap.put("status", 1);// 有
		}
		return infoMap;
	}

	@Override
	public boolean hasPhoneNumber(long username) {
		User user = userDAO.findByUsername(username);
		if (user == null) {
			return false;// 未注册
		} else {
			return true;// 有
		}
	}

	@Override
	public Map<String, Object> changePwd(long username, String password) {
		Map<String, Object> infoMap = new HashMap<>();
		User user = userDAO.findByUsername(username);
		if (user == null) {
			infoMap.put("status", -3);
		} else {
			user.setPassword(password);
			userDAO.update(user);
			infoMap.put("status", 1);
		}
		return infoMap;
	}

	@Override
	public Map<String, Object> getMyRequest(User user) {
		Map<String, Object> infoMap = new HashMap<>();
		Set<UserRequest> userRequests = userDAO.findById(user.getId()).getUserRequest();
		infoMap.put("userRquests", userRequests);
		return infoMap;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public UserAccountDAO getUserAccountDAO() {
		return userAccountDAO;
	}

	public void setUserAccountDAO(UserAccountDAO userAccountDAO) {
		this.userAccountDAO = userAccountDAO;
	}

}
