package com.mypro.spider.utils;

import com.google.common.collect.Maps;
import org.apache.hadoop.io.*;

import java.util.Map;

/**
 * @author fgzhong
 * @description: Map 与 MapWritable 互转
 * @since 2019/4/8
 */
public class Map2Writable {



    public static Map<String, Object> deepForWritableToMap(MapWritable mapWritable, Map<String, Object> map) {
        if (map == null) map = Maps.newHashMap();
        for (Map.Entry<Writable, Writable> entry : mapWritable.entrySet()) {
            if (entry.getValue() instanceof Text) {
                map.put(entry.getKey().toString(), entry.getValue().toString());
            } else if (entry.getValue() instanceof IntWritable) {
                map.put(entry.getKey().toString(), ((IntWritable) entry.getValue()).get());
            } else if (entry.getValue() instanceof BytesWritable) {
                map.put(entry.getKey().toString(), ((BytesWritable) entry.getValue()).getBytes());
            } else if (entry.getValue() instanceof ByteWritable) {
                map.put(entry.getKey().toString(), ((ByteWritable) entry.getValue()).get());
            } else if (entry.getValue() instanceof MapWritable) {
                map.put(entry.getKey().toString(), deepForWritableToMap((MapWritable) entry.getValue(), Maps.newHashMap()));
            } else {
                throw new IllegalArgumentException("暂不支持该数据格式：" + entry.getValue().getClass());
            }
        }
        return map;
    }

    public static MapWritable deepForMapToWritable(Map<String, Object> map, MapWritable mapWritable) {
        if (mapWritable == null) mapWritable = new MapWritable();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof String) {
                mapWritable.put(new Text(entry.getKey()), new Text((String) entry.getValue()));
            } else if (entry.getValue() instanceof Integer) {
                mapWritable.put(new Text(entry.getKey()), new IntWritable((Integer) entry.getValue()));
            } else if (entry.getValue() instanceof Map) {
                mapWritable.put(new Text(entry.getKey()), deepForMapToWritable((Map<String, Object>) entry.getValue(), new MapWritable()));
            } else {
                throw new IllegalArgumentException("暂不支持该数据格式：" + entry.getValue().getClass());
            }
        }
        return mapWritable;
    }

}
