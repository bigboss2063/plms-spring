package com.plms.springframework.context;

import com.plms.springframework.bean.BeansException;

/**
 * @Author bigboss
 * @Date 2021/11/6 13:21
 */
public interface ConfigurableApplicationContext extends ApplicationContext{

    /**
     * 刷新容器
     * @throws BeansException
     */
    void refresh() throws BeansException;
}
