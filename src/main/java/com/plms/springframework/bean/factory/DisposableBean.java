package com.plms.springframework.bean.factory;

import com.plms.springframework.bean.BeansException;

import java.lang.reflect.InvocationTargetException;

/**
 * @Author bigboss
 * @Date 2021/11/7 15:50
 */
public interface DisposableBean {

    /**
     * 销毁Bean实例
     * @throws BeansException
     */
    void destroy() throws Exception;
}
