package com.plms.springframework;

import com.plms.springframework.bean.Car;
import com.plms.springframework.bean.Person;
import com.plms.springframework.bean.factory.support.DefaultListableBeanFactory;
import com.plms.springframework.bean.factory.xml.XmlBeanDefinitionReader;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @Author bigboss
 * @Date 2021/11/3 14:40
 */
public class ReadBeanFromXMLTest {

    @Test
    public void testXmlFile() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        xmlBeanDefinitionReader.loadBeanDefinitions("classpath:spring.xml");
        Person person = (Person) beanFactory.getBean("person");
        System.out.println(person);
//        assertThat(person.getName()).isEqualTo("derek");
//        assertThat(person.getCar().getBrand()).isEqualTo("porsche");

        Car car = (Car) beanFactory.getBean("car");
        System.out.println(car);
//        assertThat(car.getBrand()).isEqualTo("porsche");
    }
}
