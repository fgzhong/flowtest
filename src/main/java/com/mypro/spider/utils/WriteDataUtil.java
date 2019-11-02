package com.mypro.spider.utils;

import com.mypro.spider.model.AbstractModel;
import com.mypro.spider.model.SpiderWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/2/26
 */
public class WriteDataUtil {

    private static Text key = new Text();
    private static SpiderWritable value = new SpiderWritable();
    private static Text inValue = new Text();

    public static synchronized void write(Mapper.Context context, String key, String value) throws IOException, InterruptedException {
        WriteDataUtil.key.set(key);
        WriteDataUtil.inValue.set(value);
        WriteDataUtil.value.set(WriteDataUtil.inValue);
        context.write(WriteDataUtil.key, WriteDataUtil.value);
    }

    public static synchronized void write(Mapper.Context context, String key, AbstractModel value) throws IOException, InterruptedException {
        WriteDataUtil.key.set(key);
        WriteDataUtil.value.set(value);
        context.write(WriteDataUtil.key, WriteDataUtil.value);
    }
}
