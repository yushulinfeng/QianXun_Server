package action;

import beans.UserEntity;
import com.opensymphony.xwork2.ActionSupport;
import service.UserService;
import utils.MsgUtil;

import java.io.InputStream;

/**
 * Created by YuShuLinFeng on 2017/4/28.
 */
public class UserAction extends ActionSupport {
    private UserEntity user;
    private InputStream inputStream;
    private UserService userService;

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public String login() {
        System.out.println("login");
        if (user == null || user.getUsername() == null || user.getUsername().equals("")) {
            inputStream = MsgUtil.sendString("0");
            return SUCCESS;
        }
        //1成功，0失败
        String state = userService.login(user);
        inputStream = MsgUtil.sendString(state == null ? "-1" : state);
        return SUCCESS;
    }

    public String register() {
        System.out.println("register");
        if (user == null || user.getUsername() == null || user.getUsername().equals("")) {
            inputStream = MsgUtil.sendString("0");
            return SUCCESS;
        }
        //1成功，0用户已注册
        boolean state = userService.register(user);
        inputStream = MsgUtil.sendString(state ? "1" : "0");
        return SUCCESS;
    }

    public String execute() {
        System.out.println("execute");
        inputStream = MsgUtil.sendString("UserAction");
        if (user == null || user.getUsername() == null || user.getUsername().equals(""))
            return ERROR;
        return SUCCESS;
    }
}
