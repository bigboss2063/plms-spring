package com.plms.springframework.bean.factory.config;

import com.plms.springframework.bean.factory.BeanFactory;

/**
 * AutowireCapableBeanFactory 自动化处理Bean工厂配置的接口
 * @Author bigboss
 * @Date 2021/11/3 13:16
 */
public interface AutowireCapableBeanFactory extends BeanFactory {

    /**
     * 执行BeanPostProcessor的postProcessBeforeInitialization方法
     * @param beanName
     * @param bean
     * @return
     */
    Object applyBeanPostProcessorsBeforeInitialization(String beanName, Object bean);

    /**
     * 执行BeanPostProcessor的postProcessAfterInitialization方法
     * @param beanName
     * @param bean
     * @return
     */
    Object applyBeanPostProcessorsAfterInitialization(String beanName, Object bean);
}
