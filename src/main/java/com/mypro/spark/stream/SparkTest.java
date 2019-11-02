package com.mypro.spark.stream;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.mypro.spark.stream.model.DyGiftModel;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.Optional;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.State;
import org.apache.spark.streaming.StateSpec;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.jsoup.Jsoup;
import scala.None;
import scala.Option;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/7/25
 */
public class SparkTest {



    public static void main(String[] args) throws Exception {
        SparkConf conf =  new SparkConf().setMaster("local[2]").setAppName("UpdateStatebyKeyApp");
        Logger.getLogger("org").setLevel(Level.ERROR);
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(10));
        JavaReceiverInputDStream inputDStream = jssc.socketTextStream("localhost",9999);

        JavaPairDStream<String, Integer> s = inputDStream.flatMap(f -> Arrays.asList(f.toString().split(" ")).iterator()).mapToPair(f -> new Tuple2(f,1));
        jssc.checkpoint("data/test");

        JavaPairDStream<String, Integer> s1 = s.mapToPair(f -> new Tuple2<>("2",1));

        System.nanoTime();
        s.union(s1).print();
//        JavaPairDStream<String, GfState> s1 =  s.updateStateByKey((Function2<
//                List<Integer>,
//                Optional<GfState>,
//                Optional<GfState>>
//                )
//                (dataList,preState) ->
//                {
//                    GfState state = preState.orElse(new GfState("1"));
//                    System.out.println(state);
//                    dataList.forEach(f -> state.setCount(state.getCount() + f));
//                    if (state.getCount() == 3) {
//                        return Optional.ofNullable(null);
//                    }
//                    return Optional.of(state);
//                });
//        JavaPairDStream<String, GfState> s2 =  s.mapWithState((Function2<
//                        String , Optional<Integer> ,
//                State<GfState>
//                >
//                )
//                (key,dataList,f1) ->
//                {
//                    GfState state = f1.orElse(new GfState("1"));
//                    System.out.println(state);
//                    state.setCount(state.getCount() + dataList);
//                    if (state.getCount() == 3) {
//                        return Optional.ofNullable(null);
//                    }
//                    return Optional.of(state);
//                });
        s.print();
        jssc.start();              // Start the computation
        jssc.awaitTermination();
    }





}
