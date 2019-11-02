package com.mypro.spark;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.mypro.spark.base.SparkBean;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.api.java.UDF0;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.omg.CORBA.OBJ_ADAPTER;
import scala.Tuple2;


import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/7/25
 */
public class SparkTest {

    public static void main(String[] args) throws Exception{
        Logger.getLogger("org").setLevel(Level.ERROR);
        SparkConf conf = new SparkConf().setAppName("a").setMaster("local[2]");
        SparkSession ss = SparkSession.builder()
                .config(conf).getOrCreate();

        Dataset<Row> gameRow = ss.read()
                .format("text")
                .load("/Users/fgzhong/Downloads/test.txt");

        gameRow.createOrReplaceTempView("game");

//        ss.udf().register("")

        Dataset<Row> rowDataset = ss.sql("select value as id, value from game").repartition(3).withColumn("index", org.apache.spark.sql.functions.monotonically_increasing_id());
        rowDataset.show();
        StructType type = new StructType().add("id","string").add("value","string")
                .add("index","long");
        JavaRDD<Row> rdd = ss.sql("select value as id, value from game").orderBy("id").javaRDD().repartition(2).zipWithIndex().map(f -> RowFactory.create(f._1.get(0), f._1.get(1), f._2));
        ss.createDataFrame(rdd, type).sort("value").show();

        JavaRDD<Row> rdd1 = ss.sql("select value as id, value from game").javaRDD().repartition(3).zipWithUniqueId().map(f -> RowFactory.create(f._1.get(0), f._1.get(1), f._2));
        ss.createDataFrame(rdd1, type).show();

        FileSystem fileSystem = FileSystem.get(new Configuration());
        fileSystem.getStatus(new Path("")).getRemaining();
    }


    public class U extends UDF {

    }


}
