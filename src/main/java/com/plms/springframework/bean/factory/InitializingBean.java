package com.plms.springframework.bean.factory;

import com.plms.springframework.bean.BeansException;

/**
 * @Author bigboss
 * @Date 2021/11/7 15:50
 */
public interface InitializingBean {

    /**
     * 在Bean完成属性填充之后执行
     * @throws BeansException
     */
    void afterPropertiesSet() throws BeansException;
}
