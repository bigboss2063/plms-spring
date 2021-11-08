package com.plms.springframework;

import com.plms.springframework.context.support.ClassPathXmlApplicationContext;
import org.junit.Test;

/**
 * @Author bigboss
 * @Date 2021/11/7 17:11
 */
public class InitAndDestroyMethodTest {

    @Test
    public void testInitAndDestroyMethod() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:init-and-destroy-method.xml");
        applicationContext.registerShutdownHook();  //或者手动关闭 applicationContext.close();
    }
}
