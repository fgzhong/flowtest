package com.mypro.spider.parse;

import com.google.common.collect.Maps;
import com.mypro.spider.data.ExtendMap;
import com.mypro.spider.utils.ExtendMapUtil;

import java.util.Map;

/**
 * @author fgzhong
 * @since 2019/1/5
 */
public class Outlink {

    private String url;
    private boolean fetchNow;
    private Map<String, Object> data;

    public Outlink(String url) {
        this.url = url;
    }

    public Outlink(String url, boolean fetchNow) {
        this.url = url;
        this.fetchNow = fetchNow;
    }

    public Outlink(String url, Map<String, Object> data) {
        this.url = url;
        this.data = data;
    }

    public Outlink(String url, boolean fetchNow, Map<String, Object> data) {
        this.url = url;
        this.fetchNow = fetchNow;
        this.data = data;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void addData(String key, Object value) {
        if (this.data == null) {
            this.data = Maps.newHashMap();
        }
        this.data.put(key, value);
    }

    public void addData(Map<String, Object> data) {
        if (this.data == null) {
            this.data = Maps.newHashMap();
        }
        this.data.putAll(data);
    }

    public void addData(ExtendMap extendMap) {
        if (this.data == null) {
            this.data = Maps.newHashMap();
        }
        this.data.putAll(ExtendMapUtil.getAllMap(extendMap));
    }

}
