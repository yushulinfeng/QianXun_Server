package domain;

import java.io.File;
import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.context.annotation.Lazy;

@Entity
@Table(name = "qx_userrequest")
public class UserRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4416377128051957616L;
	/**
	 * 需求status: 需求报名中
	 */
	public static final int SIGNUP_STATUS = 0;
	/**
	 * 需求status: 需求进行中
	 */
	public static final int ING_STATUS = 1;
	/**
	 * 需求status: 需求已完成
	 */
	public static final int FINISHED_STATUS = 2;
	/**
	 * 需求status: 需求检查中
	 */
	public static final int CHECKED_STATUS = 3;
	/**
	 * 需求status: 需求已删除
	 */
	public static final int DELETED_STATUS = 4;

	private int id;
	private String startLocation_remark;// 2号楼
	private String startAddress; // 山东大学软件园校区
	private double startLocation_x;// 开始位置经度
	private double startLocation_y;// 开始位置纬度
	private double endLocation_x;
	private double endLocation_y;
	private String endAddress;// 开始位置地址
	private String endLocation_remark;
	private String friendsNotify;// at好友
	private String request_content; // 内容
	private String request_limitTime;// 限制时间
	private String request_picture;
	private String request_picture2;
	private String request_picture3;
	private String reward_money;
	private String reward_thing;
	private String request_postTime;
	private String request_endTime;// 完成时间
	private String request_key;// 标签
	private String distance;// 距离
	private int status;// 需求的状态 0表示报名中，1表示进行中，2表示已完成，3表示检查中,4表示已删除
	private int report;// 举报数
	private int type;// 类型

	private File img1;
	private File img2;
	private File img3;

	// 发送需求的用户
	private User user;
	// 最终接单的用户
	private User finalReceiver;
	// 已报名的用户
	private Set<User> receivers;

	private long username;
	private String headIcon;
	private String nickName;
	private String gender;

	private int rank_credit;

	private UserComment userComment;

	public UserRequest() {
	}

	public UserRequest(int id, long username, String headIcon, String nickName, String gender, int rank_credit,
			String request_content, String request_key, String reward_money, String reward_thing,
			String request_picture, String request_picture2, String request_picture3, String distance,
			String request_postTime) {
		this.id = id;
		this.username = username;
		this.headIcon = headIcon;
		this.nickName = nickName;
		this.gender = gender;
		this.rank_credit = rank_credit;
		this.request_content = request_content;
		this.request_key = request_key;
		this.reward_money = reward_money;
		this.reward_thing = reward_thing;
		this.request_picture = request_picture;
		this.request_picture2 = request_picture2;
		this.request_picture3 = request_picture3;
		this.distance = distance;
		this.request_postTime = request_postTime;
	}

	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}

	@ManyToOne(targetEntity = User.class)
	@JoinColumn(name = "userId", referencedColumnName = "id")
	@Lazy(false)
	@Fetch(FetchMode.JOIN)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getEndAddress() {
		return endAddress;
	}

	public String getEndLocation_remark() {
		return endLocation_remark;
	}

	public String getFriendsNotify() {
		return friendsNotify;
	}

	public String getRequest_content() {
		return request_content;
	}

	public String getRequest_limitTime() {
		return request_limitTime;
	}

	public String getRequest_picture() {
		return request_picture;
	}

	public String getRequest_picture2() {
		return request_picture2;
	}

	public String getRequest_picture3() {
		return request_picture3;
	}

	public String getRequest_postTime() {
		return request_postTime;
	}

	public String getReward_money() {
		return reward_money;
	}

	public String getReward_thing() {
		return reward_thing;
	}

	public String getStartAddress() {
		return startAddress;
	}

	public String getStartLocation_remark() {
		return startLocation_remark;
	}

	public void setEndAddress(String endAddress) {
		this.endAddress = endAddress;
	}

	public void setEndLocation_remark(String endLocation_remark) {
		this.endLocation_remark = endLocation_remark;
	}

	public void setFriendsNotify(String friendsNotify) {
		this.friendsNotify = friendsNotify;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setRequest_content(String request_content) {
		this.request_content = request_content;
	}

	public void setRequest_limitTime(String request_limitTime) {
		this.request_limitTime = request_limitTime;
	}

	public void setRequest_picture(String request_picture) {
		this.request_picture = request_picture;
	}

	public void setRequest_picture2(String request_picture2) {
		this.request_picture2 = request_picture2;
	}

	public void setRequest_picture3(String request_picture3) {
		this.request_picture3 = request_picture3;
	}

	public void setRequest_postTime(String request_postTime) {
		this.request_postTime = request_postTime;
	}

	public void setReward_money(String reward_money) {
		this.reward_money = reward_money;
	}

	public void setReward_thing(String reward_thing) {
		this.reward_thing = reward_thing;
	}

	public void setStartAddress(String startAddress) {
		this.startAddress = startAddress;
	}

	public void setStartLocation_remark(String startLocation_remark) {
		this.startLocation_remark = startLocation_remark;
	}

	public String getRequest_endTime() {
		return request_endTime;
	}

	public void setRequest_endTime(String request_endTime) {
		this.request_endTime = request_endTime;
	}

	public String getRequest_key() {
		return request_key;
	}

	public void setRequest_key(String request_key) {
		this.request_key = request_key;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public double getStartLocation_x() {
		return startLocation_x;
	}

	public void setStartLocation_x(double startLocation_x) {
		this.startLocation_x = startLocation_x;
	}

	public double getStartLocation_y() {
		return startLocation_y;
	}

	public void setStartLocation_y(double startLocation_y) {
		this.startLocation_y = startLocation_y;
	}

	public double getEndLocation_x() {
		return endLocation_x;
	}

	public void setEndLocation_x(double endLocation_x) {
		this.endLocation_x = endLocation_x;
	}

	public double getEndLocation_y() {
		return endLocation_y;
	}

	public void setEndLocation_y(double endLocation_y) {
		this.endLocation_y = endLocation_y;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Transient
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	@Transient
	public String getHeadIcon() {
		return headIcon;
	}

	public void setHeadIcon(String headIcon) {
		this.headIcon = headIcon;
	}

	@Transient
	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	@Transient
	public int getRank_credit() {
		return rank_credit;
	}

	public void setRank_credit(int rank_credit) {
		this.rank_credit = rank_credit;
	}

	@OneToOne(targetEntity = UserComment.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "userComment_id")
	@Lazy(false)
	public UserComment getUserComment() {
		return userComment;
	}

	public void setUserComment(UserComment userComment) {
		this.userComment = userComment;
	}

	@ManyToOne(targetEntity = User.class)
	@JoinColumn(name = "finalReceiver")
	@Lazy(false)
	@Fetch(FetchMode.JOIN)
	public User getFinalReceiver() {
		return finalReceiver;
	}

	public void setFinalReceiver(User finalReceiver) {
		this.finalReceiver = finalReceiver;
	}

	@ManyToMany(targetEntity = User.class, mappedBy = "receivedRequest", fetch = FetchType.EAGER)
	@Lazy(true)
	public Set<User> getReceivers() {
		return receivers;
	}

	public void setReceivers(Set<User> receivers) {
		this.receivers = receivers;
	}

	@Transient
	public long getUsername() {
		return username;
	}

	public void setUsername(long username) {
		this.username = username;
	}

	public int getReport() {
		return report;
	}

	public void setReport(int report) {
		this.report = report;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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
