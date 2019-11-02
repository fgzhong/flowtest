package com.mypro.spark.util;

import com.alibaba.fastjson.JSON;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.SparkHadoopUtil;
import org.apache.tools.ant.taskdefs.LoadProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.Properties;

/**
 * @author fgzhong
 * @description: 加载配置文件, 配置文件 > 代码设置
 * @since 2019/3/27
 */
public class ConfigUtil {

    private static final Logger LOG = LoggerFactory
            .getLogger(MethodHandles.lookup().lookupClass());

    public static void sparkConfUtil(SparkConf conf, String filename) throws IOException {
        Properties properties = new Properties();
        InputStream sparkConfInput = LoadProperties.class.getClassLoader().getResourceAsStream(filename);
        if (sparkConfInput != null) {
            properties.load(sparkConfInput);
            for (Object key : properties.keySet()) {
                conf.set(key.toString(), properties.getProperty(key.toString()));
            }
        }
        InputStream hdfsXmlInput = LoadProperties.class.getClassLoader().getResourceAsStream("hdfs-site.xml");
        InputStream yarnXmlInput = LoadProperties.class.getClassLoader().getResourceAsStream("yarn-site.xml");
        InputStream coreXmlInput = LoadProperties.class.getClassLoader().getResourceAsStream("core-site.xml");

        if (hdfsXmlInput != null && yarnXmlInput != null && coreXmlInput != null) {
            Configuration hadoopConf = new Configuration();
            hadoopConf.addResource(hdfsXmlInput);
            hadoopConf.addResource(yarnXmlInput);
            hadoopConf.addResource(coreXmlInput);
            hadoopConf.iterator().forEachRemaining(f -> {
                conf.set("spark.hadoop."+f.getKey(), f.getValue());
            });
        }
    }

    public static void sparkConfUtil(SparkConf conf) throws IOException {
        sparkConfUtil(conf, "spark-defaults.conf");
    }
}
