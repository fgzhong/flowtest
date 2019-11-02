package com.mypro.spark.stream.model;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * @author fgzhong
 * @description: 数据结构
 * @since 2019/7/18
 */
public class DataBean implements Serializable{

    private int version;
    private String id;  // web + userId + time
    private String uid;  // web + userId
    private String web;
    private String achorId;
    private String userId;
    private String userName;
    private String type;
    private String msg;
    private String gfName;
    private String gfId;
    private Double gfSigPrice;
    private Integer gfCount;
    private BigDecimal gfPrice;
    private Timestamp timestamp;
    private Long time;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getGfName() {
        return gfName;
    }

    public void setGfName(String gfName) {
        this.gfName = gfName;
    }

    public String getGfId() {
        return gfId;
    }

    public void setGfId(String gfId) {
        this.gfId = gfId;
    }

    public Double getGfSigPrice() {
        return gfSigPrice;
    }

    public void setGfSigPrice(double gfSigPrice) {
        this.gfSigPrice = gfSigPrice;
    }

    public Integer getGfCount() {
        return gfCount;
    }

    public void setGfCount(int gfcount) {
        this.gfCount = gfcount;
    }

    public BigDecimal getGfPrice() {
        return gfPrice;
    }

    public void setGfPrice(BigDecimal gfPrice) {
        this.gfPrice = gfPrice;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
