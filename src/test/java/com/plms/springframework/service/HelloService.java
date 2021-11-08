package com.plms.springframework.service;

import com.plms.springframework.bean.BeansException;
import com.plms.springframework.bean.factory.BeanFactory;
import com.plms.springframework.bean.factory.BeanFactoryAware;
import com.plms.springframework.context.ApplicationContext;
import com.plms.springframework.context.ApplicationContextAware;
import com.plms.springframework.dao.UserDao;

/**
 * @Author bigboss
 * @Date 2021/11/1 21:21
 */
public class HelloService implements ApplicationContextAware, BeanFactoryAware {

    private ApplicationContext applicationContext;

    private BeanFactory beanFactory;

    public String hello() {
        System.out.println("hello");
        return "hello";
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }
}
