package com.plms.springframework.bean.factory.config;

/**
 * @Author bigboss
 * @Date 2021/11/2 19:45
 */
public class BeanReference {

    private final String beanName;

    public BeanReference(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return beanName;
    }
}
