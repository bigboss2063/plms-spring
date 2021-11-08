package com.plms.springframework.context.support;

import com.plms.springframework.bean.BeansException;
import com.plms.springframework.bean.factory.config.BeanPostProcessor;
import com.plms.springframework.context.ApplicationContext;
import com.plms.springframework.context.ApplicationContextAware;

/**
 * @Author bigboss
 * @Date 2021/11/8 20:51
 */
public class ApplicationContextAwareProcessor implements BeanPostProcessor {

    private final ApplicationContext applicationContext;

    public ApplicationContextAwareProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ApplicationContextAware) {
            ((ApplicationContextAware) bean).setApplicationContext(applicationContext);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
