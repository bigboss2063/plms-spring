package com.plms.springframework.bean;

/**
 * @Author bigboss
 * @Date 2021/11/2 19:39
 */
public class PropertyValue {
    private final String name;

    private final Object value;

    public PropertyValue(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}
