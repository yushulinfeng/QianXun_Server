package service.impl;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;

import service.UserCommentService;
import util.DecodeUtil;
import util.ImageUtil;
import config.BaseConfiger;
import config.BasicInfoConfig;
import dao.UserCommentDAO;
import domain.UserComment;

public class UserCommentServiceImpl implements UserCommentService {

	private UserCommentDAO userCommentDAO;

	private static String root = BaseConfiger.FileSavePath;
	private static String CommentImagePath = BasicInfoConfig.CommentImageDir;
	private static String SmallCommentImagePath = BasicInfoConfig.SmallCommentImageDir;

	@Override
	public boolean save(UserComment userComment) {

		File dir = new File(root + CommentImagePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		dir = new File(root + SmallCommentImagePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		try {

			userComment.setComment_great(0);

			String picName = userComment.getId() + System.currentTimeMillis() + "1.jpg";
			String pic2Name = userComment.getId() + System.currentTimeMillis() + "2.jpg";
			String pic3Name = userComment.getId() + System.currentTimeMillis() + "3.jpg";

			if (userComment.getComment_picture() != null && !"".equals(userComment.getComment_picture())
					&& !"null".equals(userComment.getComment_picture())) {
				FileUtils.writeByteArrayToFile(new File(root + CommentImagePath + picName),
						DecodeUtil.decodeImageString(userComment.getComment_picture()), false);
				userComment.setComment_picture("/qianxun" + CommentImagePath + picName);
				ImageUtil.zipTo400(root + CommentImagePath + picName, root + SmallCommentImagePath + picName);
			}
			if (userComment.getComment_picture2() != null && !"".equals(userComment.getComment_picture2())
					&& !"null".equals(userComment.getComment_picture2())) {
				FileUtils.writeByteArrayToFile(new File(root + CommentImagePath + pic2Name),
						DecodeUtil.decodeImageString(userComment.getComment_picture2()), false);
				userComment.setComment_picture2("/qianxun" + CommentImagePath + pic2Name);
				ImageUtil.zipTo400(root + CommentImagePath + pic2Name, root + SmallCommentImagePath + pic2Name);
			}
			if (userComment.getComment_picture3() != null && !"".equals(userComment.getComment_picture3())
					&& !"null".equals(userComment.getComment_picture3())) {
				FileUtils.writeByteArrayToFile(new File(root + CommentImagePath + pic3Name),
						DecodeUtil.decodeImageString(userComment.getComment_picture3()), false);
				userComment.setComment_picture3("/qianxun" + CommentImagePath + pic3Name);
				ImageUtil.zipTo400(root + CommentImagePath + pic3Name, root + SmallCommentImagePath + pic3Name);
			}

			userCommentDAO.save(userComment);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void update(UserComment userComment) {
		userCommentDAO.update(userComment);
		System.out.println("updating comment..");
	}

	@Override
	public void delete(UserComment userComment) {
		userCommentDAO.delete(userComment);
		System.out.println("deleting comment..");
	}

	@Override
	public UserComment getDetailByRequestId(int id) {
		UserComment u = null;
		try {
			u = userCommentDAO.getDetailByRequestId(id);
			System.out.println("userCommentId:" + u.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return u;
	}

	@Override
	public boolean addCommentGreat(int commentId) {
		try {
			userCommentDAO.addCommentGreat(commentId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public UserComment getUserCommentById(int id) {
		UserComment u = null;
		try {
			u = userCommentDAO.getUserCommentById(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return u;
	}

	@Override
	public List<UserComment> getBriefUserCommentByPage(int page) {
		List<UserComment> list = null;
		try {
			list = userCommentDAO.getBriefByPage(page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**********************************************/
	public void setUserCommentDAO(UserCommentDAO userCommentDAO) {
		this.userCommentDAO = userCommentDAO;
	}

}
