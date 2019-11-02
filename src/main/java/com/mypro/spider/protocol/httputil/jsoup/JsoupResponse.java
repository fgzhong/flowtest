package com.mypro.spider.protocol.httputil.jsoup;

import com.mypro.spider.constant.ProtocolStatus;
import com.mypro.spider.data.ExtendMap;
import com.mypro.spider.protocol.httputil.Response;
import com.mypro.spider.utils.*;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.net.Proxy;
import java.util.Map;
import java.util.UUID;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/3/30
 */
public class JsoupResponse implements Response {

    private static final Logger LOG = LoggerFactory
            .getLogger(MethodHandles.lookup().lookupClass());


    private String url;
    private byte[] content;
    private int code;

    public JsoupResponse(Jsoups jsoups, String url, ExtendMap headers) {
        this.url = url;
        Connection connection = Jsoup.connect(url).timeout(Jsoups.timeout)
                .maxBodySize(Jsoups.maxContent)
                .ignoreContentType(true);
        Map<String, String> headerMap = ExtendMapUtil.getHeaderMap(headers);
        if (headerMap != null && headerMap.size() != 0) {
            connection.headers(headerMap);
        }

        if (!headerMap.containsKey("User-Agent")) {
            connection.header("User-Agent", UserAgentUtil.getPCAgent());
        }
        switch (headers.getHttpType()) {
            case ExtendMap.HTTP_TYPE_POST : {
                connection.method(Connection.Method.POST);
                if (headers.getRequestBody() != null) {
                    connection.requestBody(headers.getRequestBody());
                }
            } break;
            default: connection.method(Connection.Method.GET);
        }
        Proxy proxy = jsoups.getProxy();
        if (proxy != null) {
            connection.proxy(proxy);
        }
        try {
            Connection.Response response =  connection.execute();
            this.code = response.statusCode();
            this.content = response.bodyAsBytes();
        } catch (HttpStatusException e) {
            this.code = e.getStatusCode();
            this.content = Jsoups.EMPTY_CONTENT;
        } catch (Exception e) {
            LOG.warn(" url : {}, error reason : {}", url, e);
            this.code = ProtocolStatus.RETRY;
            this.content = Jsoups.EMPTY_CONTENT;
        }

    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public byte[] getContent() {
        return this.content;
    }

}
