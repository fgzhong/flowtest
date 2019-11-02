package com.mypro.spark.work;

import com.google.common.collect.Lists;
import com.mypro.spark.util.ConfigUtil;
import com.mypro.spider.config.ESConfig;
import com.mypro.spider.utils.TimeoutThreadPoolExecutor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.MapFileOutputFormat;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.util.LongAccumulator;
import org.elasticsearch.spark.rdd.api.java.JavaEsSpark;
import scala.Tuple2;

import java.util.*;
import java.util.concurrent.TimeUnit;
import static org.apache.spark.sql.functions.col;


/**
 * @author fgzhong
 * @description: 图片下载
 * @since 2019/3/6
 */
public class SparkPicBak {


    public static void main(String[] args) throws Exception{
        SparkConf conf = new SparkConf()
                .setMaster("local[1]")
                .set("spark.hadoop.mapreduce.input.fileinputformat.split.maxsize", "134217728")
                .set("spark.hadoop.mapreduce.input.fileinputformat.split.minsize", "134217728")
                .setAppName("SparkPic-bak");
        conf.set(ESConfig.INPUT_MAX_DOCS_PER_PARTITION, "250000");
        conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");

        ConfigUtil.sparkConfUtil(conf);
        SparkSession ss = SparkSession.builder()
                .config(conf).getOrCreate();
        JavaSparkContext context = JavaSparkContext.fromSparkContext(ss.sparkContext());

        JavaPairRDD<Text,Text> esRDD = context.newAPIHadoopFile("/user/maplecloudy/mypro/data-pic/**/data", SequenceFileInputFormat.class,  Text.class, Text.class, context.hadoopConfiguration());

        ArrayList<StructField> fields = new ArrayList<>();
        StructField field = null;
        field = DataTypes.createStructField("sid", DataTypes.StringType, true);
        fields.add(field);
        field = DataTypes.createStructField("sname", DataTypes.StringType, true);
        fields.add(field);
        StructType schema = DataTypes.createStructType(fields);
        Dataset<Row> df = ss.createDataFrame(esRDD.map(f ->
            RowFactory.create(f._1.toString(), f._2.toString())), schema);


        df.sort(col("sid").asc())
                .toJavaRDD()
                .mapToPair(
                        f -> new Tuple2<>(new Text(f.getString(0)), new Text(f.getString(1)))
                )
                .saveAsNewAPIHadoopFile(
                        "/user/maplecloudy/mypro/data-pic-v2",
                        Text.class, Text.class,
                        MapFileOutputFormat.class);


        context.stop();
    }

}
