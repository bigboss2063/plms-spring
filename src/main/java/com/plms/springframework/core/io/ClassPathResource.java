package com.plms.springframework.core.io;

import cn.hutool.core.lang.Assert;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Author bigboss
 * @Date 2021/11/2 20:34
 */
public class ClassPathResource implements Resource{

    private final String path;

    public ClassPathResource(String path) {
        Assert.notNull(path, "Path must not be null");
        this.path = path;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException(
                    this.path + " cannot be opened because it does not exist");
        }
        return is;
    }
}
