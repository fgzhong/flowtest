package com.mypro.spark.stream;

import com.alibaba.fastjson.JSONObject;
import com.mypro.kafka.KafkaConstant;
import com.mypro.spark.stream.model.DataBean;
import com.mypro.spider.config.ESConfig;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.*;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.*;
import org.apache.spark.sql.streaming.*;
import org.jsoup.Jsoup;
import scala.reflect.ClassManifestFactory;


import java.io.File;
import java.math.BigDecimal;
import java.util.Iterator;

import static org.apache.spark.sql.functions.*;
import static org.apache.spark.sql.streaming.Trigger.ProcessingTime;

/**
 * @author fgzhong
 * @description: 消费kafka的实时数据
 * @since 2019/7/16
 */
public class KafkaStream {

    private final static String DY_GF_URL = "http://webconf.douyucdn.cn//resource/common/prop_gift_list/prop_gift_config.json";
    private SparkConf conf;

    public static void main(String[] args) throws Exception{
        KafkaStream stream = new KafkaStream();
        SparkConf conf = new SparkConf()
                .setMaster("local")
                .setAppName("test")
                .set(ESConfig.NODES, KafkaConstant.ES_HOST)
                .set("es.index.auto.create", "yes")
                .set("spark.sql.shuffle.partitions", "1")
                .set("spark.default.parallelism", "1")
                .set("es.nodes.wan.only", "true")
//                .set("spark.driver.extraJavaOptions", "-Duser.timezone=UTC")
//                .set("spark.executor.extraJavaOptions", "-Duser.timezone=UTC")
                .set("spark.sql.session.timeZone","Asia/Shanghai")
                .set("spark.sql.streaming.minBatchesToRetain", "100")
                ;
        stream.conf = conf;
        SparkSession spark = SparkSession.builder()
                .config(conf).getOrCreate();
        Dataset<Row> dataset = spark
            .readStream()
            .format("kafka")
            .option("startingOffsets", "earliest")
            .option("kafka.bootstrap.servers", KafkaConstant.SERVER_ADDR)
            .option("subscribe", KafkaConstant.TOPIC)
            .load();

        File file = new File("data/memory");
        if(file.exists()) {
            file.delete();
        }
        Encoder<DataBean> dataBean = Encoders.bean(DataBean.class);
        String gfStr = Jsoup.connect(DY_GF_URL).ignoreContentType(true).get().toString();
        JSONObject dygf1 = JSONObject.parseObject(gfStr.substring(gfStr.indexOf("{"), gfStr.lastIndexOf("}")+1)).getJSONObject("data");
        Broadcast<JSONObject> dygf = spark.sparkContext().broadcast(dygf1, ClassManifestFactory.classType(JSONObject.class));

        dataset.printSchema();

//        spark.udf()  自定义函数
//        spark.sql("select * from data").printSchema();
//        while (query.isActive()) {
//            TimeUnit.SECONDS.sleep(5);
//            System.out.println("    -------------------    "+spark.sql("select * from data").count());
//        }
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
                }, dataBean)
                ;

        /*   -------------   msg five count  --------------      */
//        Dataset<DataBean> msgDataset = bean2data.filter((FilterFunction<DataBean>) value -> DataType.msg.equals(value.getType()));
        Dataset<DataBean> msgDataset = bean2data.filter((FilterFunction<DataBean>) value -> DataType.msg.equals(value.getType()));

        stream.msg5windowCount(msgDataset);

        /*   -------------   gf five count  --------------      */
