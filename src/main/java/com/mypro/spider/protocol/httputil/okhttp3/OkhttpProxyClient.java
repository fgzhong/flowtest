package com.mypro.spider.protocol.httputil.okhttp3;

import okhttp3.OkHttpClient;

import javax.annotation.Nullable;
import java.net.Proxy;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/4/23
 */
public class OkhttpProxyClient extends OkHttpClient {

    @Nullable
    @Override
    public Proxy proxy() {
        return super.proxy();
    }
}
