package com.mypro.spider.protocol.httputil.okhttp3;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.connection.RouteSelector;

import java.io.IOException;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/2/28
 */
public class ProxyInterceptor implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        return response;
    }
}
