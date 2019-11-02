package com.mypro.spark.work;

import com.google.common.collect.Lists;
import com.mypro.spark.util.ConfigUtil;
import com.mypro.spider.config.ESConfig;
import com.mypro.spider.utils.ProxyUtil;
import com.mypro.spider.utils.TimeoutThreadPoolExecutor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.output.MapFileOutputFormat;
import org.apache.spark.Partition;
import org.apache.spark.RangePartitioner;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.rdd.MapPartitionsRDD;
import org.apache.spark.util.LongAccumulator;
import org.apache.spark.util.random.SamplingUtils;
import org.elasticsearch.spark.rdd.api.java.JavaEsSpark;
import org.spark_project.guava.collect.ImmutableMap;
import scala.Tuple2;
import scala.reflect.ClassTag;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author fgzhong
 * @description: 图片下载
 * @since 2019/3/6
 */
public class SparkPic {


    private final static String IP_REG = "(2(5[0-5]{1}|[0-4]\\d{1})|[0-1]?\\d{1,2})(\\.(2(5[0-5]{1}|[0-4]\\d{1})|[0-1]?\\d{1,2})){3}:\\d+";
    private final static Random PROXY_RANDOM = new Random();
    private final static List<Protocol> protocols = Arrays.asList(okhttp3.Protocol.HTTP_2,okhttp3.Protocol.HTTP_1_1);
    private final static OkHttpClient client = new OkHttpClient.Builder()
            .protocols(protocols)
            .followRedirects(true)
            .callTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
            ;


    public static void main(String[] args) throws Exception{
        SparkConf conf = new SparkConf()
                .setMaster("local[1]")
                .setAppName("SparkPic");
        conf.set("es.nodes","es1.ali.szol.bds.com:9200,es2.ali.szol.bds.com:9200");
        conf.set(ESConfig.INPUT_MAX_DOCS_PER_PARTITION, "250000");
        conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");

        ConfigUtil.sparkConfUtil(conf);
        JavaSparkContext context = new JavaSparkContext(conf);
        LongAccumulator accum = context.sc().longAccumulator("pic-suc");

        JavaPairRDD<String, Map<String, Object>> esRDD = JavaEsSpark.esRDD(context, "warehouse_picture").repartition(1000);

        JavaRDD<Map<String, Object>> fetchRdd =  esRDD.mapPartitions(f -> {

            /*  proxy  */
//            final List<String> proxyList = Lists.newArrayList();
//            try {
//                proxyList.addAll(ProxyUtil.getList());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            TimeoutThreadPoolExecutor executor = TimeoutThreadPoolExecutor.newFixedThreadPool(300,30, TimeUnit.SECONDS);
            List<Map<String, Object>> newRDD = Lists.newArrayList();

            while (f.hasNext()) {
                while (!executor.isReady()) {
                    TimeUnit.MILLISECONDS.sleep(100);
                }
                Map<String, Object> map =  f.next()._2;
                executor.execute(() -> {
                    List<String> picUrls = (List<String>) map.get("picUrl");
//                    OkHttpClient client;
                    for (String pic : picUrls) {
                        if (Thread.currentThread().isInterrupted()) {
                            break;
                        }
                        if (StringUtils.isBlank(pic) || (!pic.startsWith("https://") && !pic.startsWith("http://"))) {
                            continue;
                        }
//                        String proxyStr = "";
//                        synchronized (proxyList) {
//                            if (proxyList.size() >0) {
//                                proxyStr = proxyList.get(PROXY_RANDOM.nextInt(proxyList.size()));
//                            }
//                        }
//                        if (StringUtils.isNotBlank(proxyStr) && proxyStr.matches(IP_REG)) {
//                            client = builder.proxy(new Proxy(Proxy.Type.HTTP,
//                                    new InetSocketAddress(proxyStr.split(":")[0], Integer.valueOf(proxyStr.split(":")[1])))).build();
//                        } else {
//                            client = builder.build();
//                        }
                        try {
                            Response response = client.newCall(new Request.Builder().url(pic).build()).execute();
                            if (response.code() == 200) {
                                byte[] content = response.body().bytes();
                                if (content.length > 0) {
                                    map.put("content", content);
                                    map.put("status", "v+");
                                    break;
                                }
                            }
                        }  catch (Exception e) {
                        }
                    }
                    synchronized (newRDD) {
                        if (!Thread.currentThread().isInterrupted()) {
                            newRDD.add(map);
                        }
                    }
                });
            }
            long endtime = System.currentTimeMillis();
            while (executor.getActive() > 0) {
                if (System.currentTimeMillis() - endtime > 30000) {
                    System.out.println(" -----------------  "+ executor.getActive());
                    break;
                }
                TimeUnit.MILLISECONDS.sleep(500);
            }
            executor.shutdownNow();
            TimeUnit.MILLISECONDS.sleep(10000);
            System.out.println(" -----------------  end  "+ executor.getActive());

            return newRDD.iterator();
        });


        JavaPairRDD<Text, Text> picRdd =  fetchRdd.filter(f -> "v+".equals(f.get("status").toString())).mapToPair(f -> {
            String key = f.get("key").toString();
            String value = Base64.getEncoder().encodeToString((byte[]) f.get("content"));
            accum.add(1);
            return new Tuple2<>(new Text(key), new Text(value));
        });
//        JavaPairRDD<Text, Text> picCacheRdd = picRdd.cache();
//        System.out.println(picCacheRdd.count());
//        System.out.println(" picRdd  -----  " + picCacheRdd.getNumPartitions());

        picRdd.coalesce(20,true).sortByKey().saveAsNewAPIHadoopFile("/user/maplecloudy/mypro/data-pic-v", Text.class, Text.class, MapFileOutputFormat.class);

//        JavaRDD<Map<String, ?>> sucPic = fetchRdd.filter(f -> "2".equals(f.get("status").toString())).map(f
//                -> ImmutableMap.of("key", f.get("key").toString()));
//
//        JavaEsSpark.saveToEs(sucPic, "warehouse_picture_test/type",ImmutableMap.of(
//                "es.mapping.id", "key",
//                "es.write.operation","update",
//                "es.update.script.inline", "ctx._source.status=1,ctx._source.retry+=1"));

        context.stop();
    }

}
