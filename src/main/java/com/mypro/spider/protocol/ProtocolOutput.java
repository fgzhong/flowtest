package com.mypro.spider.protocol;

import com.mypro.spider.parse.Content;

import java.text.ParseException;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/1/29
 */
public class ProtocolOutput {

    private Content content;
    private int status;

    public ProtocolOutput(Content content, int status) {
        this.content = content;
        this.status = status;
    }

    public Content getContent() {
        return content;
    }


    public int getStatus() {
        return status;
    }

}