//        Dataset<DataBean> gf2bean = bean2data.filter((FilterFunction<DataBean>) value -> DataType.gift.equals(value.getType()));
//
//        stream.gf5windowCount(gf2bean);
//        DataSourceRegister KafkaSourceProvider


        spark.streams().awaitAnyTermination();
    }

    private void msg5windowCount(Dataset<DataBean> msgDataset) {

        Dataset<Row> msgWindow = msgDataset
                .withWatermark("timestamp", Constant.FIVE_MINUTES)
                .groupBy(
                        functions.window(msgDataset.col("timestamp"), Constant.TWO_MINUTES, Constant.ONE_MINUTES),
                        msgDataset.col("web"), msgDataset.col("achorId"),msgDataset.col("type")
                )
                .count();

        Dataset<Row> msgWindowForAppend = msgWindow.select(col("web"),
                col("achorId"),
                col("type"),
                col("count").as("msgCount"),
                col("window").getField("start").as("time"))
                .withColumn("timestamp", col("time").cast("String"))
                .withColumn("id", concat_ws("_",col("web"),col("achorId"),col("time").cast("long")))
//                .groupByKey((MapFunction<Row,Row>) f -> f, Encoders.bean(Row.class))
//                .flatMapGroupsWithState(OutputMode.Update(), GroupStateTimeout.NoTimeout(), mappingFunctions, Encoders.INT(), Encoders.bean(Row.class))
//                .mapGroupsWithState(mappingFunction, Encoders.INT(), Encoders.bean(Row.class), GroupStateTimeout.NoTimeout())
                ;

        msgWindowForAppend.writeStream().outputMode(OutputMode.Update())
                .foreach(new EsSink(conf, true))
                .queryName(Constant.MSG_FIVE_WINDOW_COUNT_QUERY_NAME)
                .option(Constant.CHECKPOINT_LOCATION, Constant.MSG_FIVE_WINDOW_COUNT_CHECKPOINT)
                .trigger(Trigger.ProcessingTime(Constant.ONE_MINUTES))
                .start();
    }

    private void gf5windowCount(Dataset<DataBean> gf2bean) {
        Dataset<Row> gfWindow = gf2bean
                .withWatermark("timestamp", Constant.FIVE_MINUTES)
                .dropDuplicates("")
                .groupBy(
                        functions.window(gf2bean.col("timestamp"), Constant.TWO_MINUTES, Constant.ONE_MINUTES),
                        gf2bean.col("web"), gf2bean.col("achorId"),gf2bean.col("type")
                )
                .sum("gfPrice");

        Dataset<Row> gfWindowForAppend = gfWindow.select(col("web"),
                col("achorId"),
                col("type"),
                col("sum(gfPrice)").as("income"),
                col("window").getField("start").as("time")
        )
                .withColumn("timestamp", col("time").cast("String"))
                .withColumn("id", concat_ws("_",col("web"),col("achorId"),col("time").cast("long")));

        gfWindowForAppend.writeStream()
                .outputMode(OutputMode.Complete())
                .foreach(new EsSink(conf, true))
                .queryName(Constant.GF_FIVE_WINDOW_COUNT_QUERY_NAME)
                .option(Constant.CHECKPOINT_LOCATION, Constant.GF_FIVE_WINDOW_COUNT_CHECKPOINT)
                .trigger(Trigger.ProcessingTime(Constant.ONE_MINUTES))
                .start();
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

    MapGroupsWithStateFunction<Row, Row, Integer, Row> mappingFunction =
            new MapGroupsWithStateFunction<Row, Row, Integer, Row>() {

                @Override
                public Row call(Row key, Iterator<Row> value, GroupState<Integer> state) {
                    if (state.hasTimedOut()) {            // If called when timing out, remove the state
                        state.remove();

                    } else if (state.exists()) {            // If state exists, use it for processing
                        int existingState = state.get();      // Get the existing state
//                        boolean shouldRemove = ...;           // Decide whether to remove the state
//                        if (shouldRemove) {
//                            state.remove();                     // Remove the state
//                        } else {
//                            int newState = ...;
//                            state.update(newState);             // Set the new state
//                            state.setTimeoutDuration("1 hour"); // Set the timeout
//                        }

                    } else {
//                        int initialState = ...;               // Set the initial state
//                        state.update(initialState);
//                        state.setTimeoutDuration("1 hour");   // Set the timeout
                    }
                    return null;
                    // return something
                }
            };

    FlatMapGroupsWithStateFunction<Row, Row, Integer, Row> mappingFunctions =
            new FlatMapGroupsWithStateFunction<Row, Row, Integer, Row>() {

                @Override
                public Iterator<Row> call(Row key, Iterator<Row> value, GroupState<Integer> state) {
                    if (state.hasTimedOut()) {            // If called when timing out, remove the state
                        state.remove();

                    } else if (state.exists()) {            // If state exists, use it for processing
                        int existingState = state.get();      // Get the existing state
//                        boolean shouldRemove = ...;           // Decide whether to remove the state
//                        if (shouldRemove) {
//                            state.remove();                     // Remove the state
//                        } else {
//                            int newState = ...;
//                            state.update(newState);             // Set the new state
//                            state.setTimeoutDuration("1 hour"); // Set the timeout
//                        }

                    } else {
//                        int initialState = ...;               // Set the initial state
//                        state.update(initialState);
//                        state.setTimeoutDuration("1 hour");   // Set the timeout
                    }
                    return null;
                    // return something
                }
            };


}
