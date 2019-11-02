package com.mypro.spark.stream.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.mypro.spark.stream.model.DyGiftModel;
import com.mypro.spark.stream.model.Rank;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/7/25
 */
public class AddGiftToEs {

    private static String daili = "http://maplecloudy.v4.dailiyun.com/query.txt?key=NPEE573347&word=&count=200&rand=true&detail=false";
    private static String dylist = "http://www.douyu.com/gapi/rkc/directory/0_0/%s";
    private final static String DY_URL = "http://open.douyucdn.cn/api/RoomApi/room/";
    private final static String DY_GF_URL = "http://webconf.douyucdn.cn//resource/common/prop_gift_list/prop_gift_config.json";


//    public static void main(String[] args) throws Exception{
//        String nodes = "es1.ali.szol.bds.com:9200";
//        ArrayList<HttpHost> hosts = Lists.newArrayList();
//        for (String clusterNode : nodes.split(",")) {
//            String hostName = clusterNode.split(":")[0];
//            String port = clusterNode.split(":")[1];
//            hosts.add(new HttpHost(hostName, Integer.valueOf(port)));
//        }
//        RestHighLevelClient client = new RestHighLevelClient(
//                RestClient.builder(hosts.toArray(new HttpHost[hosts.size()]))
//                        .setRequestConfigCallback(
//                                new RestClientBuilder.RequestConfigCallback() {
//                                    @Override
//                                    public RequestConfig.Builder customizeRequestConfig(
//                                            RequestConfig.Builder requestConfigBuilder) {
//                                        return requestConfigBuilder.setConnectTimeout(5000)
//                                                .setSocketTimeout(10000)
//                                                .setMaxRedirects(3)
//                                                .setConnectionRequestTimeout(10000);
//                                    }
//                                }));
//        BulkRequest request = new BulkRequest();
//        String gfStr = Jsoup.connect(DY_GF_URL).ignoreContentType(true).get().toString();
//        JSONObject dygf1 = JSONObject.parseObject(gfStr.substring(gfStr.indexOf("{"), gfStr.lastIndexOf("}")+1)).getJSONObject("data");
//        for(Map.Entry<String, Object> key2value : dygf1.entrySet()) {
//            DyGiftModel model = new DyGiftModel();
//            model.setId(key2value.getKey());
//            model.setName(((JSONObject)key2value.getValue()).getString("name"));
//            model.setPc(((JSONObject)key2value.getValue()).getInteger("pc")/100);
//            request.add(new IndexRequest("spark-dy-gfit","_doc")
//                    .id(key2value.getKey())
//                    .source(JSON.toJSONString(model), XContentType.JSON)
//            );
//        }
//
//        client.bulk(request, RequestOptions.DEFAULT);
//        client.close();
//    }

    public static void main(String[] args) throws Exception{
        String nodes = "es1.ali.szol.bds.com:9200";
        ArrayList<HttpHost> hosts = Lists.newArrayList();
        for (String clusterNode : nodes.split(",")) {
            String hostName = clusterNode.split(":")[0];
            String port = clusterNode.split(":")[1];
            hosts.add(new HttpHost(hostName, Integer.valueOf(port)));
        }
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(hosts.toArray(new HttpHost[hosts.size()]))
                        .setRequestConfigCallback(
                                new RestClientBuilder.RequestConfigCallback() {
                                    @Override
                                    public RequestConfig.Builder customizeRequestConfig(
                                            RequestConfig.Builder requestConfigBuilder) {
                                        return requestConfigBuilder.setConnectTimeout(5000)
                                                .setSocketTimeout(10000)
                                                .setMaxRedirects(3)
                                                .setConnectionRequestTimeout(10000);
                                    }
                                }));
        BulkRequest request = new BulkRequest();
        Rank model = new Rank();
        model.setTotalonline(1);
        model.setWeb("1");
        request.add(new IndexRequest().index("test"+"-gfrank")
                .type("_doc")
                .source(model.toString(), XContentType.JSON)
        );
        client.bulk(request, RequestOptions.DEFAULT);
        client.close();
    }


}
