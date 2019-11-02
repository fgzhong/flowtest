package com.mypro.spark.work;

import com.google.common.collect.Lists;
import com.mypro.spark.model.GameStatModel;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/3/10
 */
public class GameStatJob {

    private static DecimalFormat df=new DecimalFormat("0.0000");


    public static void main(String[] args) throws Exception{
        Logger.getLogger("org").setLevel(Level.ERROR);
        SparkConf conf = new SparkConf().setAppName("a").setMaster("local[2]");
        conf.set("spark.sql.sources.partitionColumnTypeInference.enabled", "false");
        SparkSession ss = SparkSession.builder().config(conf).getOrCreate();

        Dataset<Row> gameRow = ss.read()
                .format("parquet")
                .load("/Users/zhuowenwei/Desktop/udsL")
//                .filter("where data <= ? and data >= ?")
                ;
        /* session终止前 dropTempView  */
        gameRow.createOrReplaceTempView("game");
        /* 应用程序终止前 dropGlobalTempView*/
        gameRow.createOrReplaceGlobalTempView("tempViewName");
        /* SELECT * FROM game WHERE appId in (1,2,4) where data <= ? and data >= ? */
        Dataset<Row> gamePcRow = ss.sql("SELECT * FROM game WHERE appId in (1,2,4)");
        gamePcRow.createOrReplaceTempView("game_pc");
        Dataset<Row> gameWebRow = ss.sql("SELECT * FROM game WHERE appId in (3,5,6,7,8,9)");
        gameWebRow.createOrReplaceTempView("game_web");
        gamePcRow.show(10);
        gameWebRow.show(10);

        /* 日活跃 */
        /* 线上SQL SELECT date,count(DISTINCT deviceId) as num FROM game_web where data=? */
        Dataset<Row> pcRow1 = ss.sql("SELECT date,count(DISTINCT deviceId) as num FROM game_pc GROUP BY date");
        pcRow1.show(10);
        proRow(pcRow1, "pc", "Day_active");
        Dataset<Row> webRow1 = ss.sql("SELECT date,count(DISTINCT deviceId) as num FROM game_web GROUP BY date");
        webRow1.show(10);
        proRow(pcRow1, "web", "Day_active");

        /* 月活跃 */
        /*  SELECT CONCAT(year,'-',month,'-','01') as time,count(DISTINCT deviceId) as num FROM game_web where data >= ? and data <= ?*/
        Dataset<Row> pcRow2 = ss.sql("SELECT CONCAT(year,'-',month,'-','01') as time,count(DISTINCT deviceId) as num FROM game_pc GROUP BY year,month");
        pcRow2.show(10);
        proRow(pcRow2, "pc", "Month_active");
        Dataset<Row> webRow2 = ss.sql("SELECT CONCAT(year,'-',month,'-','01') as time,count(DISTINCT deviceId) as num FROM game_web GROUP BY year,month");
        webRow2.show(10);
        proRow(webRow2, "web", "Month_active");

        /* 日激活 */
        /* 加载中间表 user */
        /*  select date,count(*) as num from (SELECT deviceId,min(date) as date FROM (SELECT deviceId,TO_DATE(date, 'yyyy-MM-dd') as date FROM game_pc) as a GROUP BY deviceId) group by date  */
        Dataset<Row> pcRow3 = ss.sql("select date,count(*) as num from (SELECT deviceId,min(date) as date FROM (SELECT deviceId,TO_DATE(date, 'yyyy-MM-dd') as date FROM game_pc) as a GROUP BY deviceId) group by date");
        pcRow3.show(10);
        proRow(pcRow3, "pc", "Increase_user");
        Dataset<Row> webRow3 = ss.sql("select date,count(*) as num from (SELECT deviceId,min(date) as date FROM (SELECT deviceId,TO_DATE(date, 'yyyy-MM-dd') as date FROM game_web) as a GROUP BY deviceId) group by date");
        webRow3.show(10);
        proRow(webRow3, "web", "Increase_user");

        /* 用户渠道 */
        /* SELECT date,count(DISTINCT deviceId) as num,source FROM game_pc where source is NOT null and data = ? */
        Dataset<Row> pcRow4 = ss.sql("SELECT date,count(DISTINCT deviceId) as num,source FROM game_pc where source is NOT null GROUP BY source,date");
        pcRow4.show(10);
        proRow(pcRow4, "pc", "Source_user");
        Dataset<Row> webRow4 = ss.sql("SELECT date,count(DISTINCT deviceId) as num,source FROM game_web where source is NOT null GROUP BY source,date");
        webRow4.show(10);
        proRow(webRow4, "web", "Source_user");

        /* 留存率 */
        /* SELECT min(first_day) first_day, sum(case when by_day = 0 then 1 else 0 end) day_0, sum(case when by_day = 1 then 1 else 0 end) day_1, sum(case when by_day = 7 then 1 else 0 end) day_7 FROM (SELECT deviceId, date, first_day, DATEDIFF(date,first_day) as by_day FROM (SELECT b.deviceId, b.date, c.first_day FROM (SELECT deviceId, TO_DATE(date, 'yyyy-MM-dd') as date FROM game_pc where data <= ? and where data >= ? GROUP BY deviceId,date) as b LEFT JOIN (SELECT deviceId, min(date) as first_day FROM (SELECT deviceId, TO_DATE(date, 'yyyy-MM-dd') as date FROM game_pc where data <= ? and where data >= ? GROUP BY deviceId,date) as a GROUP BY deviceId) as c ON b.deviceId = c.deviceId) e) AS f */
        Dataset<Row> pcRow5 = ss.sql("SELECT min(first_day) first_day, sum(case when by_day = 0 then 1 else 0 end) day_0, sum(case when by_day = 1 then 1 else 0 end) day_1, sum(case when by_day = 7 then 1 else 0 end) day_7 FROM (SELECT deviceId, date, first_day, DATEDIFF(date,first_day) as by_day FROM (SELECT b.deviceId, b.date, c.first_day FROM (SELECT deviceId, TO_DATE(date, 'yyyy-MM-dd') as date FROM game_pc GROUP BY deviceId,date) as b LEFT JOIN (SELECT deviceId, min(date) as first_day FROM (SELECT deviceId, TO_DATE(date, 'yyyy-MM-dd') as date FROM game_pc GROUP BY deviceId,date) as a GROUP BY deviceId) as c ON b.deviceId = c.deviceId) e) AS f ");
        pcRow5.show(10);
        proRowForV1(pcRow5, "pc");
        Dataset<Row> webRow5 = ss.sql("SELECT min(first_day) first_day, sum(case when by_day = 0 then 1 else 0 end) day_0, sum(case when by_day = 1 then 1 else 0 end) day_1, sum(case when by_day = 7 then 1 else 0 end) day_7 FROM (SELECT deviceId, date, first_day, DATEDIFF(date,first_day) as by_day FROM (SELECT b.deviceId, b.date, c.first_day FROM (SELECT deviceId, TO_DATE(date, 'yyyy-MM-dd') as date FROM game_web GROUP BY deviceId,date) as b LEFT JOIN (SELECT deviceId, min(date) as first_day FROM (SELECT deviceId, TO_DATE(date, 'yyyy-MM-dd') as date FROM game_web GROUP BY deviceId,date) as a GROUP BY deviceId) as c ON b.deviceId = c.deviceId) e) AS f ");
        webRow5.show(10);
        proRowForV1(webRow5, "web");

        ss.stop();
    }

