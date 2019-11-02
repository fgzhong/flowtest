package com.mypro.spider.model;

import com.google.common.collect.Maps;
import com.mypro.spider.data.ExtendMap;
import com.mypro.spider.parse.Outlink;
import com.mypro.spider.utils.ExtendMapUtil;
import com.mypro.spider.utils.Map2Writable;
import com.mypro.spider.utils.UrlLengthUtil;
import org.apache.hadoop.io.*;
import org.elasticsearch.hadoop.mr.LinkedMapWritable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

/**
 * @author fgzhong
 * @description: 基础字段定义
 * @since 2019/1/12
 */
public abstract class AbstractModel implements Writable, ESWritable, DeepMap2Writable, ModelStatus<AbstractModel>, Cloneable {

    public static final byte STATUS_UNFETCH = 0x00;
    public static final byte STATUS_SUCCESS = 0x01;
    public static final byte STATUS_RETRY = 0x02;
    public static final byte STATUS_FAIL = 0x03;
    public static final byte STATUS_DATAERROR = 0x04;

    public static final byte INITIAL_VALUE = 0x00;
    public static final byte ADD_ONE = 0x01;


    private String url;
    /** 作id为文档id，由 URL base64 加密 获得 */
    private String uid; 
    /**  状态  */
    private byte status; 
    /**  优先级  */
    private byte level;  
    /**  深度，用于深度优先还是广度优先  */
    private byte depth; 
    private byte retries;
    private String parseClass;
    private Map<String, Object> map;

    /** 不用设置 */
    private final static String MAP_WRITABLE_STR = "mapWritableStr";
    /** hdfs 用于 map to mapWritable */
    private MapWritable mapWritable;


    @Override
    public void write(DataOutput dataOutput) throws IOException {
        Text.writeString(dataOutput, url);
        Text.writeString(dataOutput, uid);
        dataOutput.writeByte(status);
        dataOutput.writeByte(level);
        dataOutput.writeByte(depth);
        dataOutput.writeByte(retries);
        Text.writeString(dataOutput, parseClass);
        if (this.map != null && this.map.size() > 0) {
            dataOutput.writeBoolean(true);
            this.mapWritable = deepForMapToWritable(this.map, new MapWritable());
            this.mapWritable.write(dataOutput);
        } else {
            dataOutput.writeBoolean(false);
        }
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.setUrl(Text.readString(dataInput));
        this.uid = Text.readString(dataInput);
        this.status = dataInput.readByte();
        this.level = dataInput.readByte();
        this.depth = dataInput.readByte();
        this.retries = dataInput.readByte();
        this.parseClass = Text.readString(dataInput);
        if (dataInput.readBoolean()) {
            this.mapWritable = new MapWritable();
            this.mapWritable.readFields(dataInput);
            this.map = deepForWritableToMap(this.mapWritable, Maps.newHashMap());
            this.mapWritable = null;
        }
    }


    @Override
    public LinkedMapWritable writeMap() {
        LinkedMapWritable esMapData = new LinkedMapWritable();
        esMapData.put(new Text("url"), new Text(url));
        esMapData.put(new Text("uid"), new Text(uid));
        esMapData.put(new Text("status"), new ByteWritable(status));
        esMapData.put(new Text("level"), new ByteWritable(level));
        esMapData.put(new Text("depth"), new ByteWritable(depth));
        esMapData.put(new Text("retries"), new ByteWritable(retries));
        esMapData.put(new Text("parseClass"), new Text(parseClass));
        if(this.map != null && this.map.size() >0) {
            this.mapWritable = deepForMapToWritable(this.map, new MapWritable());
            esMapData.put(new Text(MAP_WRITABLE_STR), this.mapWritable);
        }
        return esMapData;
    }

