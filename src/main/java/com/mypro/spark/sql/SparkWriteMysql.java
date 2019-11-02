package com.mypro.spark.sql;

import com.google.common.collect.Lists;
import com.mypro.spark.base.SparkBean;
import com.mypro.spark.work.Model;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.*;

import java.util.List;

/**
 * @author fgzhong
 * @description: spark write to mysql
 * @since 2019/3/27
 */
public class SparkWriteMysql {

    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("a").setMaster("local[2]");
        SparkSession ss = SparkSession.builder()
                .config(conf).getOrCreate();
        Logger.getLogger("org").setLevel(Level.ERROR);

        List<SparkBean> data = Lists.newArrayList();
        Encoder<SparkBean> personEncoder = Encoders.bean(SparkBean.class);
        Dataset<SparkBean> dataSet = ss.createDataset(data, personEncoder);

        dataSet.write().format("jdbc")
                .mode(SaveMode.Append)
                .option("url", "jdbc:mysql://mc.ali.bjol.bigdata.udsp.com:3306/uds?useUnicode=true&characterEncoding=utf-8&useSSL=false&autoReconnect=true")
                .option("driver", "com.mysql.cj.jdbc.Driver")
                .option("dbtable", "uds_fdm_video_log_di_2")
                .option("user", "uds").option("password", "rsjxfyyyzf2018")
                .save();
        ss.stop();
    }


}
