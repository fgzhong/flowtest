package com.mypro.spider.model;

import org.elasticsearch.hadoop.mr.LinkedMapWritable;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/1/24
 */
public interface ESWritable {

    LinkedMapWritable writeMap();

    void readMap(LinkedMapWritable mapdata);


}
