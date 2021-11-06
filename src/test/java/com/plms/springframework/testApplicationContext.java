package com.plms.springframework;

import cn.hutool.core.lang.Assert;
import com.plms.springframework.bean.Car;
import com.plms.springframework.bean.Person;
import com.plms.springframework.context.support.ClassPathXmlApplicationContext;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @Author bigboss
 * @Date 2021/11/6 14:54
 */
public class testApplicationContext {

    @Test
    public void testApplicationContext() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        Person person = applicationContext.getBean("person", Person.class);
        System.out.println(person);
        //name属性在CustomBeanFactoryPostProcessor中被修改为bigboss
        Assert.isTrue((person.getName()).equals("bigboss"));
        Car car = applicationContext.getBean("car", Car.class);
        System.out.println(car);
        //brand属性在CustomerBeanPostProcessor中被修改为hongqi
        Assert.isTrue((car.getBrand()).equals("hongqi"));
    }
}
