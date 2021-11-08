package com.plms.springframework.context;

import com.plms.springframework.bean.BeansException;

/**
 * @Author bigboss
 * @Date 2021/11/8 20:44
 */
public interface ApplicationContextAware {

    void setApplicationContext(ApplicationContext applicationContext) throws BeansException;
}
