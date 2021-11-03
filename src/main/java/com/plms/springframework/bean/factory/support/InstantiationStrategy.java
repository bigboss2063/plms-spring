package com.plms.springframework.bean.factory.support;

import com.plms.springframework.bean.factory.config.BeanDefinition;

import java.lang.reflect.Constructor;

/**
 * @Author bigboss
 * @Date 2021/11/2 14:15
 */
public interface InstantiationStrategy {

    /**
     * 实例化方法
     * @param beanDefinition bean定义
     * @param beanName bean名称
     * @param constructor 构造器
     * @param args 参数列表
     * @return bean对象
     */
    Object instantiate(BeanDefinition beanDefinition, String beanName, Constructor constructor, Object[] args);
}
