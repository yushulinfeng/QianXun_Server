package domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.context.annotation.Lazy;

@Entity
@Table(name = "qx_usercomment")
public class UserComment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4216693209078250676L;
	private int id;
	private String comment_context;
	private int comment_great;
	private String comment_finishTime;
	private String comment_picture;
	private String comment_picture2;
	private String comment_picture3;
	private User user;

	private UserRequest userRequest;

	public UserComment() {
	}

	// 获取简要信息
	public UserComment(String comment_context, String comment_picture) {
		this.comment_context = comment_context;
		this.comment_picture = comment_picture;
	}

	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getComment_context() {
		return comment_context;
	}

	public void setComment_context(String comment_context) {
		this.comment_context = comment_context;
	}

	public String getComment_picture() {
		return comment_picture;
	}

	public void setComment_picture(String comment_picture) {
		this.comment_picture = comment_picture;
	}

	public String getComment_picture3() {
		return comment_picture3;
	}

	public void setComment_picture3(String comment_picture3) {
		this.comment_picture3 = comment_picture3;
	}

	public int getComment_great() {
		return comment_great;
	}

	public void setComment_great(int comment_great) {
		this.comment_great = comment_great;
	}

	public String getComment_finishTime() {
		return comment_finishTime;
	}

	public void setComment_finishTime(String comment_finishTime) {
		this.comment_finishTime = comment_finishTime;
	}

	public String getComment_picture2() {
		return comment_picture2;
	}

	public void setComment_picture2(String comment_picture2) {
		this.comment_picture2 = comment_picture2;
	}

	@OneToOne(targetEntity = User.class)
	// optional = false,
	@JoinColumn(name = "username", referencedColumnName = "username")
	@Lazy(true)
	@Fetch(FetchMode.SELECT)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@OneToOne(targetEntity = UserRequest.class, mappedBy = "userComment")
	@JoinColumn(name = "requestId", referencedColumnName = "id", unique = true)
	public UserRequest getUserRequest() {
		return userRequest;
	}

	public void setUserRequest(UserRequest userRequest) {
		this.userRequest = userRequest;
	}

}
