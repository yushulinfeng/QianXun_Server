package beans;

import javax.persistence.*;

/**
 * Created by YuShuLinFeng on 2017/4/28.
 */
@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(name = "bl_user", schema = "outapp")
public class UserEntity {
    private String userid;
    private String username;
    private String userpass;
    private String usernick;

    @Id
    @Column(name = "userid", nullable = false, length = 255)
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    @Basic
    @Column(name = "username", nullable = true, length = 255)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Basic
    @Column(name = "userpass", nullable = true, length = 255)
    public String getUserpass() {
        return userpass;
    }

    public void setUserpass(String userpass) {
        this.userpass = userpass;
    }

    @Basic
    @Column(name = "usernick", nullable = true, length = 255)
    public String getUsernick() {
        return usernick;
    }

    public void setUsernick(String usernick) {
        this.usernick = usernick;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserEntity that = (UserEntity) o;

        if (userid != null ? !userid.equals(that.userid) : that.userid != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (userpass != null ? !userpass.equals(that.userpass) : that.userpass != null) return false;
        if (usernick != null ? !usernick.equals(that.usernick) : that.usernick != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userid != null ? userid.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (userpass != null ? userpass.hashCode() : 0);
        result = 31 * result + (usernick != null ? usernick.hashCode() : 0);
        return result;
    }
}
