package domain;

import java.io.File;
import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.context.annotation.Lazy;

@Entity
@Table(name = "qx_dynamic")
public class Dynamic implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8114381942352446942L;
	/**
	 * 动态状态：初始化
	 */
	public static final int NONE_STATUS = 0;
	/**
	 * 动态状态：已删除
	 */
	public static final int HASDELETED_STATUS = 1;

	private int id;
	private User user;// 发服务的人
	private String content;// 内容
	private String pic1;
	private File img1;
	private String pic2;
	private File img2;
	private String pic3;
	private File img3;
	private double location_x;
	private double location_y;
	private BusinessService businessService;
	private int support;// 顶
	private int trample;// 踩
	private String publishTime;// 发布时间
	private int status;

	public Dynamic() {

	}

	public Dynamic(int id, String pic1, String pic2, String pic3, String content, String publishTime) {
		this.id = id;
		this.pic1 = pic1;
		this.pic2 = pic2;
		this.pic3 = pic3;
		this.content = content;
		this.publishTime = publishTime;
	}

	// 实现Dynamic的getBrief接口的构造器
	public Dynamic(int userId, String headIcon, long username, String nickName, int id, String pic1, String content) {
		this.userId = userId;
		this.headIcon = headIcon;
		this.username = username;
		this.nickName = nickName;
		this.id = id;
		this.pic1 = pic1;
		this.content = content;
	}

	private int userId;
	private String headIcon;
	private long username;
	private String nickName;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(targetEntity = User.class)
	@JoinColumn(name = "userId", referencedColumnName = "id")
	@Lazy(true)
	@Fetch(FetchMode.SELECT)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPic1() {
		return pic1;
	}

	public void setPic1(String pic1) {
		this.pic1 = pic1;
	}

	public String getPic2() {
		return pic2;
	}

	public void setPic2(String pic2) {
		this.pic2 = pic2;
	}

	public String getPic3() {
		return pic3;
	}

	public void setPic3(String pic3) {
		this.pic3 = pic3;
	}

	public double getLocation_x() {
		return location_x;
	}

	public void setLocation_x(double location_x) {
		this.location_x = location_x;
	}

	public double getLocation_y() {
		return location_y;
	}

	public void setLocation_y(double location_y) {
		this.location_y = location_y;
	}

	@ManyToOne(targetEntity = BusinessService.class)
	@JoinColumn(name = "serviceId", referencedColumnName = "id")
	public BusinessService getBusinessService() {
		return businessService;
	}

	public void setBusinessService(BusinessService businessService) {
		this.businessService = businessService;
	}

	public int getSupport() {
		return support;
	}

	public void setSupport(int support) {
		this.support = support;
	}

	public int getTrample() {
		return trample;
	}

	public void setTrample(int trample) {
		this.trample = trample;
	}

	public String getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Transient
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	@Transient
	public String getHeadIcon() {
		return headIcon;
	}

	public void setHeadIcon(String headIcon) {
		this.headIcon = headIcon;
	}

	@Transient
	public long getUsername() {
		return username;
	}

	public void setUsername(long username) {
		this.username = username;
	}

	@Transient
	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	@Transient
	public File getImg1() {
		return img1;
	}

	public void setImg1(File img1) {
		this.img1 = img1;
	}

	@Transient
	public File getImg2() {
		return img2;
	}

	public void setImg2(File img2) {
		this.img2 = img2;
	}

	@Transient
	public File getImg3() {
		return img3;
	}

	public void setImg3(File img3) {
		this.img3 = img3;
	}

}
