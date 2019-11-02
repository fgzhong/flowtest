package com.mypro.kafka.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mypro.kafka.KafkaConstant;
import com.mypro.kafka.ProducerMain;
import com.mypro.spark.stream.model.DyGiftModel;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author fgzhong
 * @description: 实时数据生产
 * @since 2019/7/16
 */
public class MsgProducer implements Closeable{

    private final static Logger LOG = LoggerFactory.getLogger(MsgProducer.class);


    private final static String DAILI = "http://maplecloudy.v4.dailiyun.com/query.txt?key=NPEE573347&word=&count=200&rand=true&detail=false";
    private final static String DY_URL = "http://open.douyucdn.cn/api/RoomApi/room/";

    public final static AtomicLong count = new AtomicLong(0);
    private List<String> proxyList = Collections.synchronizedList(Lists.newArrayList());
    private Map<Integer, DouyuClient> clientMap = Maps.newConcurrentMap();
    private ScheduledExecutorService timePool;
    private ThreadPoolExecutor clientPool;
    public String version;


    private void startTimer(int start, int end, ProducerMain producerMain) {
        timePool.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    String eString = Jsoup.connect(DAILI).ignoreContentType(true).get()
                            .toString().trim();
                    eString = eString.split("<body>")[1].split("</body>")[0].trim();
                    String[] se = eString.split(" ");
                    if (eString.length() > 10) {
                        proxyList.clear();
                        for (int i = 0; i < se.length; i++) {
                            proxyList.add(se[i].split(",")[0]);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LOG.info(" ----------  get proxy {} --------------", proxyList.size());
            }
        }, 0, 3 , TimeUnit.MINUTES);
        timePool.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                clientListener();
                clientInit(start, end, producerMain);
            }
        }, 0, 5 , TimeUnit.MINUTES);
    }

    private void initPool() {
        timePool = new ScheduledThreadPoolExecutor(2,
                new BasicThreadFactory.Builder().namingPattern("time-pool-%d").daemon(true).build());
        clientPool = new ThreadPoolExecutor(300, 300,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),new ThreadFactoryBuilder()
                .setNameFormat("client-pool-%d").build());
    }

    public String getProxy() {
        if (proxyList.size() > 0) {
            return proxyList.get(ThreadLocalRandom.current().nextInt(proxyList.size()));
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        timePool.shutdown();
        clientPool.shutdown();
    }

    private void clientListener() {
        LOG.info(" ----------  online check {} --------------", clientMap.size());
        List<Integer> removeClient = Lists.newArrayList();
        for (Map.Entry<Integer, DouyuClient> entry : clientMap.entrySet()) {
            try {
                DouyuClient client = entry.getValue();
                Connection connection = Jsoup
                        .connect(DY_URL + client.getZmid());
                String ip2port = getProxy();
                if (ip2port != null) {
                    connection.proxy(ip2port.split(":")[0], Integer.valueOf(ip2port.split(":")[1]));
                }
                JSONObject data = JSON.parseObject(connection.ignoreContentType(true).timeout(1000).get().body().text()).getJSONObject("data");
                // 1 开播
                if (data.getInteger("room_status") != 1) {
                    if (client.offline()) {
                        client.stop();
                        removeClient.add(entry.getKey());
                    }
                }
            } catch (Exception e) {
//                LOG.error(" ----  link error : {} -----", e.getMessage());
            }
        }
        removeClient.forEach(f -> clientMap.remove(f));
    }

    private void clientInit(int start, int end, ProducerMain main) {
        for (int k=start; k<=end; k++) {
            try {
                String info = Jsoup.connect("http://www.douyu.com/gapi/rkc/directory/0_0/"+k)
                        .ignoreContentType(true).get().body().text();
                JSONObject jsonObject = JSON.parseObject(info);
                JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("rl");
                for (int i=0;i<jsonArray.size();i++) {
                    JSONObject zbDetail = jsonArray.getJSONObject(i);
                    Integer zmid = zbDetail.getInteger("rid");
                    if (!clientMap.containsKey(zmid) && clientMap.size() < 300) {
                        System.out.println(i);
                        DouyuClient client = new DouyuClient(zmid, main, version);
                        clientPool.execute(client);
                        clientMap.put(zmid, client);
                        clientGift(zmid);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        LOG.info(" ----------  online listener {} , count {}--------------", clientMap.size(), count.get());
    }

    private void clientGift(int zmid) {
        try {
            JSONObject data = JSON.parseObject(Jsoup.connect(DY_URL + zmid).ignoreContentType(true).get().body().text()).getJSONObject("data");
            JSONArray gfArray = data.getJSONArray("gift");
            for (int i=0; i<gfArray.size(); i++) {
                JSONObject gf = gfArray.getJSONObject(i);
                DyGiftModel model = new DyGiftModel();
                model.setName(gf.getString("name"));
                model.setId(gf.getString("id"));
                model.setPc(gf.getDouble("pc"));
                try {
                    Jsoup.connect(String.format("http://%s/spark-dy-gfit/_doc/%s", KafkaConstant.ES_HOST, model.getId()))
                            .header("Content-Type", "application/json")
                            .ignoreContentType(true)
                            .requestBody(model.toString()).post();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{
        if (args == null || args.length == 0) {
            args = new String[]{"1","1","2"};
        }
        MsgProducer msgProducer = new MsgProducer();
        msgProducer.version = args[2];
        msgProducer.initPool();
        msgProducer.startTimer(Integer.valueOf(args[0]), Integer.valueOf(args[1]), new ProducerMain());
        TimeUnit.SECONDS.sleep(20);
    }


}
