package com.mypro.spider.protocol;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/1/28
 */
public class ProtocolException extends Exception{


    public ProtocolException() {
        super();
    }

    public ProtocolException(String message) {
        super(message);
    }

    public ProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProtocolException(Throwable cause) {
        super(cause);
    }
}
