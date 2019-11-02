package com.mypro.spider.model;

import org.apache.hadoop.io.Writable;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/2/20
 */
public class SpiderWritable extends GenericWritableConfigurable{

    private static Class<? extends Writable>[] CLASSES = null;

    static {
        CLASSES = (Class<? extends Writable>[]) new Class<?>[] {
                org.apache.hadoop.io.Text.class,
                org.apache.hadoop.io.NullWritable.class,
                org.apache.hadoop.io.BooleanWritable.class,
                org.apache.hadoop.io.LongWritable.class,
                org.apache.hadoop.io.ByteWritable.class,
                org.apache.hadoop.io.BytesWritable.class,
                org.apache.hadoop.io.FloatWritable.class,
                org.apache.hadoop.io.IntWritable.class,
                org.apache.hadoop.io.MapWritable.class,
                org.apache.hadoop.io.MD5Hash.class,
                com.mypro.spider.model.FetchOutModel.class,
                com.mypro.spider.model.InjectInModel.class,
                org.elasticsearch.hadoop.mr.LinkedMapWritable.class
        };
    }

    public SpiderWritable() {
    }

    public SpiderWritable(Writable instance) {
        set(instance);
    }



    @Override
    protected Class<? extends Writable>[] getTypes() {
        return CLASSES;
    }
}
