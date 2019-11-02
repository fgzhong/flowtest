package com.mypro.spark.base;

import com.google.common.collect.Lists;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author fgzhong
 * @description: spark
 * @since 2019/3/26
 */
public class SparkBase {

    public static void main(String[] args) {

        //        conf.set("spark.sql.sources.partitionColumnTypeInference.enabled", "false");
        //  解决spark与hive 对 decimal类型的精确度不同的差异
//        conf.set("spark.sql.parquet.writeLegacyFormat", "true");

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

        /* 全局日志等级，在程序开头设置 */
        Logger.getLogger("org").setLevel(Level.ERROR);

        SparkConf conf = new SparkConf().setAppName("a").setMaster("local[2]");
        /* 直接创建JavaSparkContext */
        JavaSparkContext jsc = new JavaSparkContext(conf);
        /* 先创建SparkSession，从SparkSession中获取JavaSparkContext，先创建SparkContext，SparkSession会用已存在的SparkContext*/
        SparkSession ss = SparkSession.builder()
                .config(conf).getOrCreate();
        JavaSparkContext sc = JavaSparkContext.fromSparkContext(ss.sparkContext());
        /* 设置JavaSparkContext的log等级 */
        sc.setLogLevel("ERROR");
        /* sql 所有配置参数 */
        ss.sql("SET -v").show(200, false);

        /* list to rdd  */
        List<SparkBean> beans = Lists.newArrayList();
        JavaRDD<SparkBean> beanRDD = jsc.parallelize(beans);
        /* bean to row */
        JavaRDD<Row> rowRDD = beanRDD.map(f -> RowFactory.create("1", "2"));
        /* rdd to dateframe -- 1 */
        ArrayList<StructField> fields = new ArrayList<>();
        StructField field = null;
        field = DataTypes.createStructField("sid", DataTypes.StringType, true);
        fields.add(field);
        field = DataTypes.createStructField("sname", DataTypes.StringType, true);
        fields.add(field);
        StructType schema = DataTypes.createStructType(fields);
        Dataset<Row> df = ss.createDataFrame(rowRDD, schema);
        /*  list/rdd to dateframe -- 2  */
        Dataset<Row> dataFrame1 = ss.createDataFrame(beans, SparkBean.class);
        Dataset<Row> dataFrame2 = ss.createDataFrame(rowRDD, SparkBean.class);
        /*  list to DataSet */
        Encoder<SparkBean> personEncoder = Encoders.bean(SparkBean.class);
        Dataset<SparkBean> dataSet = ss.createDataset(beans, personEncoder);

//        dataSet.write()
        /* parquet json csv jdbc Hive text*/
//                .format("com.databricks.spark.avro")
        /* 写入模式： Append 追加，Overwrite 覆盖，ErrorIfExists 存在报错，Ignore 忽略*/
//                .mode(SaveMode.Overwrite)
        /* 外部表 */
//                .option("path","/some/path")
//                .partitionBy("date","hour")   //  目录 date=2019-01-01,hour=12
//                .jdbc("url","table", new Properties() )
//                .saveAsTable("table")
//                .save("/Users/zhuowenwei/Desktop/udsH")
        ;
        //        jdbcDF.write
//                .format("jdbc")
//                .option("url", url)
//                .option("dbtable", "vulcanus_ljl.data_dict_temp1")
//                .option("user", "vulcanus_ljl")
//                .option("password", "mypassword")
//                .option("createTableColumnTypes", "dict_name varchar(60), dict_type varchar(60)") // 没有指定的字段使用默认的类型
//                .save()


    }
}
