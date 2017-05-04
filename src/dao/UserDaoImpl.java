package dao;

import beans.UserEntity;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

/**
 * Created by YuShuLinFeng on 2017/4/28.
 */
public class UserDaoImpl extends HibernateDaoSupport implements UserDao {

    @Override
    public boolean register(UserEntity user) {
        if (user == null) return false;
        String hql = "from BlUserEntity where username=?";
        List list = getHibernateTemplate().find(hql, user.getUsername());
        if (list != null && list.size() != 0)
            return false;//用户已注册
        user.setUserid(String.valueOf(System.currentTimeMillis()));
        getHibernateTemplate().save(user);
        return true;
    }

    @Override
    public String login(UserEntity user) {
        if (user == null) return null;
        String hql = "from BlUserEntity where username=? and userpass=?";
        List list = getHibernateTemplate().find(hql, user.getUsername(), user.getUserpass());
        if (list != null && list.size() != 0)
            return ((UserEntity) list.get(0)).getUsernick(); //登录成功
        return null;
    }

}
