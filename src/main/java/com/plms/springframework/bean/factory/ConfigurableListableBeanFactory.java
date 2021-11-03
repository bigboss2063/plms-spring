package com.plms.springframework.bean.factory;

import com.plms.springframework.bean.factory.config.AutowireCapableBeanFactory;
import com.plms.springframework.bean.factory.config.BeanDefinition;
import com.plms.springframework.bean.factory.config.ConfigurableBeanFactory;

/**
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
}
