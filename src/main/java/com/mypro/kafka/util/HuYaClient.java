package com.mypro.kafka.util;


import com.google.gson.Gson;
import okhttp3.*;
import okhttp3.internal.ws.RealWebSocket;
import okio.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.EOFException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/7/31
 */

public class HuYaClient {

    private final static Logger logger = LoggerFactory.getLogger(HuYaClient.class);

    private final static String socketUrl = "wss://live-ws-pg.kuaishou.com/websocket";

    private final static String socketTokenUrl = "https://fx1.service.kugou.com/socket_scheduler/pc/v2/address.jsonp?_p=0&_v=7.0.0&pv=20171111&rid=%s&cid=100&at=101&_=%s";
    //    ws://chat1.KUAISHOU.kugou.com:1314/
    private final static String linkMsg = "{\"type\":\"CS_ENTER_ROOM\",\"payload\":{\"liveStreamId\":\"%s\",\"token\":\"tH9q3ZpxqUFp7+B+S3jTn/Pj6YFZTIoFRIb1tXPxcTAfbFlZFLjx4y6x/vUXO4IXMbl9yCmGo8mqNilfOIclhg==\",\"pageId\":\"N5Q98X9rcBNyBGkl_%s\"}}";

    private final static String keepLive = "{\"type\":\"CS_HEARTBEAT\",\"pageId\":\"WU9mojDvtmBmOAEi_%s\"}";


    private final static OkHttpClient client = new OkHttpClient.Builder().readTimeout(1000, TimeUnit.MILLISECONDS).build();

    private int retry = 0;
    private Long linkTime;
    private int zmid;
    private String live;

    public HuYaClient(int zmid, String live ) {
        this.zmid = zmid;
        this.live = live;
    }

    void socketLink() {
        try {
            Request request = new Request.Builder()
                    .url(socketUrl)
                    .build();
            new RealWebSocket(request, new Listener(), new SecureRandom(), 0).connect(client);
        }catch (Exception e) {
            logger.warn("[HUYA] zmid：{} 其他未知错误：{} {}", zmid, e.getStackTrace(), e.getMessage());
            if (retry < 2) {
                retry++;
                socketLink();
            }
        }

    }

    private class Listener extends WebSocketListener {

        private Long strTime;
        Gson gson = new Gson();

        @Override
        public void onOpen(final WebSocket webSocket, Response response) {
            strTime = System.currentTimeMillis();
            logger.info("[HUYA] link socket, zmid：{}", zmid);
            webSocket.send(String.format(linkMsg, zmid, System.currentTimeMillis()));
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            long endTime = System.currentTimeMillis();
            if (endTime - strTime >= 20000) {
                webSocket.send(String.format(keepLive, endTime));
                strTime = endTime;
            }
            System.out.println(text);
        }



        @Override
        public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
            logger.error("[HUYA] zmid：{}, 其他未知错误：{} {}", zmid, t.getStackTrace(), t.getMessage());
            webSocket.close(3000, "dd");

        }
    }


    public static void main(String[] args) throws Exception{
        HuYaClient client = new HuYaClient(225214073, "qTRd7OF_Jxw");
//            ProxyPool.proxyQueue.add(new Direct());
        new Thread(()->client.socketLink()).start();
    }
}
