package com.mypro.kafka.util;

import com.google.common.collect.Maps;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class DouyuHandler {
    private InputStream inputStream;
    private Gson gson = new Gson();
    private String version;

    public DouyuHandler(InputStream inputStream, String version) {
        this.inputStream = inputStream;
        this.version = version;
    }

    /**
     * @return 返回有意义的json串
     * @throws IOException
     */
    public String read() throws IOException {

        int contentLen = 0;
        //读取4个字节，得到数据长度
        byte[] Lengthbytes = new byte[4];
        for (int i = 0;i < 4;i++) {
            int ss = inputStream.read();
            Lengthbytes[i] = (byte) ss;
        }
        contentLen = bytesToInt(Lengthbytes);

        //越过前面没用的字节，跳到标记内容长度的字节
        for (int i = 0;i < 8;i++) {
            inputStream.read();
        }
        int len;
        int readLen = 0;
        contentLen -= 8;
        byte[] bytes = new byte[contentLen];
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        while ((len = inputStream.read(bytes,0,contentLen - readLen)) != -1) {
            byteArray.write(bytes,0,len);
            readLen += len;
            if (readLen == contentLen) {
                break;
            }
        }
        String dd = byteArray.toString();
        Map<String, String> data = Maps.newHashMap();
        String[] strs = dd.substring(0,dd.length()-2).split("/");
        for (String str:strs) {
            String[] map = str.replaceAll("@S", "/").split("@=");
            if (map.length == 2) {
                data.put(map[0], map[1]);
            }
        }
        data.put("timestamp", System.currentTimeMillis()+"");
        data.put("hash", dd.hashCode() + "");
        data.put("version", version);
        return gson.toJson(data);

    }

    private int bytesToInt(byte[] src) {
        int value;
        value = (int) ((src[0] & 0xFF)
                | ((src[1] & 0xFF)<<8)
                | ((src[2] & 0xFF)<<16)
                | ((src[3] & 0xFF)<<24));
        return value;
    }

}
