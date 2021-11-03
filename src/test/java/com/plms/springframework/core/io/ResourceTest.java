package com.plms.springframework.core.io;

import cn.hutool.core.io.IoUtil;
import com.plms.springframework.core.io.ClassPathResource;
import com.plms.springframework.core.io.DefaultResourceLoader;
import com.plms.springframework.core.io.Resource;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Author bigboss
 * @Date 2021/11/2 20:44
 */
public class ResourceTest {

    private DefaultResourceLoader resourceLoader;

    @Before
    public void init() {
        resourceLoader = new DefaultResourceLoader();
    }

    @Test
    public void classPathResourceTest() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:important.property");
        InputStream inputStream = resource.getInputStream();
        String s = IoUtil.readUtf8(inputStream);
        System.out.println(s);
    }

    @Test
    public void filePathResourceTest() throws IOException {
        Resource resource = resourceLoader.getResource("src/test/resources/important.property");
        InputStream inputStream = resource.getInputStream();
        String s = IoUtil.readUtf8(inputStream);
        System.out.println(s);
    }

    @Test
    public void urlPathResourceTest() throws IOException {
        Resource resource = resourceLoader.getResource("https://github.com/DerekYRC/mini-spring/blob/main/README.md");
        InputStream inputStream = resource.getInputStream();
        String s = IoUtil.readUtf8(inputStream);
        System.out.println(s);
    }
}
