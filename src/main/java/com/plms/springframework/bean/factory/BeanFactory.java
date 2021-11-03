package com.plms.springframework.bean.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author bigboss
 * @Date 2021/11/1 21:14
 */
public interface BeanFactory {
    /**
     * 获取bean实例
     * @param beanName bean名称
     * @return bean实例
     */
    Object getBean(String beanName);

    /**
     * 在有参构造函数的情况下获取bean实例
     * @param beanName bean名称
     * @param args 参数
     * @return bean实例
     */
    Object getBean(String beanName, Object... args);

    /**
     * 根据类型获取bean实例
     * @param beanName bean名称
     * @param requiredType 要求的类型
     * @param <T> 要求的类型
     * @return bean实例
     */
    <T> T getBean(String beanName, Class<T> requiredType);
}
