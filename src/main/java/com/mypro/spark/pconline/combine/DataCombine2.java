package com.mypro.spark.pconline.combine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.mypro.spark.util.ConfigUtil;
import com.mypro.spider.config.ESConfig;
import com.mypro.spider.example.example2pro.parse.WeiBoParse;
import com.mypro.spider.putformat.SpiderInputFormat;
import com.mypro.spider.putformat.SpiderOutputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.elasticsearch.hadoop.mr.EsOutputFormat;
import scala.Tuple2;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fgzhong
 * @description: model str combine
 * @error bug : com.alibaba.fastjson.JSONObject 中 JSONArray 包含 JSONObject，反序列化错误，变成 true/false
 * @since 2019/5/15
 */
public class DataCombine2 extends Configured implements Tool {

    private final static String SPARK_HADOOP = "spark.hadoop.";
    private final static Gson GSON = new Gson();

    @Override
    public int run(String[] args) throws Exception {
        SparkConf conf = new SparkConf(true)
//                .setMaster("local[2]")
                .setAppName("DataCombine")
                .set(SPARK_HADOOP + "mapreduce.input.fileinputformat.split.maxsize", "134217728")
                .set(SPARK_HADOOP + "mapreduce.input.fileinputformat.split.minsize", "134217728")
                .set(SPARK_HADOOP +ESConfig.NODES, "es1.ali.szol.bds.com:9200,es2.ali.szol.bds.com:9200")
                .set(ESConfig.NODES, "es1.ali.szol.bds.com:9200,es2.ali.szol.bds.com:9200")
                .set(SPARK_HADOOP + ESConfig.RESOURCE_WRITE, "data-weibo/_doc")
                .set(ESConfig.RESOURCE_WRITE, "data-weibo/_doc")
//                .set(SPARK_HADOOP + ESConfig.MAPPING_ID, "key")
//                .set(ESConfig.MAPPING_ID, "key")
                .set(SPARK_HADOOP + "es.index.auto.create", "yes")
                .set("es.index.auto.create", "yes")
                .set(SPARK_HADOOP + ESConfig.INPUT_JSON, "true")
                .set(ESConfig.INPUT_JSON, "true")
                .set(SPARK_HADOOP + "es.ser.writer.value.class", "org.elasticsearch.hadoop.serialization.builder.JdkValueWriter")
                .set("es.ser.writer.value.class", "org.elasticsearch.hadoop.serialization.builder.JdkValueWriter")
                ;
//        conf.registerKryoClasses(new Class[]{JSONObject.class, Text.class, JSONArray.class});

        ConfigUtil.sparkConfUtil(conf);
        JavaSparkContext jsc = new JavaSparkContext(conf);


        JavaPairRDD<Text, Text> modelRDD = jsc.newAPIHadoopFile(
                "/user/maplecloudy/mypro/data-weibo/parse/",
                SpiderInputFormat.class
                , Text.class, Text.class,
                jsc.hadoopConfiguration());

        JavaPairRDD<String, Map<String, Object>> dataJsonPair = modelRDD.mapToPair(f -> {
            Map<String, Object> data = GSON.fromJson(f._2.toString(), Map.class);

            String key = (String) data.get("key");
            if (key != null && WeiBoParse.NEW_COMMENT_REGEX.matches(key)) {
                key = String.format(WeiBoParse.CONTENT, key.split("id=")[1].split("&")[0]);
                data.put("key", key);
            }
            return new Tuple2<>(key, data);
        });


        JavaPairRDD<String, Map<String,Object>> combineDataJsonPair = dataJsonPair.reduceByKey((f1,f2) -> {
            return f1;
        });

        JavaPairRDD<String, String> dataToSave = combineDataJsonPair.coalesce(10)
                .mapToPair(f -> new Tuple2<>(f._1,JSON.toJSONString(f._2))).cache();

        dataToSave.mapToPair(f -> new Tuple2<>(f._1, JSONObject.parseObject(f._2))).saveAsNewAPIHadoopFile(
                "/user/maplecloudy/mypro/data-weibo/combine/",
                String.class,
                JSONObject.class,
                EsOutputFormat.class);


        return 0;
    }

    public static void main(String[] args) throws Exception{
        int res = ToolRunner.run(new Configuration(), new DataCombine2(), args);
        System.exit(res);
    }
}
