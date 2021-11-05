package com.plms.springframework.bean.factory.config;

import com.plms.springframework.bean.factory.ConfigurableListableBeanFactory;

/**
 * Spring提供的容器拓展机制，可以在Bean实例化之前修改Beandefinition
 * @Author bigboss
 * @Date 2021/11/3 20:51
 */
public interface BeanFactoryPostProcessor {

    /**
     * 在Bean实例化之前执行
     * @param configurableBeanFactory
     */
    void postProcessBeanFactory(ConfigurableListableBeanFactory configurableBeanFactory);
}
