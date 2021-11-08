package com.plms.springframework;

import com.plms.springframework.context.support.ClassPathXmlApplicationContext;
import com.plms.springframework.service.HelloService;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @Author bigboss
 * @Date 2021/11/8 20:59
 */
public class AwareInterfaceTest {

    @Test
    public void testAwareInterface() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        HelloService helloService = applicationContext.getBean("helloService", HelloService.class);
        assertThat(helloService.getApplicationContext()).isNotNull();
        assertThat(helloService.getBeanFactory()).isNotNull();
        applicationContext.registerShutdownHook();
    }
}
