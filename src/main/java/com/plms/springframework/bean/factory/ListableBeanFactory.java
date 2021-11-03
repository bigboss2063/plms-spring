package com.plms.springframework.bean.factory;

import com.plms.springframework.bean.BeansException;

import java.beans.Beans;
import java.util.Map;

/**
 * ListableBeanFactory 一个扩展 Bean 工厂接口的接口
 * @Author bigboss
 * @Date 2021/11/3 13:17
 */
public interface ListableBeanFactory extends BeanFactory {

    /**
     * 返回指定类型的所有实例
     * @param type
     * @param <T>
     * @return
     * @throws BeansException
     */
    <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException;

    /**
     * 返回定义的所有bean的名称
     * @return
     */
    String[] getBeanDefinitionNames();
}
