package com.mypro.spark.stream;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mypro.spark.stream.listener.Listener;
import com.mypro.spark.stream.model.*;
import com.mypro.spark.stream.state.DmState;
import com.mypro.spark.stream.state.GfState;
import com.mypro.spark.stream.utils.DyGiftUtil;
import com.mypro.spark.stream.utils.StateSaveUtils;
import com.mypro.spark.util.ConfigUtil;
import kafka.serializer.StringDecoder;
import kafka.zk.KafkaZkClient;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.serde2.io.ByteWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.*;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.Optional;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.deploy.SparkHadoopUtil;
import org.apache.spark.rdd.RDD;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka010.*;
import scala.Tuple2;
import scala.reflect.ClassManifestFactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/8/3
 */
public class SparkStreaming implements Serializable{

    public static void main(String[] args) throws Exception{
        new SparkStreaming().run();
    }

    public void run() throws Exception{

//        Logger.getLogger("org").setLevel(Level.WARN);
        SparkConf conf = new SparkConf().setMaster("local").setAppName("spark-stream-v1");
        ConfigUtil.sparkConfUtil(conf, "spark-stream.conf");
        conf.set("spark.streaming.stopGracefullyOnShutdown","true");
        conf.registerKryoClasses(new Class[]{DataBean.class,
                UnionKey.class,
                DyGiftModel.class,
                Rank.class,
                GiftDataModel.class,
                DmDataModel.class,
                DmState.class,
                GfState.class,
                BroadcastManager.class});

        FileSystem fs = FileSystem.get(SparkHadoopUtil.get().newConfiguration(conf));
        if (fs.exists(new Path(conf.get(Constant.STREAM_CHECKPOINT,Constant.DEFAULT_STREAM_CHECKPOINT)))) {
            fs.delete(new Path(conf.get(Constant.STREAM_CHECKPOINT,Constant.DEFAULT_STREAM_CHECKPOINT)), true);
        }

        String version = conf.get(Constant.PROJECT_VERSION,Constant.DEFAULT_PROJECT_VERSION);
        String esNode = conf.get(Constant.ES_ADDR,Constant.DEFAULT_ES_ADDR);
        String gfIndex = conf.get(Constant.ES_GIFT,Constant.DEFAULT_ES_GIFT);

        Map<String, Object> kafkaParams = Maps.newHashMap();
        kafkaParams.put("bootstrap.servers", conf.get(Constant.KAFKA_SERVER_ADDR,Constant.DEFAULT_KAFKA_SERVER_ADDR));
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", conf.get(Constant.KAFKA_GROUP_ID,Constant.DEFAULT_KAFKA_GROUP_ID)
                + version);
        kafkaParams.put("auto.offset.reset", "latest");
//        kafkaParams.put("auto.offset.reset", "earliest");
        kafkaParams.put("enable.auto.commit", false);
        Collection<String> topics = Arrays.asList(conf.get(Constant.KAFKA_TOPIC,Constant.DEFAULT_KAFKA_TOPIC));

        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.minutes(1));

        jssc.checkpoint(conf.get(Constant.STREAM_CHECKPOINT,Constant.DEFAULT_STREAM_CHECKPOINT));

        jssc.addStreamingListener(new Listener());


        Broadcast<HashMap<UnionKey,Long>> fiveDataForDistance = jssc.sparkContext().sc().broadcast(Maps.newHashMap(), ClassManifestFactory.classType(HashMap.class));
        Map<TopicPartition, Long> offsetNow = Maps.newHashMap();

        JavaInputDStream<ConsumerRecord<String, String>> stream =
                KafkaUtils.createDirectStream(
                        jssc,
                        LocationStrategies.PreferConsistent(),
                        ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams, offsetNow)
                );


        JavaPairDStream<String, String> pairs = stream.mapToPair(record -> new Tuple2<>(record.key(), record.value()));
        JavaPairDStream<UnionKey, DataBean> dataBean = pairs.mapToPair(f -> {
            DataBean bean = new DataBean();
            JSONObject dataJson = JSON.parseObject(f._2);
            String uid = f._1;
            bean.setTime(Long.valueOf(dataJson.getString("timestamp")));
            String web = uid.split("_")[0];
            String achorId = uid.split("_")[1];
            bean.setWeb(web);
            bean.setAchorId(achorId);
            bean.setUid(uid);
            bean.setId(uid + "_" + bean.getTime());
            UnionKey key = new UnionKey();
            key.setUid(bean.getUid());
            key.setTime(bean.getTime());
            key.setHash(dataJson.getString("hash"));
            key.setVersion(Integer.valueOf(dataJson.getString("version")));
            switch (web) {
                case WebName.dy: dy(dataJson, bean, esNode); break;
                default:;
            }
            return new Tuple2<>(key, bean);
        }).filter(f -> !DataType.undefine.equals(f._2.getType())).cache();

        JavaPairDStream<String, DataBean> data = dataBean.transformToPair(f -> {
            SparkContext sc = f.context();

//            Map<Object, RDD<?>> cacheRDD = JavaConversions.mapAsJavaMap(sc.getPersistentRDDs());
//            cacheRDD.entrySet().forEach(x -> x.getValue().unpersist(false));

//            JavaPairRDD<UnionKey, DataBean> dataV1 = f.filter(v -> v._1.getVersion() == 1);
//            JavaPairRDD<UnionKey, DataBean> dataV2 = f.filter(v -> v._1.getVersion() == 2);
//            JavaPairRDD<UnionKey, DataBean> dataUnion = dataV1.union(dataV2);    //  union 两数据直接合并，不去重


            JavaPairRDD<UnionKey, DataBean> distinctData = f
                    .distinct()   //  reduceBykey._1
                    .repartition(90)
                    .filter(v -> !fiveDataForDistance.getValue().containsKey(v._1))
//                    .cache()
                    ;
            List<Tuple2<UnionKey, Long>> newData = distinctData.map(v-> new Tuple2<>(v._1, v._2.getTime())).collect();
            BroadcastManager.updata(sc, fiveDataForDistance, newData);
            JavaPairRDD<String, DataBean> result = distinctData.mapToPair(v -> new Tuple2<>(v._2.getWeb() + "_" + v._2.getAchorId(), v._2));
//            distinctData.unpersist(); // 为什么添加后会导致result无结果

            return result;
        })
                ;
        JavaPairDStream<String, DataBean> dataForCache = data.cache();
