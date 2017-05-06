package service;

import beans.UserEntity;

/**
 * Created by YuShuLinFeng on 2017/4/28.
 */
public interface UserService {

    boolean register(UserEntity user);

    String login(UserEntity user);
}
