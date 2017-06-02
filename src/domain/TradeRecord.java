package domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "qx_traderecord")
public class TradeRecord {// 交易记录

	/**
	 * 未完成
	 */
	public static final int INIT_STATUS = 0;
	/**
	 * 已完成
	 */
	public static final int FINISHED_STATUS = 1;
	/**
	 * 失败
	 */
	public static final int FAILED_STATUS = 2;

	private int id;
	// private String batchNumber;// 批次号,unique
	private String account_name;// 收款人姓名
	private String account_date;// 收款日期
	private String account_email;// 收款人账号
	private String account_fee;// 收款金额
	private String account_note;// 备注
	private int finalStatus;// 最终处理结果
	private int userId;// 用户的id
	private int checkCode;// 验证码

	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAccount_name() {
		return account_name;
	}

	public void setAccount_name(String account_name) {
		this.account_name = account_name;
	}

	public String getAccount_date() {
		return account_date;
	}

	public void setAccount_date(String account_date) {
		this.account_date = account_date;
	}

	public String getAccount_email() {
		return account_email;
	}

	public void setAccount_email(String account_email) {
		this.account_email = account_email;
	}

	public String getAccount_fee() {
		return account_fee;
	}

	public void setAccount_fee(String account_fee) {
		this.account_fee = account_fee;
	}

	public String getAccount_note() {
		return account_note;
	}

	public void setAccount_note(String account_note) {
		this.account_note = account_note;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	@Transient
	public int getCheckCode() {
		return checkCode;
	}

	public void setCheckCode(int checkCode) {
		this.checkCode = checkCode;
	}

	/**
	 * 有空返回第一个空的名,否则返回"no_Null"
	 * 
	 * @return
	 */
	public String hasNull() {
		if (account_name == null || account_name.isEmpty()) {
			return "account_name";
		}
		if (account_date == null || account_date.isEmpty()) {
			return "account_date";
		}
		if (account_email == null || account_email.isEmpty()) {
			return "account_email";
		}
		if (account_fee == null || account_fee.isEmpty()) {
			return "account_fee";
		}
		if (account_note == null || account_note.isEmpty()) {
			return "account_note";
		}
		if (userId == 0) {
			return "userId";
		}
		return "no_Null";
	}

	public int getFinalStatus() {
		return finalStatus;
	}

	public void setFinalStatus(int finalStatus) {
		this.finalStatus = finalStatus;
	}
}
