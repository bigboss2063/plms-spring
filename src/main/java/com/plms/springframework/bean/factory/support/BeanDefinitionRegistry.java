package com.plms.springframework.bean.factory.support;

import com.plms.springframework.bean.factory.config.BeanDefinition;

/**
 * @Author bigboss
 * @Date 2021/11/1 21:52
 */
public interface BeanDefinitionRegistry {
    /**
     * 注册bean定义
     * @param beanName
     * @param beanDefinition
     */
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);
}
