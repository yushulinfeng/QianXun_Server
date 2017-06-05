package domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.context.annotation.Lazy;

/**
 * 商家服务类
 * 
 * @author 沧
 * 
 */
@Entity
@Table(name = "qx_businessservice")
public class BusinessService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6178299161740416781L;
	/**
	 * 服务的状态：初始化
	 */
	public static final int NO_STATUS = 0;
	/**
	 * 服务的状态：停止的状态
	 */
	public static final int STOP_STATUS = 1;
	/**
	 * 服务的状态：已删除的状态
	 */
	public static final int HASDELETED_STATUS = 2;

	private int id;
	private int category;// 分类,总分类
	private String name;
	private String detail;
	private Set<ServiceImage> images;

	private String reward_money;
	private String reward_unit;
	private String reward_thing;

	private String exchange;// 交换条件
	private int serviceType;// 服务方式
	private String time;// 发布时间
	private String serviceTime;// 可服务时间
	private String serviceCity;// 可服务的城市
	private String canServiceDay;// 保存可服务日子
	private double location_x;
	private double location_y;

	private int favoriteNumber;// 喜欢，收藏人数
	private int finishedPeople;// 已完成的订单数
	private int great;// 点赞人
	private int status;// service的状态 1,有现货 3,是需定制 1（能用） 2已删除 3（中止）
	private User user;// 发服务的人

	private Set<User> favoritePeople;// 收藏的人
	private Set<User> greatPeople; // 点赞的人

	public BusinessService() {

	}

	private int userId;
	private String headIcon;
	private long uesrname;
	private String nickName;

	public BusinessService(int userId, String headIcon, long username,
			String nickName) {
		this.uesrname = username;
		this.headIcon = headIcon;
		this.userId = userId;
		this.setNickName(nickName);
	}

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

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public int getServiceType() {
		return serviceType;
	}

	public void setServiceType(int serviceType) {
		this.serviceType = serviceType;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getFavoriteNumber() {
		return favoriteNumber;
	}

	public void setFavoriteNumber(int favoriteNumber) {
		this.favoriteNumber = favoriteNumber;
	}

	public int getFinishedPeople() {
		return finishedPeople;
	}

	public void setFinishedPeople(int finishedPeople) {
		this.finishedPeople = finishedPeople;
	}

	public int getGreat() {
		return great;
	}

	public void setGreat(int great) {
		this.great = great;
	}

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "service", targetEntity = ServiceImage.class)
	@OrderBy(value = "id ASC")
	@Lazy(false)
	public Set<ServiceImage> getImages() {
		return images;
	}

	public void setImages(Set<ServiceImage> images) {
		this.images = images;
	}

	@ManyToMany(targetEntity = User.class, mappedBy = "favoriteServices")
	@Lazy(true)
	@Fetch(FetchMode.SELECT)
	public Set<User> getFavoritePeople() {
		return favoritePeople;
	}

	public void setFavoritePeople(Set<User> favoritePeople) {
		this.favoritePeople = favoritePeople;
	}

	@OneToMany(targetEntity = User.class)
	@Lazy(true)
	@Fetch(FetchMode.SELECT)
	public Set<User> getGreatPeople() {
		return greatPeople;
	}

	public void setGreatPeople(Set<User> greatPeople) {
		this.greatPeople = greatPeople;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getReward_money() {
		return reward_money;
	}

	public void setReward_money(String reward_money) {
		this.reward_money = reward_money;
	}

	public String getReward_unit() {
		return reward_unit;
	}

	public void setReward_unit(String reward_unit) {
		this.reward_unit = reward_unit;
	}

	public String getReward_thing() {
		return reward_thing;
	}

	public void setReward_thing(String reward_thing) {
		this.reward_thing = reward_thing;
	}

	public String getServiceTime() {
		return serviceTime;
	}

	public void setServiceTime(String serviceTime) {
		this.serviceTime = serviceTime;
	}

	public String getServiceCity() {
		return serviceCity;
	}

	public void setServiceCity(String serviceCity) {
		this.serviceCity = serviceCity;
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

	public String getCanServiceDay() {
		return canServiceDay;
	}

	public void setCanServiceDay(String canServiceDay) {
		this.canServiceDay = canServiceDay;
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
	public long getUesrname() {
		return uesrname;
	}

	public void setUesrname(long uesrname) {
		this.uesrname = uesrname;
	}

	@Transient
	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	/////////////////////////
	
	private String discussId;// //后期添加

	// @Transient
	public String getDiscussId() {
		return discussId;
	}

	public void setDiscussId(String discussId) {
		this.discussId = discussId;
	}

}
