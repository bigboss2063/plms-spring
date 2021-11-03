package com.plms.springframework.bean;

/**
 * @Author bigboss
 * @Date 2021/11/1 21:50
 */
public class BeansException extends RuntimeException{

    public BeansException(String message) {
        super(message);
    }

    public BeansException(String message, Throwable cause) {
        super(message, cause);
    }
}
