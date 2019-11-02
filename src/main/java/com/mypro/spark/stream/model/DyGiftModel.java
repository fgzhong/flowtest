package com.mypro.spark.stream.model;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * @author fgzhong
 * @description: 斗鱼礼物
 * @since 2019/8/23
 */
public class DyGiftModel implements Serializable{

    private String id;
    private String name;
    private double pc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPc() {
        return pc;
    }

    public void setPc(double pc) {
        this.pc = pc;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
