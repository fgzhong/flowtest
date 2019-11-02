package com.mypro.kafka.util;

import com.mypro.kafka.ProducerMain;
import com.mypro.spark.stream.WebName;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class DouyuClient  implements Runnable{

    private final static Logger logger = LoggerFactory.getLogger(DouyuClient.class);
    private final static String login = "type@=loginreq/roomid@=%s/";
    private final static String keepLive = "type@=mrkl/";
    private final static String groupin = "type@=joingroup/rid@=%s/gid@=-9999/";

    private static final String socketIp = "openbarrage.douyutv.com";  //  124.95.155.50
    private final static Integer socketPort = 8601;
    private final static Integer liveTime = 40000;
    private final static String clostMag = "{\"type\":\"offonline\",\"timestamp\":\"%s\",\"hash\":\"1\",\"version\":\"%s\"}";

    private final static byte[] sendHeader = new byte[]{ (byte) 0xb1, 0x02, 0x00, 0x00};
    private final static byte end =  0x00;

    private int retry = 0;
    private volatile boolean readFlag = true;

    private Socket socket;
    private int zmid;
    private ProducerMain producer;
    private String version;
    private int offline;

    public DouyuClient(int zmid, ProducerMain producer, String version) {
        this.zmid = zmid;
        this.producer = producer;
        this.version = version;
    }

    @Override
    public void run() {
        socketLink();
    }

    public boolean offline() {
        offline++;
        if (producer != null) {
            producer.seedMessage(WebName.dy+"_"+zmid, String.format(clostMag,System.currentTimeMillis(),version));
        }
        if (offline >= 3) {
            return true;
        }
        return false;
    }

    public void stop() {
        readFlag = false;
    }

    public int getZmid() {
        return this.zmid;
    }

    private void socketLink() {
        try {

            logger.info("[DOUYU] link socket, zmid：{}, socketIp：{}", zmid, socketIp);
            socket = new Socket(socketIp, socketPort);

            OutputStream outputStream = socket.getOutputStream();
            sendMsg(outputStream, String.format(login, zmid));
            sendMsg(outputStream, String.format(groupin, zmid));

            logger.info("[DOUYU] socket数据接收开始，zmid：{}", zmid);
            DouyuHandler douyuHandler = new DouyuHandler(socket.getInputStream(), version);
            long startTime = System.currentTimeMillis();

            while (socket.isConnected() && readFlag) {
                String msg = douyuHandler.read();
                if (StringUtils.isNotBlank(msg)) {
//                    System.out.println(msg);
                    if (producer != null) {
                        producer.seedMessage(WebName.dy+"_"+zmid,msg);
                        producer.seedMessage(WebName.dy+"_"+zmid+"A",msg);
                        producer.seedMessage(WebName.dy+"_"+zmid+"B",msg);
                        producer.seedMessage(WebName.dy+"_"+zmid+"C",msg);
                        producer.seedMessage(WebName.dy+"_"+zmid+"D",msg);
                        producer.seedMessage(WebName.dy+"_"+zmid+"E",msg);
                        producer.seedMessage(WebName.dy+"_"+zmid+"F",msg);
                        producer.seedMessage(WebName.dy+"_"+zmid+"G",msg);
                        producer.seedMessage(WebName.dy+"_"+zmid+"H",msg);
                        producer.seedMessage(WebName.dy+"_"+zmid+"I",msg);
                        producer.seedMessage(WebName.dy+"_"+zmid+"J",msg);
                        producer.seedMessage(WebName.dy+"_"+zmid+"K",msg);
                        producer.seedMessage(WebName.dy+"_"+zmid+"L",msg);
                        producer.seedMessage(WebName.dy+"_"+zmid+"M",msg);
                        producer.seedMessage(WebName.dy+"_"+zmid+"N",msg);
                        producer.seedMessage(WebName.dy+"_"+zmid+"O",msg);
                        producer.seedMessage(WebName.dy+"_"+zmid+"P",msg);
                        producer.seedMessage(WebName.dy+"_"+zmid+"Q",msg);
                        producer.seedMessage(WebName.dy+"_"+zmid+"R",msg);
                        producer.seedMessage(WebName.dy+"_"+zmid+"S",msg);
                        producer.seedMessage(WebName.dy+"_"+zmid+"T",msg);
                        producer.seedMessage(WebName.dy+"_"+zmid+"U",msg);
                        producer.seedMessage(WebName.dy+"_"+zmid+"V",msg);
                        producer.seedMessage(WebName.dy+"_"+zmid+"W",msg);
                        producer.seedMessage(WebName.dy+"_"+zmid+"X",msg);
                        producer.seedMessage(WebName.dy+"_"+zmid+"Y",msg);
                        producer.seedMessage(WebName.dy+"_"+zmid+"Z",msg);
                        MsgProducer.count.incrementAndGet();
                    }
                }
                long endTime = System.currentTimeMillis();
                if (endTime - startTime > liveTime) {
//                    logger.info("[DOUYU] 发送心跳，zmid：{}", zmid);
                    sendMsg(outputStream, keepLive);
                    startTime = endTime;
                }
            }
        }catch (IOException e) {
            logger.warn("[DOUYU] socket连接失败, zmid：{}", zmid);
        }catch (Exception e) {
            logger.warn("[DOUYU] zmid：{} 其他未知错误：{} {}", zmid, e.getStackTrace(), e.getMessage());
        } finally {
            retryStep();
        }
    }

    private void sendMsg(OutputStream outputStream, String msg) throws IOException{
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        byte[] msgBytes  = msg.getBytes();
        Integer lenth = 9 + msgBytes.length;
        byteArray.write(intToBytes(lenth));
        byteArray.write(intToBytes(lenth));
        byteArray.write(sendHeader);
        byteArray.write(msgBytes);
        byteArray.write(end);
        outputStream.write(byteArray.toByteArray());
        byteArray.close();
    }

    private byte[] intToBytes( int value ) {
        byte[] src = new byte[4];
        src[3] =  (byte) ((value>>24) & 0xFF);
        src[2] =  (byte) ((value>>16) & 0xFF);
        src[1] =  (byte) ((value>>8) & 0xFF);
        src[0] =  (byte) (value & 0xFF);
        return src;
    }


    private void retryStep() {
        try {
            if (socket != null) {
                socket.close();
            }
        }catch (IOException e) {
            logger.error("[DOUYU] socket关闭失败, zmid：{}", zmid);
        }
        if (retry < 2 && readFlag){
            retry++;
            this.socketLink();
            return;
        }
        if (retry == 2) {
            logger.error("[DOUYU] socket连接失败，zmid：{}", zmid);
        }
        else {
            logger.info("[DOUYU] socket连接关闭，zmid：{}", zmid);
        }
    }

    public static void main(String[] args) throws Exception{
        for (int i=0; i<1;i++) {
            DouyuClient client = new DouyuClient(110, null,"1");
        }
    }

}
