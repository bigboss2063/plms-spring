package com.plms.springframework.dao;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author bigboss
 * @Date 2021/11/2 19:54
 */
public class UserDao {
    private static Map<String, String> hashMap = new HashMap<>();

    static {
        hashMap.put("10001", "bigboss");
        hashMap.put("10002", "promise");
    }

    public String queryUserName(String uId) {
        return hashMap.get(uId);
    }
}
