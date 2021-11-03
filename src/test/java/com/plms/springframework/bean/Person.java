package com.plms.springframework.bean;

import lombok.Data;

/**
 * @Author bigboss
 * @Date 2021/11/3 14:39
 */

@Data
public class Person {

    private String name;

    private int age;

    private Car car;
}
