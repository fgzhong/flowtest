package com.mypro.spark.stream;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mypro.spider.config.ESConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.ForeachWriter;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author fgzhong
 * @description: es sink 支持updata
 * @since 2019/7/22
 */
public class EsSink extends ForeachWriter<Row> {

    private final static Log LOG = LogFactory.getLog(EsSink.class);

    private List<Row> rows = Lists.newArrayList();
    private int batchSize = 10000;
    private boolean updata = false;

    private SparkConf conf;
    private RestHighLevelClient client;

    public EsSink(SparkConf conf, boolean updata) {
        this.conf = conf;
        this.updata = updata;
    }

//    version 是每个触发器增加的单调递增的 id
    @Override
    public boolean open(long partitionId, long version) {
        LOG.info(partitionId + " --- " + version);
        if (this.client == null) {
            LOG.info("--------------  client init  --------------");
            String clusterNodes = this.conf.get(ESConfig.NODES);
            ArrayList<HttpHost> hosts = Lists.newArrayList();
            for (String clusterNode : clusterNodes.split(",")) {
                String hostName = clusterNode.split(":")[0];
                String port = clusterNode.split(":")[1];
                hosts.add(new HttpHost(hostName, Integer.valueOf(port)));
            }
            this.client = new RestHighLevelClient(
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
            this.batchSize = this.conf.getInt("es.batch.size", batchSize);
            LOG.info("--------------  client init suc  --------------");
        }
        return true;
    }

    @Override
    public void process(Row row) {
        rows.add(row);
        if (rows.size() >= batchSize) {
            pushData();
        }
    }

    @Override
    public void close(Throwable errorOrNull) {
        try {
            if (errorOrNull != null) {
                errorOrNull.printStackTrace();
            }
            if (this.client != null) {
                LOG.info("--------------  client close  --------------");
                pushData();
                this.client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void pushData(){
        try {
            if (rows.size() > 0) {
                LOG.info("--------------  push data  -------------- " + rows.size());
                BulkRequest request = new BulkRequest();
                for (Row row : rows) {
                    Map<String, Object> js = row2json(row);
                    String data = JSON.toJSONString(js);
                    if (updata) {
                        request.add(new UpdateRequest("spark-dy", "_doc", js.get("id").toString())
//                                .id(js.get("id").toString())
                                .doc(data, XContentType.JSON)
                                .upsert(data, XContentType.JSON)
                                .docAsUpsert(true)
                        );
                    } else {
                        request.add(new IndexRequest().source(data, XContentType.JSON));
                    }
                }
                this.client.bulk(request, RequestOptions.DEFAULT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rows.clear();
        }
    }

    private static Map<String, Object> row2json(Row row) {
        Map<String, Object> json = Maps.newHashMap();
        StructType type = row.schema();
        if (type != null) {
            JSONArray fields = JSON.parseObject(type.json()).getJSONArray("fields");
            for (int i=0;i<fields.size();i++) {
                String key = fields.getJSONObject(i).getString("name");
                if (row.getAs(key) != null) {
                    json.put(key, row.getAs(key));
                }
            }
        } else {
            for (int i = 0; i < row.length(); i++) {
                json.put("_"+(i+1), row.get(i));
            }
        }
        return json;
    }
}
