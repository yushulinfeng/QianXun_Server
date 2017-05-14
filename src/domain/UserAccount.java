package domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "qx_useraccount")
public class UserAccount {

	private double balance;// 余额
	private int userId;// 用户id
	private String username;// 用户手机号
	private String lastGetCashTime;// 最后一次提现的时间

	// private String alipay_account;// 支付宝账户

	@Id
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	// @NumberFormat(pattern = "0.00", style = Style.NUMBER)
	@Column(scale = 2, precision = 10)
	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	@Column(unique = true)
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLastGetCashTime() {
		return lastGetCashTime;
	}

	public void setLastGetCashTime(String lastGetCashTime) {
		this.lastGetCashTime = lastGetCashTime;
	}

}
