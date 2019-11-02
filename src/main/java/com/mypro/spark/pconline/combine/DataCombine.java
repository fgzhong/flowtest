package com.mypro.spark.pconline.combine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mypro.spark.util.ConfigUtil;
import com.mypro.spider.config.ESConfig;
import com.mypro.spider.example.example2pro.parse.WeiBoParse;
import com.mypro.spider.putformat.SpiderInputFormat;
import com.mypro.spider.putformat.SpiderOutputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.serializer.KryoSerializer;
import org.apache.spark.util.LongAccumulator;
import org.elasticsearch.hadoop.EsHadoopIllegalArgumentException;
import org.elasticsearch.hadoop.mr.EsOutputFormat;
import org.elasticsearch.hadoop.rest.Resource;
import org.elasticsearch.hadoop.serialization.field.MapWritableFieldExtractor;
import org.elasticsearch.spark.rdd.api.java.JavaEsSpark;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Map;

/**
 * @author fgzhong
 * @description: model str combine
 * @error bug : com.alibaba.fastjson.JSONObject 中 JSONArray 为空时，反序列化错误，变成 true/false
 * @since 2019/5/15
 */
public class DataCombine extends Configured implements Tool {

    private final static String SPARK_HADOOP = "spark.hadoop.";

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

        JavaPairRDD<String, JSONObject> dataJsonPair = modelRDD.mapToPair(f -> {
            JSONObject data = JSONObject.parseObject(f._2().toString());
            data.put("reviews", JSON.toJSONString(data.getJSONArray("reviews")));
            String key = data.getString("key");
            if (key.matches(WeiBoParse.NEW_COMMENT_REGEX)) {
                key = String.format(WeiBoParse.CONTENT, key.split("id=")[1].split("&")[0]);
                data.put("key", key);
            }
            return new Tuple2<>(key, data);
        });


        /**
         * com.alibaba.fastjson.JSONObject shuffle后反序列化，会把是List（当中存放集合Map）的value置为true/false
         */
        JavaPairRDD<String, JSONObject> combineDataJsonPair = dataJsonPair.reduceByKey((f1,f2) -> {
            for (Map.Entry<String, Object> key2value: f2.entrySet()) {
                String key_2 = key2value.getKey();
                Object value_2 = key2value.getValue();
                if (f1.containsKey(key_2) && f1.get(key_2) != null) {
                    if (value_2 instanceof String) {
                        if (((String) value_2).startsWith("[") && ((String) value_2).endsWith("]")) {
                            JSONArray value_1 = JSONArray.parseArray(f1.getString(key_2));
                            JSONArray value_arr_2 = JSONArray.parseArray((String) value_2);
                            value_1.addAll(value_arr_2);
                            f1.put(key_2, value_1);
                        } else {
                            if (!f1.getString(key_2).contains((String) value_2)) {
                                f1.put(key_2, f1.getString(key_2) + "," + value_2);
                            }
                        }
                    } else if (value_2 instanceof JSONArray) {
                        f1.put(key_2,f1.getJSONArray(key_2).addAll((JSONArray) value_2));
                    } else if (value_2 instanceof JSONObject) {
                        f1.getJSONObject(key_2).putAll((JSONObject) value_2);
                    } else {
                        f1.put(key_2, value_2);
                    }
                } else {
                    f1.put(key_2, value_2);
                }
            }
            return f1;
        });

        JavaPairRDD<String, String> dataToSave = combineDataJsonPair.coalesce(10)
                .mapToPair(f -> new Tuple2<>(f._1,JSON.toJSONString(f._2))).cache();

        dataToSave.mapToPair(f -> new Tuple2<>(f._1, JSONObject.parseObject(f._2))).saveAsNewAPIHadoopFile(
                "/user/maplecloudy/mypro/data-weibo/combine/",
                String.class,
                JSONObject.class,
                EsOutputFormat.class);

        dataToSave.mapToPair(f -> new Tuple2<>(new Text(f._1),new Text(f._2)))
                .saveAsNewAPIHadoopFile(
                        "/user/maplecloudy/mypro/data-weibo/combine/",
                        Text.class,
                        Text.class,
                        SpiderOutputFormat.class);

        dataToSave.saveAsTextFile("/user/maplecloudy/mypro/data-weibo/combine-text/");
        return 0;
    }

    public static void main(String[] args) throws Exception{
        int res = ToolRunner.run(new Configuration(), new DataCombine(), args);
        System.exit(res);
    }
}
