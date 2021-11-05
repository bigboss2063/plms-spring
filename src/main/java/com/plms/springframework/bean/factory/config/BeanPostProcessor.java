package com.plms.springframework.bean.factory.config;

import com.plms.springframework.bean.BeansException;

/**
 * 在Bean初始化之前和初始化之后对Bean进行操作，可以修改Bean或替换Bean，是AOP的关键
 * @Author bigboss
 * @Date 2021/11/3 21:00
 */
public interface BeanPostProcessor {

    /**
     * 在Bean初始化之前执行
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException;

    /**
     * 在Bean初始化之后执行
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException;
}
