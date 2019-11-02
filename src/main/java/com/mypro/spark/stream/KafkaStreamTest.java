package com.mypro.spark.stream;

import com.alibaba.fastjson.JSONObject;
import com.mypro.kafka.KafkaConstant;
import com.mypro.spark.stream.model.DataBean;
import com.mypro.spider.config.ESConfig;
import org.apache.spark.SparkConf;
import org.apache.spark.TaskContext;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.api.java.function.MapPartitionsFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.*;
import org.apache.spark.sql.streaming.DataStreamWriter;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.Trigger;
import org.jsoup.Jsoup;
import scala.reflect.ClassManifestFactory;

import java.math.BigDecimal;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.concat_ws;

/**
 * @author fgzhong
 * @description: 消费kafka的实时数据
 * @since 2019/7/16
 */
public class KafkaStreamTest {



    private final static String DY_GF_URL = "http://webconf.douyucdn.cn//resource/common/prop_gift_list/prop_gift_config.json";

    public static void main(String[] args) throws Exception{
        SparkConf conf = new SparkConf()
                .setMaster("local")
                .setAppName("test")
                .set(ESConfig.NODES, KafkaConstant.ES_HOST)
//                .set(ESConfig.MAPPING_ID, "key")
                .set("es.index.auto.create", "yes")
                .set("spark.sql.shuffle.partitions", "1")
                .set("spark.default.parallelism", "1")
                .set("es.nodes.wan.only", "true")
                ;
        SparkSession spark = SparkSession.builder()
                .config(conf).getOrCreate();

        /*
          1/ readStream() -> DataStreamReader -> 根据format输入的数据源名字（名字的定义是在实现DataSourceRegister接口然后重写其方法shortName中定义的）
             -> 调用option设置参数
             startingOffsets、endingOffsets、failOnDataLoss、kafkaConsumer.pollTimeoutMs、fetchOffset.numRetries
             fetchOffset.retryIntervalMs、maxOffsetsPerTrigger

        */

        Dataset<Row> dataset = spark
            .readStream()
            .format("kafka")
            .option("startingOffsets", "earliest")
            .option("kafka.bootstrap.servers", KafkaConstant.SERVER_ADDR)
            .option("subscribe", KafkaConstant.TOPIC)
            .load();

        Encoder<DataBean> dataBean = Encoders.bean(DataBean.class);
        String gfStr = Jsoup.connect(DY_GF_URL).ignoreContentType(true).get().toString();
        JSONObject dygf1 = JSONObject.parseObject(gfStr.substring(gfStr.indexOf("{"), gfStr.lastIndexOf("}")+1)).getJSONObject("data");
        Broadcast<JSONObject> dygf = spark.sparkContext().broadcast(dygf1, ClassManifestFactory.classType(JSONObject.class));

        dataset.printSchema();

        Dataset<DataBean> bean2data = dataset
                .selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)", "timestamp")
                .map((MapFunction<Row, DataBean>) row ->{
                    DataBean bean = new DataBean();
                    bean.setTimestamp(row.getTimestamp(2));
                    JSONObject js = JSONObject.parseObject(row.getString(1));
                    String uid = row.getString(0);
                    String web = uid.split("_")[0];
                    String achorId = uid.split("_")[1];
                    bean.setWeb(web);
                    bean.setAchorId(achorId);
                    switch (web) {
                        case WebName.dy: dy(js, bean, dygf.getValue()); break;
                        default:;
                    }
                    return bean;
                }, dataBean);


        Dataset<DataBean> gf2bean = bean2data.filter((FilterFunction<DataBean>) value -> DataType.gift.equals(value.getType()));

