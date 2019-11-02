package com.mypro.spider.protocol.httputil;

import java.net.URL;
import java.util.Map;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/1/29
 */
public interface Response {

    String getUrl();
    int getCode();
    byte[] getContent();

}
