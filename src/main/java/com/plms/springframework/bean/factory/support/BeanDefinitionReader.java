package com.plms.springframework.bean.factory.support;

import com.plms.springframework.bean.BeansException;
import com.plms.springframework.core.io.Resource;
import com.plms.springframework.core.io.ResourceLoader;

/**
 * @Author bigboss
 * @Date 2021/11/3 13:55
 */
public interface BeanDefinitionReader {
    /**
     * 获取Bean定义的注册表
     * @return
     */
    BeanDefinitionRegistry getRegistry();

    /**
     * 获取资源加载器
     * @return
     */
    ResourceLoader getResourceLoader();

    /**
     * 根据资源加载Bean定义
     * @param resource
     * @throws BeansException
     */
    void loadBeanDefinitions(Resource resource) throws BeansException;

    /**
     * 根据多个资源加载Bean定义
     * @param resources
     * @throws BeansException
     */
    void loadBeanDefinitions(Resource... resources) throws BeansException;

    /**
     * 根据配置文件路径加载Bean定义
     * @param location
     * @throws BeansException
     */
    void loadBeanDefinitions(String location) throws BeansException;
}
