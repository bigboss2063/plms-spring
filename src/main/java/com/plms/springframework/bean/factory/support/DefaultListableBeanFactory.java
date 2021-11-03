package com.plms.springframework.bean.factory.support;

import com.plms.springframework.bean.BeansException;
import com.plms.springframework.bean.factory.config.BeanDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author bigboss
 * @Date 2021/11/1 22:01
 */
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements BeanDefinitionRegistry {
    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    @Override
    protected BeanDefinition getBeanDefinition(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new BeansException("no beanDefinition named [" + beanName + "]");
        }
        return beanDefinition;
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanName, beanDefinition);
    }
}
