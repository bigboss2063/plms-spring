package com.plms.springframework.bean;

import com.plms.springframework.bean.factory.config.BeanDefinition;
import com.plms.springframework.bean.factory.support.DefaultListableBeanFactory;
import com.plms.springframework.service.HelloService;
import org.junit.Test;

/**
 * @Author bigboss
 * @Date 2021/11/2 14:37
 */
public class BeanInstantiationStrategyTest {

    @Test
    public void test_BeanFactory() {
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        BeanDefinition beanDefinition = new BeanDefinition(HelloService.class);
        factory.registerBeanDefinition("helloService", beanDefinition);
        HelloService bean = (HelloService) factory.getBean("helloService", "promise");
        System.out.println(bean.hello());
    }
}
