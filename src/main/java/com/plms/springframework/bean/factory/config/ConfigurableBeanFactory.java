package com.plms.springframework.bean.factory.config;

import com.plms.springframework.bean.factory.HierarchicalBeanFactory;

/**
 * ConfigurableBeanFactory 可获取 BeanPostProcessor、BeanClassLoader等的一个配置化接口
 * @Author bigboss
 * @Date 2021/11/3 13:16
 */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {

    /**
     * 添加一个beanPostProcessor
     * @param beanPostProcessor
     */
    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);
}
