package com.mypro.spider.constant;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/2/28
 */
public interface ProtocolStatus {

    int SUCCESS = 1;
    int RETRY = 2;
    int FAIL = 3;
    int ERROR = -1;
    int DEPTH_ERROR = -2;
}
