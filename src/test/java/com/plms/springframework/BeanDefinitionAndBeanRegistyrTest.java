package com.plms.springframework;

import cn.hutool.core.lang.Assert;
import com.plms.springframework.bean.factory.config.BeanDefinition;
import com.plms.springframework.bean.factory.support.DefaultListableBeanFactory;
import com.plms.springframework.service.HelloService;
import org.junit.Test;

/**
 * @Author bigboss
 * @Date 2021/11/1 22:20
 */
public class BeanDefinitionAndBeanRegistyrTest {

    @Test
    public void test_BeanFactory() {
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        BeanDefinition beanDefinition = new BeanDefinition(HelloService.class);
        factory.registerBeanDefinition("helloService", beanDefinition);
        HelloService helloService = (HelloService) factory.getBean("helloService");
        Assert.isTrue(helloService.hello().equals("hello world"));
    }
}
