package com.mypro.spark.work;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.mypro.spark.model.GameModel;
import org.apache.avro.generic.GenericData;
import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapreduce.AvroKeyInputFormat;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;

import java.util.*;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/3/9
 */
public class GameCleanJob {
    private final static FastDateFormat dateFormat =FastDateFormat.getInstance("yyyy-MM-dd", Locale.CHINA);

    /** 线程安全 */
    private final static ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) throws Exception{

        SparkConf conf = new SparkConf().setAppName("a").setMaster("local[2]");
//        conf.set("spark.sql.sources.partitionColumnTypeInference.enabled", "false");
        //  解决spark与hive 对 decimal类型的精确度不同的差异
//        conf.set("spark.sql.parquet.writeLegacyFormat", "true");

        SparkSession ss = SparkSession.builder()
                .config(conf)
                /* 动态分区 */
//                .config("hive.exec.dynamic.partition", "true")
//                .config("hive.exec.dynamic.partition.mode", "nonstrict")
//              .config("spark.sql.warehouse.dir", "path")
                //	    		  .config("spark.sql.warehouse.dir", "/user/maplecloudy")
//                .config("hive.metastore.uris", "thrift://dn5.ali.bjol.bigdata.udsp.com:9083")
//	    		  .config("hive.jdbc_passwd.auth.maplecloudy","")
//	    		  .config("javax.jdo.option.ConnectionUserName","maplecloudy")
//	    		  .config("javax.jdo.option.ConnectionPassword","")
//	    	      .config("javax.jdo.option.ConnectionUserName","maplecloudy")
//	    	      .config("javax.jdo.option.ConnectionPassword","")
//                .config("hive.exec.local.scratchdir","/tmp/maple")
                .enableHiveSupport()
                .getOrCreate();
        JavaSparkContext sc = JavaSparkContext.fromSparkContext(ss.sparkContext());
        sc.setLogLevel("ERROR");
        Logger.getLogger("org").setLevel(Level.ERROR);

        String path = "/Users/zhuowenwei/Desktop/uds/**/**";
//        JavaPairRDD<AvroKey, NullWritable> origRDD = sc.newAPIHadoopFile(path, AvroKeyInputFormat.class, AvroKey.class, NullWritable.class, sc.hadoopConfiguration());
//        System.out.println(groupsSort.count() + " ----------------- ");
//        groupsSort
//                .flatMap(f -> f._2.iterator())
//                .mapToPair(f -> new Tuple2<>(f.getDeviceId(), f))
//                .saveAsNewAPIHadoopFile("/Users/zhuowenwei/Desktop/udsH", String.class, GameModel.class, FileOutputFormat.class);



//        StructType
        /* rdd dataframe(Dataset<Row>) dataset(Dataset<GameModel>) 关系区别*/
//        Dataset<Row> df = ss.createDataFrame(d, GameModel.class);
//        df.show();
//        /*  按时间分区保存到hive */
//        df.coalesce(1).write()
                /* parquet json csv jdbc Hive text*/
//                .format("com.databricks.spark.avro")
                /* 写入模式： Append 追加，Overwrite 覆盖，ErrorIfExists 存在报错，Ignore 忽略*/
//                .mode(SaveMode.Overwrite)
                /* 外部表 */
//                .option("path","/some/path")
//                .partitionBy("date","hour")   //  目录 date=2019-01-01,hour=12
//                .jdbc("url","table", new Properties() )
//                .saveAsTable("table")
//                .save("/Users/zhuowenwei/Desktop/udsH");


//        jdbcDF.write
//                .format("jdbc")
//                .option("url", url)
//                .option("dbtable", "vulcanus_ljl.data_dict_temp1")
//                .option("user", "vulcanus_ljl")
//                .option("password", "mypassword")
//                .option("createTableColumnTypes", "dict_name varchar(60), dict_type varchar(60)") // 没有指定的字段使用默认的类型
//                .save()

        sc.stop();
        ss.stop();
    }

}
