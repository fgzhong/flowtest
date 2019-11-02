package com.mypro.spider.protocol.httputil;

import com.mypro.spider.constant.ProtocolStatus;
import com.mypro.spider.data.ExtendMap;
import com.mypro.spider.parse.Content;
import com.mypro.spider.protocol.Protocol;
import com.mypro.spider.protocol.ProtocolException;
import com.mypro.spider.protocol.ProtocolOutput;
import com.mypro.spider.utils.ExtendMapUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.Proxy;
import java.util.List;
import java.util.Map;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/1/29
 */
public abstract class HttpBase extends Configured implements Protocol{

    protected static final Logger LOG = LoggerFactory
            .getLogger(MethodHandles.lookup().lookupClass());

    public static final int BUFFER_SIZE = 8 * 1024;
    public static final byte[] EMPTY_CONTENT = new byte[0];
    public static final int DEFAULT_TIMEOUT = 10000;
    public static final boolean DEFAULT_USEPROXY = true;

    public static int timeout;
    public static int maxContent = 64 * 1024 * 1024;  // 单位 byte -> 64M
    public static int maxDuration = 300;
    public static boolean useHttp2 = true;
    public static Proxy.Type proxyType = Proxy.Type.HTTP;
    public static boolean useProxy = true;

    protected static volatile Configuration conf;

    @Override
    public void setConf(Configuration conf) {
        if (conf == null) {
            conf = new Configuration();
        }
        super.setConf(conf);
        timeout = conf.getInt("fetch.http.timeout", DEFAULT_TIMEOUT);
        useProxy = conf.getBoolean("fetch.http.useproxy", DEFAULT_USEPROXY);
    }

    @Override
    public ProtocolOutput getProtocolOutput(String url, Map<String, Object> map) {
        try {
            Response response = getResponse(url, ExtendMapUtil.mapToExtendMap(map));
            int code = response.getCode();
            byte[] content = response.getContent();
            if (code == 200 && content.length >0) {
                return new ProtocolOutput(Content.newContent(url, content, map), ProtocolStatus.SUCCESS);
            } else if (code == 404) {
                return new ProtocolOutput(Content.newContent(url, code), ProtocolStatus.FAIL);
            } else {
                return new ProtocolOutput(Content.newContent(url, code), ProtocolStatus.RETRY);
            }
        }  catch (Exception e) {
            return new ProtocolOutput(Content.newContent(url, ProtocolStatus.DEPTH_ERROR), ProtocolStatus.FAIL);
        }
    }

    /**  请求返回  */
    protected abstract Response getResponse(String url, ExtendMap headers);

    /**  本地测试请求返回  */
    public abstract Response getLocalResponse(String url, ExtendMap headers) throws ProtocolException, IOException;

    /**  添加代理  */
    public abstract void addProxyStr(List<String> proxy);
}
