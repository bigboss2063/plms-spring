package com.plms.springframework.context.support;

import com.plms.springframework.bean.BeansException;
import com.plms.springframework.bean.factory.ConfigurableListableBeanFactory;
import com.plms.springframework.bean.factory.config.BeanFactoryPostProcessor;
import com.plms.springframework.bean.factory.config.BeanPostProcessor;
import com.plms.springframework.context.ConfigurableApplicationContext;
import com.plms.springframework.core.io.DefaultResourceLoader;

import java.util.Map;

/**
 * @Author bigboss
 * @Date 2021/11/6 13:32
 */
public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext {
    @Override
    public void refresh() throws BeansException {
        // 刷新容器，创建 BeanFactory，并加载 BeanDefinition
        refreshBeanFactory();
        // 获取 BeanFactory
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        // 在Bean实例化之前调用所有的 postProcessBeanFactory方法
        invokeBeanFactoryPostProcessors(beanFactory);
        // 注册BeanPostProcessor
        registerBeanPostProcessors(beanFactory);
        // 提前实例化所有单例Bean
        beanFactory.preInstantiateSingletons();
    }

    /**
     * 刷新Bean工厂
     * @throws BeansException
     */
    protected abstract void refreshBeanFactory() throws BeansException;

    /**
     * 获取 ConfigurableListableBeanFactory
     * @return
     */
    protected abstract ConfigurableListableBeanFactory getBeanFactory();

    protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        Map<String, BeanFactoryPostProcessor> beanFactoryPostProcessorMap = beanFactory.getBeansOfType(BeanFactoryPostProcessor.class);
        for (BeanFactoryPostProcessor beanFactoryPostProcessor : beanFactoryPostProcessorMap.values()) {
            beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
        }
    }

    protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        Map<String, BeanPostProcessor> beanPostProcessorMap = beanFactory.getBeansOfType(BeanPostProcessor.class);
        for (BeanPostProcessor beanPostProcessor : beanPostProcessorMap.values()) {
            beanFactory.addBeanPostProcessor(beanPostProcessor);
        }
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return getBeanFactory().getBeansOfType(type);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return getBeanFactory().getBeanDefinitionNames();
    }

    @Override
    public Object getBean(String beanName) {
        return getBeanFactory().getBean(beanName);
    }

    @Override
    public <T> T getBean(String beanName, Class<T> requiredType) {
        return getBeanFactory().getBean(beanName, requiredType);
    }

    @Override
    public Object getBean(String beanName, Object... args) {
        return getBeanFactory().getBean(beanName, args);
    }
}
