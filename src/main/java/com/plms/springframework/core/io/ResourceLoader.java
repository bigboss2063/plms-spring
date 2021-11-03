package com.plms.springframework.core.io;

/**
 * @Author bigboss
 * @Date 2021/11/2 20:35
 */
public interface ResourceLoader {

    String CLASSPATH_URL_PREFIX = "classpath:";

    /**
     * 获取资源
     * @param location 资源地址
     * @return 资源
     */
    Resource getResource(String location);
}
