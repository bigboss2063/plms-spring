package com.plms.springframework;

import cn.hutool.core.lang.Assert;
import com.plms.springframework.bean.Car;
import com.plms.springframework.context.support.ClassPathXmlApplicationContext;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @Author bigboss
 * @Date 2021/11/8 21:46
 */
public class PrototypeBeanTest {

    @Test
    public void testPrototype() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:prototype-bean.xml");
        Car car1 = applicationContext.getBean("car", Car.class);
        Car car2 = applicationContext.getBean("car", Car.class);
        Assert.isTrue(car1 != car2);
    }
}
