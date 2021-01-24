package cn.corbinhu.community.utils;

import cn.corbinhu.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author: CorbinHu
 * @date: 2021/1/20 14:14
 * @description: 持有用户信息，用于代替session
 */

@Component
public class HostHolder {

    private ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    public void setUser(User user){
        userThreadLocal.set(user);
    }

    public User getUser(){
        return userThreadLocal.get();
    }

    public void clear(){
        userThreadLocal.remove();
    }
}
