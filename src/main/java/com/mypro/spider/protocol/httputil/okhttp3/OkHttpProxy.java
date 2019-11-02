package com.mypro.spider.protocol.httputil.okhttp3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.net.spi.DefaultProxySelector;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/2/22
 */
public class OkHttpProxy extends ProxySelector {

    private static final Logger LOG = LoggerFactory
            .getLogger(MethodHandles.lookup().lookupClass());

    private OkHttp http;

    OkHttpProxy(OkHttp http) {
        this.http = http;
    }

    @SuppressWarnings("serial")
    private final List<Proxy> noProxy = new ArrayList<Proxy>() {
        {
            add(Proxy.NO_PROXY);
        }
    };

    @Override
    public List<Proxy> select(URI uri) {
        return noProxy;
    }
    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        http.removeProxy(sa.toString().substring(1));
//        LOG.error("Connection to proxy failed for {}: proxy : [{}], e : {}", uri, sa.toString(), ioe);
    }


}
