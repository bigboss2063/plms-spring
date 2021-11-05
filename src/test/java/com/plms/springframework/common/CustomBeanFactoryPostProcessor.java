package com.plms.springframework.common;

import com.plms.springframework.bean.PropertyValue;
import com.plms.springframework.bean.PropertyValues;
import com.plms.springframework.bean.factory.ConfigurableListableBeanFactory;
import com.plms.springframework.bean.factory.config.BeanDefinition;
import com.plms.springframework.bean.factory.config.BeanFactoryPostProcessor;
import com.plms.springframework.bean.factory.config.ConfigurableBeanFactory;

/**
 * @Author bigboss
 * @Date 2021/11/4 17:54
 */
public class CustomBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableBeanFactory) {
        BeanDefinition person = configurableBeanFactory.getBeanDefinition("person");
        PropertyValues propertyValues = person.getPropertyValues();
        propertyValues.addPropertyValue(new PropertyValue("name", "bigboss"));
    }
}
