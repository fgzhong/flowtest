package com.mypro.spider.config;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/2/26
 */
public interface SpiderConfig {

    String reg = "spider.";

    String fetchThreadNum = reg + "fetch.thread.num";
    /** parse 30ms, http 600ms, http等待时间可处理20个parse */
    int default_fetchThreadNum = 20;
    String fetchThreadWait = reg + "fetch.thread.wait";
    int default_fetchThreadWait = 500;
    String fetchThreadTimeout = reg + "fetch.thread.timeout";
    int default_fetchThreadTimeout = 30;
    String fetchTimer = reg + "fetch.timer";
    boolean default_fetchTimer = true;
    String fetchHttpClasses = reg + "fetch.http.classes";
    String default_fetchHttpClass = "com.mypro.spider.protocol.httputil.okhttp3.OkHttp";

    int default_reduce_num = 1;

}
