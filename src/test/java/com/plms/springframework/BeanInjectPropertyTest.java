package com.plms.springframework;

import com.plms.springframework.bean.PropertyValue;
import com.plms.springframework.bean.PropertyValues;
import com.plms.springframework.bean.factory.config.BeanDefinition;
import com.plms.springframework.bean.factory.config.BeanReference;
import com.plms.springframework.bean.factory.support.DefaultListableBeanFactory;
import com.plms.springframework.dao.UserDao;
import com.plms.springframework.service.HelloService;
import org.junit.Test;

/**
 * @Author bigboss
 * @Date 2021/11/2 19:56
 */
public class BeanInjectPropertyTest {

    @Test
    public void test_beanFactory() {
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        factory.registerBeanDefinition("userDao", new BeanDefinition(UserDao.class));
        PropertyValues propertyValues = new PropertyValues();
        propertyValues.addPropertyValue(new PropertyValue("uId", "10001"));
        propertyValues.addPropertyValue(new PropertyValue("userDao", new BeanReference("userDao")));
        BeanDefinition beanDefinition = new BeanDefinition(HelloService.class, propertyValues);
        factory.registerBeanDefinition("helloService", beanDefinition);
        HelloService helloService = (HelloService) factory.getBean("helloService");
        helloService.queryUserInfo();
    }
}
