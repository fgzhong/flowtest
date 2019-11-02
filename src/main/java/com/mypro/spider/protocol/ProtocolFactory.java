package com.mypro.spider.protocol;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mypro.spider.config.SpiderConfig;
import com.mypro.spider.protocol.httputil.HttpBase;
import com.mypro.spider.protocol.httputil.jsoup.Jsoups;
import com.mypro.spider.protocol.httputil.okhttp3.OkHttp;
import com.mypro.spider.utils.Records;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/1/29
 */
public class ProtocolFactory {

    private static final Logger LOG = LoggerFactory
            .getLogger(MethodHandles.lookup().lookupClass());

    private static final Map<String, HttpBase> CONTAINER = Maps.newHashMap();

    static {
        try {
            CONTAINER.put(Jsoups.class.getName(), Records.newInstance((Class<HttpBase>) Class.forName(Jsoups.class.getName())));
            CONTAINER.put(OkHttp.class.getName(), Records.newInstance((Class<HttpBase>) Class.forName(OkHttp.class.getName())));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Protocol getProtocol(String httpClassName){
        if (StringUtils.isBlank(httpClassName)) { httpClassName = SpiderConfig.default_fetchHttpClass; }
        HttpBase httpBase = null;
        if (CONTAINER.containsKey(httpClassName)) {
            httpBase = CONTAINER.get(httpClassName);
        } else {
            LOG.warn(" http protocol not found for class name : {}", httpClassName);
            synchronized (CONTAINER) {
                if (CONTAINER.containsKey(httpClassName)) {
                    httpBase = CONTAINER.get(httpClassName);
                } else {
                    try {
                        httpBase = Records.newInstance((Class<HttpBase>) Class.forName(httpClassName));
                        ProtocolFactory.addHttpClass(httpClassName, httpBase);
                    } catch (Exception e) {
                        throw new RuntimeException("http class："+httpClassName+" not found");
                    }
                }
            }
        }
        return httpBase;
    }

    public static void addHttpClass(String httpClassName, HttpBase httpBase) {
        if (!CONTAINER.containsKey(httpClassName)) {
            CONTAINER.put(httpClassName, httpBase);
        }
    }

    public static List<HttpBase> getHttpClasses() {
        List<HttpBase> httpBases = Lists.newArrayList();
        for (Map.Entry<String, HttpBase> entry: CONTAINER.entrySet()) {
            httpBases.add(entry.getValue());
        }
        return httpBases;
     }

     public static boolean hasClass(String httpClassName) {
        return CONTAINER.containsKey(httpClassName);
     }

}
