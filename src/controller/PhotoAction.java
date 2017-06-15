package controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import service.PhotoService;

import com.opensymphony.xwork2.ActionSupport;

import dao.PhotoDAO;
import domain.Photo;
import domain.User;

public class PhotoAction extends ActionSupport implements SessionAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6617921861546322244L;
	private InputStream inputStream;
	private File photoFile;
	private Map<String, Object> session;
	private PhotoService photoService;
	private int photoId;
	private PhotoDAO photoDAO;

	public String add() {
		User user = (User) session.get("user");
		String result;
		if (user != null) {
			photoService.add(user, photoFile);
			result = "1";
		} else {
			result = "-1";
		}
		setInputStream(new ByteArrayInputStream(result.getBytes()));
		return SUCCESS;
	}

	public String delete() {
		Photo photo = photoDAO.findById(photoId);
		String result;
		if (photo != null) {
			try {
				photoDAO.delete(photo);
				result = "1";
			} catch (Exception e) {
				// TODO
				e.printStackTrace();
				result = "-2";
			}
		} else {
			result = "-1";
		}
		inputStream = new ByteArrayInputStream(result.getBytes());
		return SUCCESS;
	}

	public File getPhotoFile() {
		return photoFile;
	}

	public void setPhotoFile(File photoFile) {
		this.photoFile = photoFile;
	}

	public PhotoService getPhotoService() {
		return photoService;
	}

	public void setPhotoService(PhotoService photoService) {
		this.photoService = photoService;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public PhotoDAO getPhotoDAO() {
		return photoDAO;
	}

	public void setPhotoDAO(PhotoDAO photoDAO) {
		this.photoDAO = photoDAO;
	}

	public int getPhotoId() {
		return photoId;
	}

	public void setPhotoId(int photoId) {
		this.photoId = photoId;
	}
}
