package com.plms.springframework.context.support;

import com.plms.springframework.bean.factory.support.DefaultListableBeanFactory;
import com.plms.springframework.bean.factory.xml.XmlBeanDefinitionReader;

/**
 * @Author bigboss
 * @Date 2021/11/6 14:39
 */
public abstract class AbstractXmlApplicationContext extends AbstractRefreshableApplicationContext {
    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) {
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(beanFactory, this);
        String[] configLocations = getConfigLocations();
        if (configLocations != null) {
            xmlBeanDefinitionReader.loadBeanDefinitions(configLocations);
        }
    }

    /**
     * 获取配置文件的地址
     * @return
     */
    protected abstract String[] getConfigLocations();
}