    @Override
    public void readMap(LinkedMapWritable mapdata) {
        this.url = mapdata.get(new Text("url")).toString();
        this.uid = mapdata.get(new Text("uid")).toString();
        this.status = ((ByteWritable) mapdata.get(new Text("status"))).get();
        this.level = ((ByteWritable) mapdata.get(new Text("level"))).get();
        this.depth = ((ByteWritable) mapdata.get(new Text("depth"))).get();
        this.retries = ((ByteWritable) mapdata.get(new Text("retries"))).get();
        this.parseClass = mapdata.get(new Text("parseClass")).toString();
        if (mapdata.containsKey(new Text(MAP_WRITABLE_STR)) && ((MapWritable) mapdata.get(new Text(MAP_WRITABLE_STR))).size() > 0) {
            this.map = this.deepForWritableToMap((MapWritable) mapdata.get(new Text(MAP_WRITABLE_STR)), Maps.newHashMap());
        }
    }

    @Override
    public MapWritable deepForMapToWritable(Map<String, Object> map, MapWritable mapWritable) {
        return Map2Writable.deepForMapToWritable(map, mapWritable);
    }

    @Override
    public Map<String, Object> deepForWritableToMap(MapWritable mapWritable, Map<String, Object> map) {
        return Map2Writable.deepForWritableToMap(mapWritable, map);
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        this.uid = UrlLengthUtil.shortenCodeUrl(url, 16);
    }

    public String getUid() {
        return uid;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public byte getDepth() {
        return depth;
    }

    public void setDepth(byte depth) {
        this.depth = depth;
    }

    public byte getRetries() {
        return retries;
    }

    public void setRetries(byte retries) {
        this.retries = retries;
    }

    public String getParseClass() {
        return parseClass;
    }

    public String getHttpClass() throws NullPointerException {
        if (map == null || !map.containsKey("httpMethodClassName")) { return null; }
        return (String) map.get("httpMethodClassName");
    }

    public void setMap(ExtendMap map) {
        if (map == null) {return;}
        if (this.map == null) { this.map = Maps.newHashMap();}
        this.map.putAll(ExtendMapUtil.getAllMap(map));
    }


    private void clearMap() {
        this.map = null;
        this.mapWritable = null;
    }

    public Map<String, Object> getMap() {
        return this.map;
    }

    @Override
    public String toString() {
        StringBuilder repr = new StringBuilder("url: ");
        repr.append(url);
        repr.append(" uid: ");
        repr.append(uid);
        repr.append(" status: ");
        repr.append(status);
        repr.append(" level: ");
        repr.append(level);
        repr.append(" depth: ");
        repr.append(depth);
        repr.append(" retries: ");
        repr.append(retries);
        if (map != null && !map.isEmpty()) {
            repr.append(" map: ");
            repr.append(map.toString());
        }
        return repr.toString();
    }

    /** clone 减少new；同时保留用于继承的基本参数，model中包含非必须参数时，必须重写此方法清除相应数据 */
    @Override
    public AbstractModel clone() throws CloneNotSupportedException {
        AbstractModel abstractSuperModel = (AbstractModel) super.clone();
        abstractSuperModel.clearMap();
        return abstractSuperModel;
    }

    @Override
    public void successModel() {
        this.status = STATUS_SUCCESS;
    }

    @Override
    public void retryModel() {
        this.retries = (byte) (this.retries + ADD_ONE);
    }

    @Override
    public void failModel() {
        this.status = STATUS_FAIL;
    }

    @Override
    public void dataErrorModel() {
        this.status = STATUS_DATAERROR;
        this.retries = (byte) (this.retries + ADD_ONE + ADD_ONE);
    }

    @Override
    public AbstractModel nextModel(Outlink o) throws CloneNotSupportedException {
        AbstractModel model = this.clone();
        model.setUrl(o.getUrl());
        model.status = STATUS_UNFETCH;
        model.retries = INITIAL_VALUE;
        model.depth = (byte) (this.depth + ADD_ONE);
        if (this.map != null && this.map.containsKey("httpMethodClassName")) {
            model.map = Maps.newHashMap();
            model.map.put("httpMethodClassName", this.map.get("httpMethodClassName"));
        }
        if (o.getData() != null && o.getData().size() != 0) {
            if (model.map == null) {
                model.map = Maps.newHashMap();
            }
            model.map.putAll(o.getData());
        }
        return model;
    }
}
