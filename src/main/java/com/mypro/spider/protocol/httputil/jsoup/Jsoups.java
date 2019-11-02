package com.mypro.spider.protocol.httputil.jsoup;

import com.google.common.collect.Lists;
import com.mypro.spider.data.ExtendMap;
import com.mypro.spider.protocol.ProtocolException;
import com.mypro.spider.protocol.httputil.HttpBase;
import com.mypro.spider.protocol.httputil.Response;
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Random;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/3/30
 */
public class Jsoups extends HttpBase {

    private final static List<String> PROXY_LIST = Lists.newArrayList();
    private final static Random RANDOM = new Random();

    @Override
    public void setConf(Configuration conf) {
        super.setConf(conf);
    }

    @Override
    protected Response  getResponse(String url, ExtendMap headers) {
        return new JsoupResponse(this, url, headers);
    }

    @Override
    public Response getLocalResponse(String url, ExtendMap headers) throws ProtocolException, IOException {
        return null;
    }

    @Override
    public void addProxyStr(List<String> proxyList) {
        synchronized (PROXY_LIST) {
            if (proxyList != null) {
                PROXY_LIST.clear();
                PROXY_LIST.addAll(proxyList);
                LOG.info("proxy size : {} ", PROXY_LIST.size());
            }
        }
    }
    public Proxy getProxy() {
        synchronized (PROXY_LIST) {
            if (PROXY_LIST.size() == 0) {
                return null;
            }
            String proxyStr = PROXY_LIST.get(RANDOM.nextInt(PROXY_LIST.size()));
            return new Proxy(Proxy.Type.HTTP,
                    new InetSocketAddress(proxyStr.split(":")[0], Integer.valueOf(proxyStr.split(":")[1])));
        }
    }

}
