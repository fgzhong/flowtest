package com.mypro.spark.stream.model;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.ComparatorUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/8/31
 */
public class Rank implements Serializable, Comparable<Rank> {

    private String web;
    private String achorId;
    private BigDecimal pc;
    private int  online;
    private int  totalonline;
    private long time;

    public void setWeb(String web) {
        this.web = web;
    }

    public void setAchorId(String achorId) {
        this.achorId = achorId;
    }

    public void setPc(BigDecimal pc) {
        this.pc = pc;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getWeb() {
        return web;
    }

    public String getAchorId() {
        return achorId;
    }

    public BigDecimal getPc() {
        return pc;
    }

    public long getTime() {
        return time;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public int getTotalonline() {
        return totalonline;
    }

    public void setTotalonline(int totalonline) {
        this.totalonline = totalonline;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    @Override
    public int compareTo(Rank o) {
        return Integer.compare(this.getTotalonline(), o.getTotalonline());
    }
}
