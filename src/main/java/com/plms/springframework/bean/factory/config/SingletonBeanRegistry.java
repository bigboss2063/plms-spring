package com.plms.springframework.bean.factory.config;

/**
 * 单例bean注册表
 * @Author bigboss
 * @Date 2021/11/1 21:48
 */
public interface SingletonBeanRegistry {

    /**
     * 获取单例bean实例
     * @param beanName
     * @return
     */
    Object getSingleton(String beanName);
}
