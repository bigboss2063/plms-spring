package com.plms.springframework.core.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Author bigboss
 * @Date 2021/11/2 20:35
 */
public interface Resource {
    /**
     * 获取输入流
     * @return 输入流
     * @throws IOException io异常
     */
    InputStream getInputStream() throws IOException;
}
