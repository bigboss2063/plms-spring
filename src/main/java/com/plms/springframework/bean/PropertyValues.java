package com.plms.springframework.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author bigboss
 * @Date 2021/11/2 19:39
 */
public class PropertyValues {
    private final List<PropertyValue> propertyValues = new ArrayList<>();

    public void addPropertyValue(PropertyValue propertyValue) {
        for (int i = 0; i < this.propertyValues.size(); i++) {
            PropertyValue value = this.propertyValues.get(i);
            if (value.getName().equals(propertyValue.getName())) {
                this.propertyValues.set(i, propertyValue);
                return;
            }
        }
        this.propertyValues.add(propertyValue);
    }

    public PropertyValue[] getPropertyValues() {
        return this.propertyValues.toArray(new PropertyValue[0]);
    }

    public PropertyValue getPropertyValue(String propertyName) {
        for (PropertyValue propertyValue : this.propertyValues) {
            if (propertyValue.getName().equals(propertyName)) {
                return propertyValue;
            }
        }
        return null;
    }
}
