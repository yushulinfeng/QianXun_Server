package service;

import beans.UserEntity;
import dao.UserDaoImpl;

/**
 * Created by YuShuLinFeng on 2017/4/28.
 */
public class UserServiceImpl implements UserService {
    private UserDaoImpl dao;

    public void setDao(UserDaoImpl dao) {
        this.dao = dao;
    }

    @Override
    public boolean register(UserEntity user) {
        try {
            return dao.register(user);
        } catch (Exception e) {
            System.out.println("register-error");
            return false;
        }
    }

    @Override
    public String login(UserEntity user) {
        return dao.login(user);
    }

}
