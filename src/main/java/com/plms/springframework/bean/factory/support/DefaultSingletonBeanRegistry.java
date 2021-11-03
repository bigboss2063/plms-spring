package com.plms.springframework.bean.factory.support;

import com.plms.springframework.bean.factory.config.SingletonBeanRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author bigboss
 * @Date 2021/11/1 21:52
 */
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {

    private Map<String, Object> singletonObjects = new HashMap<>();

    @Override
    public Object getSingleton(String beanName) {
        return singletonObjects.get(beanName);
    }

    public void addSingleton(String beanName, Object bean) {
        singletonObjects.put(beanName, bean);
    }
}
