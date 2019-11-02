package com.mypro.spark.stream.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.mypro.spark.stream.model.DyGiftModel;
import org.jsoup.Jsoup;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/8/23
 */
public class EsSearchUtil {

    private final static String DY_GIFT = "http://%s/spark-dy-gfit/_search?q=id:%s";
    private final static String DY_ONLINE = "http://%s/spark-dy-online/_search?q=id:%s";


    public static DyGiftModel getGiftModel(String node, String id) {
        try {
            String result =  Jsoup.connect(String.format(DY_GIFT,node, id))
                    .ignoreContentType(true).get().toString();
            JSONArray data = JSON.parseObject(
                    result.substring(result.indexOf("{"), result.lastIndexOf("}")+1)
            ).getJSONObject("hits").getJSONArray("hits");
            if (data.size() == 0) return null;
            return JSON.parseObject(
                    data.getJSONObject(0).getJSONObject("_source").toJSONString(),
                    DyGiftModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(getGiftModel("localhost:9200", "20456"));;
    }

}