        gf2bean.mapPartitions((MapPartitionsFunction<DataBean, DataBean>) f -> {
            TaskContext.get().partitionId();
            return f;
        },dataBean);

//        bean2data.writeStream().format("console").trigger(Trigger.ProcessingTime(10000)).start();

//        gf2bean.writeStream().format("console").trigger(Trigger.ProcessingTime(10000)).start();

//        gf2bean.dropDuplicates("id");   按照唯一标识符对所有数据去重
//        gf2bean.withWatermark("eventYime", "10 seconds")
//                .dropDuplicates("id","eventYime");    //  以唯一标识符删除10s内的重复数据

        Dataset<Row> gfWindow = gf2bean
                .withWatermark("timestamp", "10 seconds")
                .groupBy(
                    functions.window(gf2bean.col("timestamp"), "10 seconds", "5 seconds"),
                    gf2bean.col("web"), gf2bean.col("achorId")
                )
                .sum("gfPrice");

        gfWindow.printSchema();

//        StructType gfSumShema = new StructType()
//                .add("web", "string")
//                .add("achorId", "string")
//                .add("sum", "double");
        Dataset<Row> gfWindowForAppend = gfWindow.select(col("web"),
                col("achorId"),
                col("sum(gfPrice)").as("income"),
                col("window").getField("start").as("time")
        )
                .withColumn("timestamp", col("time").cast("String"))
                .withColumn("id", concat_ws("_",col("web"),col("achorId"),col("time").cast("long")));

        gfWindowForAppend.printSchema();

        DataStreamWriter<Row> query = gfWindowForAppend.writeStream();

        String a = "5";
        switch (a) {
            case "1":{
                query
                    .outputMode(OutputMode.Complete())
                    .format("csv")        // can be "orc", "json", "csv", etc.
                    .option("path", "data/ls")
                    .queryName("csv query");
            } break;
            case "2" : {
                query
                    .outputMode(OutputMode.Complete())
                    .format("console")        // can be "orc", "json", "csv", etc.
                    .queryName("console query")
                    .option("truncate", "false");
            } break;
            case "3" : {
                query
                        .outputMode(OutputMode.Append())
                        .format("es")  // org.elasticsearch.spark.sql
                        .queryName("es query");
//                .start("structured.es.example.{name}.{date|yyyy-MM}") // 写出索引配置，ES 7+ 无需配置 type
            } break;
            case "4" : {
                query
                    .outputMode(OutputMode.Complete())
                    .format("kafka")
                    .option("topic","t1" )
                    .queryName("kfk query");
            } break;
            case "5" : {
                query
                        .outputMode(OutputMode.Complete())
                        .foreach(new EsSink(conf, true))
                        .queryName("foreach query");
            } break;
            default:;
        }

        query.option("checkpointLocation", "data/fe")
                .trigger(Trigger.ProcessingTime(10000))
                .start();

        spark.streams().awaitAnyTermination();
    }

    private static DataBean dy(JSONObject js, DataBean bean, JSONObject gf){
        if (bean == null) {
            bean = new DataBean();
        }
        bean.setUserId(js.getString("uid"));
        bean.setUserName(js.getString("nn"));
        switch (js.containsKey("type")?js.getString("type"):"") {
            case "chatmsg" : {
                bean.setType(DataType.msg);
                bean.setMsg(js.getString("txt"));
            } break;
            case "dgb": {
                bean.setType(DataType.gift);
                bean.setGfId(js.getString("gfid"));
                bean.setGfCount(js.containsKey("gfcnt") ? Integer.valueOf(js.getString("gfcnt")) :1);
                if (gf.containsKey(bean.getGfId())) {
                    bean.setGfSigPrice(gf.getJSONObject(bean.getGfId()).getDouble("pc")/100);
                    double income = bean.getGfSigPrice()*bean.getGfCount();
                    BigDecimal decimal = new BigDecimal(income).setScale(1, BigDecimal.ROUND_DOWN);
                    bean.setGfPrice(decimal);
                } else {
                    bean.setType(DataType.gift_unkown);
                }
            } break;
            default: {
                bean.setType(DataType.undefine);
                bean.setMsg(js.toString());
            }
        }
        return bean;
    }

}
