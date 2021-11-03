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

    /**
     * 根据bean名称获取定义
     * @param beanName
     * @return
     */
    BeanDefinition getBeanDefinition(String beanName);

    /**
     * 获取所有Bean的名称
     * @return
     */
    String[] getBeanDefinitionNames();

    /**
     * 是否包含指定名称的BeanDefinition
     *
     * @param beanName
     * @return
     */
    boolean containsBeanDefinition(String beanName);
}
