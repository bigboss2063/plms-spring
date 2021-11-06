package com.plms.springframework.bean.factory;

import com.plms.springframework.bean.BeansException;
import com.plms.springframework.bean.factory.config.AutowireCapableBeanFactory;
import com.plms.springframework.bean.factory.config.BeanDefinition;
import com.plms.springframework.bean.factory.config.BeanPostProcessor;
import com.plms.springframework.bean.factory.config.ConfigurableBeanFactory;

/**
 * ConfigurableListableBeanFactory 提供分析和修改Bean以及预先实例化的操作接口
 * @Author bigboss
 * @Date 2021/11/3 13:17
 */
public interface ConfigurableListableBeanFactory extends ListableBeanFactory, ConfigurableBeanFactory, AutowireCapableBeanFactory {

    /**
     * 根据名称获取bean定义
     * @param beanName
     * @return
     */
    BeanDefinition getBeanDefinition(String beanName);

    @Override
    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    /**
     * 提前实例化所有单例实例
     * @throws BeansException
     */
    void preInstantiateSingletons() throws BeansException;
}
