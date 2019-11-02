package com.mypro.spark.stream.model;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * @author fgzhong
 * @description: 礼物指标model
 * @since 2019/8/23
 */
public class GiftDataModel implements Serializable{

    private String uuid;  // id + partitionid
    private String id;  //  web + achorId + time
    private String web;
    private String achorId;
    private double pc;
    private String time;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getAchorId() {
        return achorId;
    }

    public void setAchorId(String achorId) {
        this.achorId = achorId;
    }

    public double getPc() {
        return pc;
    }

    public void setPc(double pc) {
        this.pc = pc;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
