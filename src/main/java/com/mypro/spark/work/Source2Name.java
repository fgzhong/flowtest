package com.mypro.spark.work;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/3/25
 */
public class Source2Name {

    private final static FastDateFormat dateFormat =FastDateFormat.getInstance("yyyy-MM-dd", Locale.CHINA);

    public static void main(String[] args) {

        Map<String, String> map = Maps.newHashMap();
        map.put("1","游戏中心");
        map.put("2","管家");
        map.put("3","管家");
        map.put("4","乐窗");
        map.put("5","悬浮球");
        map.put("6","开机助手");
        map.put("7","页游平台");
        map.put("8","智慧联想");
        map.put("9","vip系统");
        map.put("10","智慧联想-腾讯版");
        map.put("11","开机助手-腾讯版");
        map.put("12","游戏中心官网");
        map.put("13","直播侧边栏");
        map.put("14","报错页-游戏");
        map.put("15","PC-联想视频客户端");
        map.put("16","直播导航页");
        map.put("17","导航页-侧边栏");
        map.put("18","导航页-推荐位");
        map.put("19","中国区");
        map.put("20","游戏社区");
        map.put("21","活动");
        map.put("22","web-联想视频");
        map.put("23","SIOT开机");
        map.put("24","智慧联想广告位");
        map.put("25","游戏中心小新版");
        map.put("26","报错页面-直播");


        SparkConf conf = new SparkConf().setAppName("a").setMaster("local[2]");
        SparkSession spark = SparkSession.builder()
                .config(conf).getOrCreate();
        JavaSparkContext sc = JavaSparkContext.fromSparkContext(spark.sparkContext());

        Dataset<Row> data = spark.read().format("jdbc").option("url", "jdbc:mysql://mc.ali.bjol.bigdata.udsp.com:3306/uds?useUnicode=true&characterEncoding=utf-8&useSSL=false&autoReconnect=true")
                .option("driver", "com.mysql.cj.jdbc.Driver").option("dbtable", "uds_fdm_game_log_di")
                .option("user", "uds").option("password", "rsjxfyyyzf2018").load();

        data.createOrReplaceTempView("video");

        Dataset<Row> source = spark.sql("select * from video");

        Dataset<Model> changeSource = source.map(new MapFunction<Row, Model>() {
            @Override
            public Model call(Row value) throws Exception {
                Model model = new Model();
                model.setAppId(value.getString(0));
                model.setQuotaId(value.getString(1));
                model.setLog_date(value.getTimestamp(3).toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.CHINA)));
                model.setResult(value.getDouble(4));
                if (value.get(5) != null) {
                    model.setFrom_source(map.get(value.getString(5)));
                }
                return model;
            }
        }, Encoders.bean(Model.class));

        changeSource.write().format("jdbc").option("url", "jdbc:mysql://mc.ali.bjol.bigdata.udsp.com:3306/uds?useUnicode=true&characterEncoding=utf-8&useSSL=false&autoReconnect=true")
                .option("driver", "com.mysql.cj.jdbc.Driver")
                .option("dbtable", "uds_fdm_game_log_di_5")
                .option("user", "uds").option("password", "rsjxfyyyzf2018")
                .save()
        ;
        changeSource.show();
    }

}
