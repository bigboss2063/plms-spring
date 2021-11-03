package com.plms.springframework.service;

import com.plms.springframework.dao.UserDao;

/**
 * @Author bigboss
 * @Date 2021/11/1 21:21
 */
public class HelloService {

    private String uId;

    private UserDao userDao;

    public String hello() {
        return "hello world";
    }

    public void queryUserInfo() {
        System.out.println("查询用户信息：" + userDao.queryUserName(uId));
    }
}
