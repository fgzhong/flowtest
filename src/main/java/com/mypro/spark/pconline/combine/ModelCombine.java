package com.mypro.spark.pconline.combine;

import com.mypro.spark.util.ConfigUtil;
import com.mypro.spider.parse.FixContent;
import com.mypro.spider.putformat.SpiderInputFormat;
import com.mypro.spider.putformat.SpiderOutputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.yarn.server.nodemanager.LinuxContainerExecutor;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.AbstractJavaRDDLike;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDDLike;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.deploy.SparkHadoopUtil;
import org.apache.spark.deploy.yarn.Client;
import org.apache.spark.deploy.yarn.ExecutorLauncher;
import org.apache.spark.executor.CoarseGrainedExecutorBackend;
import org.spark_project.jetty.servlet.DefaultServlet;
import org.spark_project.jetty.servlet.ServletHandler;

import javax.servlet.http.HttpServletRequest;


/**
 * @author fgzhong
 * @description:
 * @since 2019/4/13
 */
public class ModelCombine extends Configured implements Tool {


    @Override
    public int run(String[] args) throws Exception {
//        Logger.getLogger("org").setLevel(Level.DEBUG);
//        System.getProperties().setProperty("HADOOP_USER_NAME","maplecloudy");
//        System.getProperties().setProperty("HADOOP_HOME ","/opt/cloudera/parcels/CDH-5.15.1-1.cdh5.15.1.p0.4/lib");
        SparkConf conf = new SparkConf(true)
//                .setMaster("local[2]")
                .setAppName("ModelCombine")
                .set("spark.hadoop.mapreduce.input.fileinputformat.split.maxsize", "134217728")
                .set("spark.hadoop.mapreduce.input.fileinputformat.split.minsize", "134217728")
                ;

        ConfigUtil.sparkConfUtil(conf);
        JavaSparkContext jsc = new JavaSparkContext(conf);
        JavaPairRDD<Text, Text> modelRDD = jsc.newAPIHadoopFile(
                "/user/maplecloudy/mypro/data-fix/parse-v3/",
                SpiderInputFormat.class
                , Text.class, Text.class,
                jsc.hadoopConfiguration());
        modelRDD.saveAsNewAPIHadoopFile("/user/maplecloudy/mypro/data-fix/parse-v3-1/",
                Text.class, Text.class,
                SpiderOutputFormat.class,
                jsc.hadoopConfiguration());
        return 0;
    }

    public static void main(String[] args) throws Exception{
        int res = ToolRunner.run(new Configuration(), new ModelCombine(), args);
        System.exit(res);
    }
}
