package com.mypro.spider.parse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/1/28
 */
public class Content {

    private String url;
    private String parseClass;
    private int code;
    private byte[] html;
    private Map data;

    private Content(){}

    public static Content newContent(String url, byte[] html, Map<String, Object> map) {
        Content content = new Content();
        content.code = 200;
        content.url = url;
        content.html = html;
        content.data = map;
        return content;
    }

    public static Content newContent(String url, int code) {
        Content content = new Content();
        content.url = url;
        content.code = code;
        return content;
    }

    public byte[] getHtml() {return html;}

    public String getUrl() {
        return url;
    }


    public int getCode() {
        return code;
    }

    public Object getValue(String key) {
        if (data == null) {
            throw new NullPointerException("map 不存在");
        }
        if (!data.containsKey(key)) {
            throw new NullPointerException("key："+ key +"  不存在");
        }
        return data.get(key);
     }

    public String getParseClass() {
        return parseClass;
    }

    public Map getData() {
        return data;
    }

    public void setParseClass(String parseClass) {
        this.parseClass = parseClass;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setHtml(byte[] html) {
        this.html = html;
    }

    public void setData(Map data) {
        this.data = data;
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }
}
