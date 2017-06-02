package domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.context.annotation.Lazy;

@Entity
@Table(name = "qx_usergetservice")
public class UserGetService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 564092334680891805L;

	/**
	 * 订单已提交，用户未付款
	 */
	public static final int WAITING_PAY_STATUS = 1;
	/**
	 * ，订单提交完，付款成功,等待商家确认预约
	 */
	public static final int PAY_FINISH_WAITING_STATUS = 6;
	/**
	 * 订单已提交，商家拒绝接单
	 */
	public static final int REFUSE_STATUS = -1;
	/**
	 * 订单进行中，即商家统一预约
	 */
	public static final int START_STATUS = 2;
	/**
	 * 订单退款申请中
	 */
	public static final int APPLY_REFUND_STATUS = -3;
	/**
	 * 同意退款，订单退款
	 */
	public static final int REFUND_STATUS = 3;
	/**
	 * 订单待评论
	 */
	public static final int WAITING_TO_COMMENT_STATUS = 4;
	/**
	 * 订单已完成
	 */
	public static final int HAS_FINISHED_STATUS = 5;

	private int id;
	private User user;// 接单用户
	private BusinessService businessService;// 用户预约的服务
	private int status;// 该条预约的服务的状态
	private String startTime;// 开始时间 //提问，在数据库中保存的时间可以保存int型的时间戳的形式
	private String statusTime;// 处在该状态时的时间
	private float price;// 价格
	private int number;// 服务定制的数量
	private String extimateTime;// 预计完成时间

	public UserGetService() {

	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@OneToOne(targetEntity = User.class)
	@JoinColumn(name = "userId", referencedColumnName = "id")
	@Lazy(false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getStatusTime() {
		return statusTime;
	}

	public void setStatusTime(String statusTime) {
		this.statusTime = statusTime;
	}

	@OneToOne(targetEntity = BusinessService.class)
	@JoinColumn(name = "serviceId", referencedColumnName = "id")
	@Lazy(false)
	public BusinessService getBusinessService() {
		return businessService;
	}

	public void setBusinessService(BusinessService businessService) {
		this.businessService = businessService;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getExtimateTime() {
		return extimateTime;
	}

	public void setExtimateTime(String extimateTime) {
		this.extimateTime = extimateTime;
	}

}
