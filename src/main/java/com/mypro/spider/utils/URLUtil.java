package com.mypro.spider.utils;

import java.net.URL;

/**
 * @author fgzhong
 * @since 2019/1/7
 */
public class URLUtil {

    public static void main(String[] args) throws Exception{
        URL url = new URL("http://www.runoob.com/index.html?language=cn#j2se");
        System.out.println("URL 为：" + url.toString());
        System.out.println("协议为：" + url.getProtocol());
        System.out.println("验证信息：" + url.getAuthority());
        System.out.println("文件名及请求参数：" + url.getFile());
        System.out.println("主机名：" + url.getHost());
        System.out.println("路径：" + url.getPath());
        System.out.println("端口：" + url.getPort());
        System.out.println("默认端口：" + url.getDefaultPort());
        System.out.println("请求参数：" + url.getQuery());
        System.out.println("定位位置：" + url.getRef());
    }
}
