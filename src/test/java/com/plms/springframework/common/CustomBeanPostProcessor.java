package com.plms.springframework.common;

import com.plms.springframework.bean.BeansException;
import com.plms.springframework.bean.Car;
import com.plms.springframework.bean.factory.config.BeanPostProcessor;

/**
 * @Author bigboss
 * @Date 2021/11/4 18:01
 */
public class CustomBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if ("car".equals(beanName)) {
            ((Car) bean).setBrand("hongqi");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("CustomBeanPostProcessor#postProcessAfterInitialization");
        return bean;
    }
}
