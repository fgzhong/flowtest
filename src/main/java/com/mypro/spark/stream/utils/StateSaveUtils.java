package com.mypro.spark.stream.utils;

import com.google.common.collect.Lists;
import com.mypro.spark.stream.model.DmDataModel;
import com.mypro.spark.stream.model.Rank;
import com.mypro.spark.stream.state.DmState;
import com.mypro.spark.stream.state.GfState;
import com.mypro.spark.stream.model.GiftDataModel;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author fgzhong
 * @description: es 线程池
 * @since 2019/8/21
 */
public class StateSaveUtils implements Serializable, Closeable {
    private final static Logger LOG = LoggerFactory.getLogger(StateSaveUtils.class);

    private Queue<RestHighLevelClient> clientPool = new ConcurrentLinkedQueue<>();

    private static volatile StateSaveUtils instance;
    private String nodes;

    private StateSaveUtils(String nodes) {
        this.nodes = nodes;
    }

    public static StateSaveUtils getInstance(String nodes) {
        if (instance == null) {
            synchronized (StateSaveUtils.class) {
                if (instance == null) {
                    instance = new StateSaveUtils(nodes);
                }
            }
        }
        return instance;
    }

    public RestHighLevelClient getClient() {
        if (clientPool.isEmpty()) {
            return createClient();
        }
        return clientPool.poll();
    }

    private RestHighLevelClient createClient() {
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
        clientPool.add(client);
        return client;
    }

    private void rebackClient(RestHighLevelClient client) {
        clientPool.add(client);
    }


    public void pushData(List<GfState> states, String index, String partitionId) throws Exception{
        RestHighLevelClient client = getClient();
        try {
            LOG.info("--------------  push data {}  --   client size {} -------------- ", states.size(), clientPool.size());
            if (states.size() > 0) {
                BulkRequest request = new BulkRequest();
                GiftDataModel model = new GiftDataModel();
                for (GfState state : states) {
                    model.setWeb(state.getWeb());
                    model.setAchorId(state.getAchorId());
                    Map<String, BigDecimal> s = state.getState();
                    for (Map.Entry<String, BigDecimal> entry : s.entrySet()) {
                        model.setTime(entry.getKey());
                        model.setPc(entry.getValue().doubleValue());
                        model.setId(model.getWeb() + " " + model.getAchorId() + " " + model.getTime());
                        model.setUuid(model.getId() + " " + partitionId);
                        request.add(new UpdateRequest(index,"_doc",model.getId())
//                                .id(model.getId())
                                .script(new Script(
                                        ScriptType.INLINE, "painless",
                                        "if(ctx._source.uuid =='"+model.getUuid()+"')" +
                                                "{ctx.op='noop'}" +
                                                "else {ctx._source.pc =='"+model.getPc()+"'}" ,
                                        Collections.emptyMap()))
                                .upsert(model.toString(), XContentType.JSON)
                        );
                    }
                }
                if (request.requests().size() > 0) {
                    client.bulk(request, RequestOptions.DEFAULT);
                }
            }
        }  finally {
            rebackClient(client);
        }
    }


    public void pushDmData(List<DmState> states, String index, String partitionId) throws Exception{
        RestHighLevelClient client = getClient();
        try {
            LOG.info("--------------  push data {}  --   client size {} -------------- ", states.size(), clientPool.size());
            if (states.size() > 0) {
                BulkRequest request = new BulkRequest();
                DmDataModel model = new DmDataModel();
                for (DmState state : states) {
                    model.setWeb(state.getWeb());
                    model.setAchorId(state.getAchorId());
                    Map<String, Integer> s = state.getState();
                    for (Map.Entry<String, Integer> entry : s.entrySet()) {
                        model.setTime(entry.getKey());
                        model.setOnline(entry.getValue());
                        model.setId(model.getWeb() + " " + model.getAchorId() + " " + model.getTime());
                        model.setUuid(model.getId() + " " + partitionId);
                        request.add(new UpdateRequest(index+"-online","_doc",model.getId())
//                                .id(model.getId())
                                        .script(new Script(
                                                ScriptType.INLINE, "painless",
                                                "if(ctx._source.uuid =='"+model.getUuid()+"')" +
                                                        "{ctx.op='noop'}" +
                                                        "else {ctx._source.online =='"+entry.getValue()+"'}" ,
                                                Collections.emptyMap()))
                                        .upsert(model.toString(), XContentType.JSON)
                        );
                    }
                }
                request.add(new UpdateRequest(index+"-disonline","_doc",model.getId()+"-dis")
//                                .id(model.getId())
                                .script(new Script(
                                        ScriptType.INLINE, "painless",
                                        "if(ctx._source.uuid =='"+model.getUuid()+"')" +
                                                "{ctx.op='noop'}" +
                                                "else {ctx._source.online =='"+model.getDisOnline()+"'}" ,
                                        Collections.emptyMap()))
                                .upsert(model.toString(), XContentType.JSON)
                );


                if (request.requests().size() > 0) {
                    client.bulk(request, RequestOptions.DEFAULT);
                }
            }
        }  finally {
            rebackClient(client);
        }
    }

    public void pushGfRankData(List<Rank> states, String index, String partitionId) throws Exception{
        RestHighLevelClient client = getClient();
        try {
            LOG.info("--------------  push data {}  --   client size {} -------------- ", states.size(), clientPool.size());
            if (states.size() > 0) {
                BulkRequest request = new BulkRequest();
                for (Rank state : states) {
                    request.add(new IndexRequest().index(index+"-gfrank")
                            .type("_doc")
                            .source(state.toString(), XContentType.JSON)
                    );
                }
                if (request.requests().size() > 0) {
                    client.bulk(request, RequestOptions.DEFAULT);
                }
            }
        }  finally {
            rebackClient(client);
        }
    }

    @Override
    public void close() throws IOException {
        Iterator<RestHighLevelClient> pool = clientPool.iterator();
        while (pool.hasNext()) {
            pool.next().close();
        }
    }
}
