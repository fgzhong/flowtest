package com.mypro.spider.protocol.httputil.okhttp3;

import com.mypro.spider.constant.ProtocolStatus;
import com.mypro.spider.data.ExtendMap;
import com.mypro.spider.protocol.httputil.HttpBase;
import com.mypro.spider.protocol.httputil.Response;
import com.mypro.spider.utils.ExtendMapUtil;
import com.mypro.spider.utils.UserAgentUtil;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Map;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/1/29
 */
public class OkHttpResponse implements Response{

    private static final Logger LOG = LoggerFactory
            .getLogger(MethodHandles.lookup().lookupClass());

    private String url;
    private byte[] content;
    private int code;

    OkHttpResponse(OkHttp okHttp, String url, ExtendMap headers) {
        this.url = url;
        Request.Builder rb = new Request.Builder().url(url);
        Map<String, String> headerMap = ExtendMapUtil.getHeaderMap(headers);

        if (headerMap != null && headerMap.size() != 0) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                rb.addHeader(entry.getKey(), entry.getValue());
            }
            if (!headerMap.containsKey("User-Agent")) {
                rb.addHeader("User-Agent", UserAgentUtil.getPCAgent());
            }
        }

        switch (headers.getHttpType()) {
            case ExtendMap.HTTP_TYPE_POST : {
                if (headers.getRequestBody() != null) {
                    rb.post(RequestBody.create(MediaType.parse("application/json"), headers.getRequestBody()));
                } else { rb.post(null); }
            } break;
            default: rb.get();
        }

        Request request = rb.build();
        try {
            okhttp3.Response response = okHttp.getClient().newCall(request).execute();
            this.code = response.code();
            this.content = response.body().bytes();
            response.close();
        } catch (IOException e) {
            LOG.warn(" url : {}, error reason : {}", url, e);
            this.code = ProtocolStatus.ERROR;
            this.content = OkHttp.EMPTY_CONTENT;
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
