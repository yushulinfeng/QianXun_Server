package domain;

import java.io.File;
import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "qx_stuinfoverify")
public class StudentVerify implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6977294911928285800L;
	private String admissionTime;// 入学时间
	private String college;// 学院
	private String degree;// 学历
	private int id;
	private String major;// 专业
	private long phone;
	private String realName;
	private String school;// 学校
	private String stuID;
	private String stuIDImage;// 学生证
	private File imageFile;

	public String getAdmissionTime() {
		return admissionTime;
	}

	public String getCollege() {
		return college;
	}

	public String getDegree() {
		return degree;
	}

	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}

	public String getMajor() {
		return major;
	}

	// @OneToOne(optional = true, targetEntity = UserVerify.class, mappedBy =
	// "phone")
	public long getPhone() {
		return phone;
	}

	public String getRealName() {
		return realName;
	}

	public String getSchool() {
		return school;
	}

	public String getStuID() {
		return stuID;
	}

	public String getStuIDImage() {
		return stuIDImage;
	}

	public void setAdmissionTime(String admissionTime) {
		this.admissionTime = admissionTime;
	}

	public void setCollege(String college) {
		this.college = college;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public void setPhone(long phone) {
		this.phone = phone;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public void setStuID(String stuID) {
		this.stuID = stuID;
	}

	public void setStuIDImage(String stuIDImage) {
		this.stuIDImage = stuIDImage;
	}

	@Transient
	public File getImageFile() {
		return imageFile;
	}

	public void setImageFile(File imageFile) {
		this.imageFile = imageFile;
	}

}
