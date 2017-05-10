package domain;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.struts2.json.annotations.JSON;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.context.annotation.Lazy;

@Entity
@Table(name = "qx_user")
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7661805270041042097L;
	/**
	 * 用户认证： 未认证
	 */
	public static final int NO_VERIFY = 0;
	/**
	 * 用户认证： 用户身份证已认证
	 */
	public static final int USERID_COMMIT_VERIFY = 1;
	/**
	 * 用户认证： 用户学生证已认证
	 */
	public static final int STU_COMMIT_VERIFY = 2;
	/**
	 * 用户认证：身份证认证中
	 */
	public static final int USERID_ING_VERIFY = -1;
	/**
	 * 用户认证： 学生证认证中
	 */
	public static final int STU_ING_VERIFY = -2;

	/**
	 * 支付宝未认证
	 */
	public static final int NO_ALIPAY_STATUS = 0;
	/**
	 * 支付宝认证中
	 */
	public static final int CONFIRMING_ALIPAY_STATUS = -1;
	/**
	 * 支付宝已认证
	 */
	public static final int CONFIRMED_ALIPAY_STATUS = 1;

	private int id;
	private long username;
	private String password;
	private int age;
	private String registCheckCode;

	private String birthday;

	private String headIcon;
	private File iconfile;
	private String homePageBackgroundImage;
	private String address;
	private String nickName;
	private String gender;
	private int rank_credit;// 积分
	private int rank;// 等级
	private String registTime;
	private int verifyStatus;// 0未认证，1通过身份证认证，2通过学生认证，-1身份证认证审核中，-2学生认证审核中
	private int alipay_verify;// 0未认证，1通过支付宝认证，-1支付宝认证中

	private String sign;// 个性签名
	private String hobby;
	private String job;
	private String school;// 学生的话先写好是哪个学校的
	private String latestLocation_x;
	private String latestLocation_y;
	// private int tipOff;// 举报

	// 已经接单
	private Set<UserRequest> todoRequest = new HashSet<>();
	// 已报名
	private Set<UserRequest> receivedRequest = new HashSet<>();

	private Set<Photo> photos = new HashSet<>();

	// 个人发单
	private Set<UserRequest> userRequest = new HashSet<>();
	private Set<UserComment> userComment = new HashSet<>();

	// 关心的人
	private Set<User> concernedPeople = new HashSet<>();

	// 收藏的服务
	private Set<BusinessService> favoriteServices = new HashSet<>();
	// 发的服务
	private Set<BusinessService> services = new HashSet<>();

	public User() {
	}

	public User(int username, String password) {
		this.username = username;
		this.password = password;
	}

	public User(int id, String headIcon, String nickName) {
		this.id = id;
		this.headIcon = headIcon;
		this.nickName = nickName;
	}

	@JSON(serialize = false)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getRank_credit() {
		return rank_credit;
	}

	public void setRank_credit(int rank_credit) {
		this.rank_credit = rank_credit;
	}

	@OneToMany(targetEntity = UserRequest.class, mappedBy = "user")
	@Transient
	@Lazy(true)
	@Fetch(FetchMode.SELECT)
	public Set<UserRequest> getUserRequest() {
		return userRequest;
	}

	public void setUserRequest(Set<UserRequest> userRequest) {
		this.userRequest = userRequest;
	}

	@OneToOne(targetEntity = UserComment.class, mappedBy = "user")
	@Transient
	@Lazy(true)
	@Fetch(FetchMode.SELECT)
	public Set<UserComment> getUserComment() {
		return userComment;
	}

	public void setUserComment(Set<UserComment> userComment) {
		this.userComment = userComment;
	}

	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getHeadIcon() {
		return headIcon;
	}

	public void setHeadIcon(String headIcon) {
		this.headIcon = headIcon;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	@Transient
	public String getRegistCheckCode() {
		return registCheckCode;
	}

	public void setRegistCheckCode(String registCheckCode) {
		this.registCheckCode = registCheckCode;
	}

	@OneToMany(targetEntity = Photo.class, mappedBy = "user")
	@Transient
	@Lazy(true)
	@Fetch(FetchMode.SELECT)
	public Set<Photo> getPhotos() {
		return photos;
	}

	public void setPhotos(Set<Photo> photos) {
		this.photos = photos;
	}

	@OneToMany(mappedBy = "finalReceiver")
	@Transient
	@Lazy(true)
	@Fetch(FetchMode.SELECT)
	public Set<UserRequest> getTodoRequest() {
		return todoRequest;
	}

	public void setTodoRequest(Set<UserRequest> todoRequest) {
		this.todoRequest = todoRequest;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getBirthday() {
		return birthday;
	}

	@ManyToMany(targetEntity = UserRequest.class, fetch = FetchType.EAGER)
	// @JoinTable(name = "user_receivedrequests", joinColumns = {
	// @JoinColumn(name = "username", referencedColumnName = "username",
	// columnDefinition = "BIGINT NOT NULL") }, inverseJoinColumns = {
	// @JoinColumn(name = "request_id", referencedColumnName = "id") })
	@JoinTable(name = "user_request", joinColumns = { @JoinColumn(name = "userId", referencedColumnName = "id", columnDefinition = "bigint") }, inverseJoinColumns = { @JoinColumn(name = "request_id", referencedColumnName = "id") })
	@Lazy(true)
	@Fetch(FetchMode.SELECT)
	public Set<UserRequest> getReceivedRequest() {
		return receivedRequest;
	}

	public void setReceivedRequest(Set<UserRequest> receivedRequest) {
		this.receivedRequest = receivedRequest;
	}

	@ManyToMany(targetEntity = User.class, fetch = FetchType.EAGER)
	@JoinTable(name = "user_concerned", joinColumns = { @JoinColumn(name = "id", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "concerned_id", referencedColumnName = "id") })
	@Lazy(false)
	public Set<User> getConcernedPeople() {
		return concernedPeople;
	}

	public void setConcernedPeople(Set<User> concernedPeople) {
		this.concernedPeople = concernedPeople;
	}

	@ManyToMany(targetEntity = BusinessService.class)
	@JoinTable(name = "user_favoriteservices", joinColumns = { @JoinColumn(name = "id", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "serviceId", referencedColumnName = "id") })
	@Fetch(FetchMode.SELECT)
	@Lazy(true)
	public Set<BusinessService> getFavoriteServices() {
		return favoriteServices;
	}

	public void setFavoriteServices(Set<BusinessService> favoriteServices) {
		this.favoriteServices = favoriteServices;
	}

	@OneToMany(targetEntity = BusinessService.class, mappedBy = "user")
	@Fetch(FetchMode.SELECT)
	@Lazy(true)
	public Set<BusinessService> getServices() {
		return services;
	}

	public void setServices(Set<BusinessService> services) {
		this.services = services;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getVerifyStatus() {
		return verifyStatus;
	}

	public void setVerifyStatus(int verifyStatus) {
		this.verifyStatus = verifyStatus;
	}

	public String getRegistTime() {
		return registTime;
	}

	public void setRegistTime(String registTime) {
		this.registTime = registTime;
	}

	@Column(unique = true, nullable = false)
	public long getUsername() {
		return username;
	}

	public void setUsername(long username) {
		this.username = username;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getHobby() {
		return hobby;
	}

	public void setHobby(String hobby) {
		this.hobby = hobby;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getLatestLocation_x() {
		return latestLocation_x;
	}

	public void setLatestLocation_x(String latestLocation_x) {
		this.latestLocation_x = latestLocation_x;
	}

	public String getLatestLocation_y() {
		return latestLocation_y;
	}

	public void setLatestLocation_y(String latestLocation_y) {
		this.latestLocation_y = latestLocation_y;
	}

	public String getHomePageBackgroundImage() {
		return homePageBackgroundImage;
	}

	public void setHomePageBackgroundImage(String homePageBackgroundImage) {
		this.homePageBackgroundImage = homePageBackgroundImage;
	}

	public int getAlipay_verify() {
		return alipay_verify;
	}

	public void setAlipay_verify(int alipay_verify) {
		this.alipay_verify = alipay_verify;
	}

	@Transient
	public File getIconfile() {
		return iconfile;
	}

	public void setIconfile(File iconfile) {
		this.iconfile = iconfile;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

}
