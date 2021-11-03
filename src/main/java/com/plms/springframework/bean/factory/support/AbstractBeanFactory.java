package com.plms.springframework.bean.factory.support;

import com.plms.springframework.bean.factory.BeanFactory;
import com.plms.springframework.bean.factory.config.BeanDefinition;

/**
 * @Author bigboss
 * @Date 2021/11/1 21:51
 */
public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory {
    @Override
    public Object getBean(String beanName) {
        return doGetBean(beanName, null);
    }

    @Override
    public Object getBean(String beanName, Object... args) {
        return doGetBean(beanName, args);
    }

    protected <T> T doGetBean(final String beanName, final Object[] args) {
        Object bean = getSingleton(beanName);
        if (bean != null) {
            return (T) bean;
        }
        BeanDefinition beanDefinition = getBeanDefinition(beanName);
        return (T) createBean(beanName, beanDefinition, args);
    }

    /**
     * 获取bean定义
     * @param beanName bean名称
     * @return bean定义
     */
    protected abstract BeanDefinition getBeanDefinition(String beanName);

    /**
     * 新建bean
     * @param beanName bean名称
     * @param beanDefinition bean定义
     * @param args 构造函数参数
     * @return bean实例
     */
    protected abstract Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args);
}