//        JavaPairDStream<String, DataBean> useDataBean = data.filter(f -> !DataType.undefine.equals(f._2.getType()));
        JavaPairDStream<String, DataBean> gfData = dataForCache.filter(f -> DataType.gift.equals(f._2.getType()) || DataType.offonline.equals(f._2.getType()));
        JavaPairDStream<String, DataBean> msgData = dataForCache.filter(f -> DataType.msg.equals(f._2.getType()) || DataType.offonline.equals(f._2.getType()));
//        JavaPairDStream<String, DataBean> unknowGfData = data.filter(f -> DataType.gift_unkown.equals(f._2.getType()));

        JavaPairDStream<String, GfState> gfState = gfData.updateStateByKey((Function2<
                List<DataBean>,
                Optional<GfState>,
                Optional<GfState>>
                )
                (dataList,preState) ->
                {
                    GfState state = preState.orElse(new GfState(version));
                    state.init();
                    dataList.forEach(v1 -> state.add(v1));
                    if (state.getUid() == null || state.isOffline()) {
                        return Optional.ofNullable(null);
                    }
                    return Optional.of(state);
                })
                .cache()
                ;


        gfState.reduceByKeyAndWindow((v1, v2) -> {
            return null;
        }, Duration.apply(1));

        gfState.foreachRDD((rdd, time) -> {
            rdd.foreachPartition(partitionIterator -> {
                int partitionID = TaskContext.get().partitionId();
                System.out.println( "  \n ----------------------------  " + partitionID + " ----------   \n");
                List<GfState> states = Lists.newArrayList();
                while (partitionIterator.hasNext()) {
                    states.add(partitionIterator.next()._2);
                }
                StateSaveUtils.getInstance(esNode).pushData(states, gfIndex, partitionID + "");
            });
        });



        JavaPairDStream<String, DmState> dmState = msgData.updateStateByKey((Function2<
                List<DataBean>,
                Optional<DmState>,
                Optional<DmState>>
                )
                (dataList,preState) ->
                {
                    DmState state = preState.orElse(new DmState(version));
                    state.init();
                    dataList.forEach(v1 -> state.add(v1));
                    if (state.getUid() == null || state.isOffline()) {
                        return Optional.ofNullable(null);
                    }
                    return Optional.of(state);
                })
                .cache()
                ;

        dmState.foreachRDD((rdd, time) -> {
            rdd.foreachPartition(partitionIterator -> {
                int partitionID = TaskContext.get().partitionId();
                System.out.println( "  \n ----------------------------  " + partitionID + " ----------   \n");
                List<DmState> states = Lists.newArrayList();
                while (partitionIterator.hasNext()) {
                    states.add(partitionIterator.next()._2);
                }
                StateSaveUtils.getInstance(esNode).pushDmData(states, gfIndex, partitionID + "");
            });
        });

        JavaPairDStream<String, Tuple2<Iterable<GfState>, Iterable<DmState>>> rankData =  gfState.cogroup(dmState).cache();

        JavaPairDStream<Rank,Integer> gfRankJavaDStream = rankData.mapToPair(f -> {
            Rank rank = new Rank();
            f._2._1.forEach(v -> {
                rank.setWeb(v.getWeb());
                rank.setAchorId(v.getAchorId());
                rank.setPc(v.getTotalIncome());
                rank.setTime(System.currentTimeMillis());
            });
            f._2._2.forEach(v -> {
                rank.setOnline(v.getUserMap().size());
                rank.setTotalonline(v.getCount());
            });
            return new Tuple2<>(rank, 1);
        });


        gfRankJavaDStream.foreachRDD((rdd, time) -> {
            rdd.repartitionAndSortWithinPartitions(new TopByWebPartitioner(6))
//                    .collect();
                    .foreachPartition(top -> {
                int partitionID = TaskContext.get().partitionId();
                System.out.println( "  \n ----------------------------  " + partitionID + " ----------   \n");
                List<Rank> states = Lists.newArrayList();
                while (top.hasNext()) {
                    states.add(top.next()._1);
                    if (states.size() == 100) {
                        break;
                    }
                }
                StateSaveUtils.getInstance(esNode).pushGfRankData(states, gfIndex, partitionID + "");
            });
        });


        stream.foreachRDD(rdd -> {
            if (!rdd.isEmpty()) {

            }
        });

        jssc.start();              // Start the computation
        jssc.awaitTermination();
        jssc.stop(true,true);
    }

    private void getOffsets(Set<String> topics, KafkaZkClient zkClient, Map<String,String> kafkaParams){
//        Map<String, Seq<Object>> partitionsForTopics = zkClient.getPartitionsForTopics(JavaConversions.asScalaSet(topics));
//        zkClient.getConsumerOffset(kafkaParams.get("group.id").toString(), new TopicPartition("1",1));
        
    }

    private void updateOffsets(KafkaZkClient zkClient, JavaPairRDD rdd, Map<String,String> kafkaParams){
        OffsetRange[] offsets = ((HasOffsetRanges) rdd.rdd()).offsetRanges();
        for (OffsetRange offset : offsets) {
            zkClient.setOrCreateConsumerOffset(kafkaParams.get("group.id").toString(), offset.topicPartition(), offset.untilOffset());
        }
    }


    private static DataBean dy(JSONObject js, DataBean bean, String esNode){
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
                DyGiftModel model = DyGiftUtil.getDyGiftModel(esNode, bean.getGfId());
                if (model != null) {
                    bean.setGfSigPrice(model.getPc());
                    double income = bean.getGfSigPrice()*bean.getGfCount();
                    BigDecimal decimal = new BigDecimal(income).setScale(1, BigDecimal.ROUND_DOWN);
                    bean.setGfPrice(decimal);
                } else {
                    bean.setType(DataType.gift_unkown);
                    bean.setMsg(js.toString());
                }
            } break;
            case DataType.offonline : {
                bean.setType(DataType.offonline);
            } break;
            default: {
                bean.setType(DataType.undefine);
                bean.setMsg(js.toString());
            }
        }
        return bean;
    }

    private class UDF1 extends UDF {
        private final LongWritable longWritable = new LongWritable();

        public LongWritable evaluate(ByteWritable i) {
            if (i == null) {
                return null;
            } else {
                this.longWritable.set((long)i.get());
                return this.longWritable;
            }
        }
    }

}
