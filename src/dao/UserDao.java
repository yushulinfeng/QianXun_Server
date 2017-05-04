package dao;

import beans.UserEntity;

/**
 * Created by YuShuLinFeng on 2017/4/28.
 */
public interface UserDao {

    boolean register(UserEntity user);

    String login(UserEntity user);
}