    private static void proRow(Dataset<Row> row, String appId, String quotaId) {
        FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd", Locale.CHINA);
        row.foreachPartition(f -> {
            List<GameStatModel> models = Lists.newArrayList();
            f.forEachRemaining(f1 -> {
                GameStatModel model = new GameStatModel();
                model.setAppId(appId);
                model.setQuotaId(quotaId);
                if (f1.get(0) instanceof Date) { model.setlogDate(dateFormat.format(f1.getDate(0)));}
                else { model.setlogDate(f1.getString(0));}
                model.setResult(f1.getLong(1)+"");
                if ("Source_user".equals(quotaId)) {
                    model.setFromSource(f1.getString(2));
                }
                models.add(model);
            });
            addDateIntoMysql(models);
        });
    }

    private static void proRowForV1(Dataset<Row> row, String appId) {
        FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd", Locale.CHINA);
        row.foreachPartition(f -> {
            List<GameStatModel> models = Lists.newArrayList();
            f.forEachRemaining(f1 -> {
                String date = dateFormat.format(f1.getDate(0));
                long fristDay = f1.getLong(1);
                long oneDay = f1.getLong(2);
                long sevenDay = f1.getLong(3);
                GameStatModel model1 = new GameStatModel();
                model1.setAppId(appId);
                model1.setQuotaId("Oneday_retention");
                model1.setlogDate(date);
                model1.setResult(oneDay+"");
                models.add(model1);
                GameStatModel model2 = new GameStatModel();
                model2.setAppId(appId);
                model2.setQuotaId("Oneday_retention_rate");
                model2.setlogDate(date);
                model2.setResult((df.format((float)oneDay/fristDay))+"");
                System.out.println(model2.getResult());
                models.add(model2);
                GameStatModel model3 = new GameStatModel();
                model3.setAppId(appId);
                model3.setQuotaId("Sevenday_retention");
                model3.setlogDate(date);
                model3.setResult(sevenDay+"");
                models.add(model3);
                GameStatModel model4 = new GameStatModel();
                model4.setAppId(appId);
                model4.setQuotaId("Sevenday_retention_rate");
                model4.setlogDate(date);
                model4.setResult((df.format((float)sevenDay/fristDay))+"");
                System.out.println(model4.getResult());
                models.add(model4);

            });
            addDateIntoMysql(models);
        });
    }



    private static void addDateIntoMysql(List<GameStatModel> models) throws SQLException{
        Connection conn = DriverManager.getConnection("jdbc:mysql://mc.ali.bjol.bigdata.udsp.com:3306/uds?useUnicode=true&characterEncoding=utf-8&useSSL=false&autoReconnect=true","uds","rsjxfyyyzf2018");
        conn.setAutoCommit(false);
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO uds_fdm_game_log_di (appId,quotaId,source,log_date,result,from_source) VALUES (?,?,?,?,?,?)");
        try {
            for (GameStatModel model : models) {
                pstmt.setString(1, model.getAppId());
                pstmt.setString(2, model.getQuotaId());
                pstmt.setString(3, model.getSource());
                pstmt.setString(4, model.getlogDate());
                pstmt.setString(5, model.getResult());
                pstmt.setString(6, model.getFromSource());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pstmt != null) { pstmt.close();}
            if (conn != null) { conn.close();}
        }
    }


}
