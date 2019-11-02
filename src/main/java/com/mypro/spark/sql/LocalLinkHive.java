package com.mypro.spark.sql;

import com.google.common.collect.Lists;
import com.mypro.spark.work.Model;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.SparkSubmit;
import org.apache.spark.sql.*;

import java.util.List;

/**
 * @author fgzhong
 * @description: 本地连线上hive
 * @since 2019/3/27
 */
public class LocalLinkHive {

    public static void main(String[] args) {

        Logger.getLogger("org").setLevel(Level.ERROR);
        /* 在resource中添加配置文件 core-site.xml/hdfs-site.xml/hive-site.xml */
        /* 设置hadoop用户名 */
        System.getProperties().setProperty("HADOOP_USER_NAME","maplecloudy");
        SparkConf conf = new SparkConf().setAppName("a").setMaster("local[*]");
        SparkSession ss = SparkSession.builder()
                .config(conf)
                /* 设置hive metastore uri */
                .config("hive.metastore.uris", "thrift://dn5.ali.bjol.bigdata.udsp.com:9083")
                .enableHiveSupport()
                .getOrCreate();
        List<Model> data = Lists.newArrayList();
        Model model = new Model();
        model.setAppId("v");
        model.setQuotaId("s");
        model.setLog_date("2019-01-01");
        model.setResult(1d);
        data.add(model);
        Encoder<Model> personEncoder = Encoders.bean(Model.class);
        Dataset<Model> dataSet = ss.createDataset(data, personEncoder);
        dataSet.write().format("parquet")
                .mode(SaveMode.Append)
                .option("path","/user/maplecloudy/stat/game/test1")
                .saveAsTable("maplecloudy.localtest");
        ss.stop();
    }

}
