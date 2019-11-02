package com.mypro.spider.protocol.httputil.okhttp3;

import com.google.common.collect.Lists;
import com.mypro.spider.data.ExtendMap;
import com.mypro.spider.protocol.ProtocolException;
import com.mypro.spider.protocol.httputil.HttpBase;
import com.mypro.spider.protocol.httputil.Response;
import okhttp3.*;
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author fgzhong
 * @description: okhttp
 * @since 2019/1/29
 */
public class OkHttp extends HttpBase {

    private volatile OkHttpClient.Builder builder;
    private final static List<String> PROXY_LIST = Lists.newArrayList();
    private final static Random RANDOM = new Random();

    private OkHttp(){}

    @Override
    public void setConf(Configuration conf) {
        super.setConf(conf);
        if (builder == null) {
            synchronized (OkHttp.class) {
                if (builder == null) {
                    List<Protocol> protocols = new ArrayList<>();
                    protocols.add(okhttp3.Protocol.HTTP_2);
                    protocols.add(okhttp3.Protocol.HTTP_1_1);

                    builder = new OkHttpClient.Builder()
                            .protocols(protocols)
                            .followRedirects(true)
                            .addInterceptor(new RetryIntercepter(5))
                            .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                            .writeTimeout(timeout, TimeUnit.MILLISECONDS)
                            .readTimeout(timeout, TimeUnit.MILLISECONDS)
                    ;

                    if (useProxy) {
                        builder.proxySelector(new OkHttpProxy(this));
                    }
                }
            }
        }
    }

    @Override
    protected Response getResponse(String url, ExtendMap headers) {
        return new OkHttpResponse(this, url, headers);
    }

    @Override
    public Response getLocalResponse(String url, ExtendMap headers) throws ProtocolException, IOException {
        return null;
    }

    public OkHttpClient getClient() {
        if(builder == null) {this.setConf(null);}
        return this.builder.proxy(this.getProxy()).build();
    }

    @Override
    public void addProxyStr(List<String> proxyList) {
        synchronized (PROXY_LIST) {
            if (proxyList != null) {
                PROXY_LIST.clear();
                PROXY_LIST.addAll(proxyList);
            }
        }
    }

    private Proxy getProxy() {
        synchronized (PROXY_LIST) {
            if (PROXY_LIST.size() == 0) {
                return null;
            }
            String proxyStr = PROXY_LIST.get(RANDOM.nextInt(PROXY_LIST.size()));
            return new Proxy(Proxy.Type.HTTP,
                    new InetSocketAddress(proxyStr.split(":")[0], Integer.valueOf(proxyStr.split(":")[1])));
        }
    }

    public void removeProxy(String proxyStr) {
        synchronized (PROXY_LIST) {
            if (PROXY_LIST.contains(proxyStr)) {
                PROXY_LIST.remove(proxyStr);
            }
        }
    }
}
