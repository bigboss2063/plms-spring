package com.plms.springframework.bean.factory;

import com.plms.springframework.bean.BeansException;

/**
 * @Author bigboss
 * @Date 2021/11/8 20:43
 */
public interface BeanFactoryAware {

    void setBeanFactory(BeanFactory beanFactory) throws BeansException;
}
