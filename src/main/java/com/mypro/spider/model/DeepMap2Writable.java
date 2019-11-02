package com.mypro.spider.model;

import org.apache.hadoop.io.MapWritable;

import java.util.Map;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/1/24
 */
public interface DeepMap2Writable {

    MapWritable deepForMapToWritable(Map<String, Object> map, MapWritable mapWritable);

    Map<String, Object> deepForWritableToMap(MapWritable mapWritable, Map<String, Object> map);

}
