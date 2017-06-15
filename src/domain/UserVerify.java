package domain;

import java.io.File;
import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "qx_userinfoverify")
public class UserVerify implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -97123878434672486L;
	private int id;
	private String IDImage;// 身份证图片
	private File imageFile;
	private String IDNumber;// 身份证号
	private long phone;
	private String realName;

	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}

	public String getIDImage() {
		return IDImage;
	}

	public String getIDNumber() {
		return IDNumber;
	}

	public long getPhone() {
		return phone;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIDImage(String iDImage) {
		IDImage = iDImage;
	}

	public void setIDNumber(String iDNumber) {
		IDNumber = iDNumber;
	}

	public void setPhone(long phone) {
		this.phone = phone;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	@Transient
	public File getImageFile() {
		return imageFile;
	}

	public void setImageFile(File imageFile) {
		this.imageFile = imageFile;
	}

}
