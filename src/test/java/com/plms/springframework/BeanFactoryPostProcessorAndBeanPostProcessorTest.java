package com.plms.springframework;

import cn.hutool.core.lang.Assert;
import com.plms.springframework.bean.Car;
import com.plms.springframework.bean.Person;
import com.plms.springframework.bean.factory.support.DefaultListableBeanFactory;
import com.plms.springframework.bean.factory.xml.XmlBeanDefinitionReader;
import com.plms.springframework.common.CustomBeanFactoryPostProcessor;
import com.plms.springframework.common.CustomBeanPostProcessor;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @Author bigboss
 * @Date 2021/11/4 17:52
 */
public class BeanFactoryPostProcessorAndBeanPostProcessorTest {

    @Test
    public void testBeanFactoryPostProcessor() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        xmlBeanDefinitionReader.loadBeanDefinitions("classpath:spring.xml");
        CustomBeanFactoryPostProcessor customBeanFactoryPostProcessor = new CustomBeanFactoryPostProcessor();
        customBeanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
        Person person = (Person) beanFactory.getBean("person");
        System.out.println(person);
        Assert.isTrue(person.getName().equals("bigboss"));
    }

    @Test
    public void testBeanPostProcessor() throws Exception {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        beanDefinitionReader.loadBeanDefinitions("classpath:spring.xml");
        CustomBeanPostProcessor customerBeanPostProcessor = new CustomBeanPostProcessor();
        beanFactory.addBeanPostProcessor(customerBeanPostProcessor);
        Car car = (Car) beanFactory.getBean("car");
        System.out.println(car);
        //brand属性在CustomerBeanPostProcessor中被修改为lamborghini
        Assert.isTrue(car.getBrand().equals("hongqi"));
    }
}
