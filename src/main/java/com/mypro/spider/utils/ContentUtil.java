package com.mypro.spider.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.mypro.spider.parse.Content;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/3/31
 */
public class ContentUtil {

    public static Content format(String content) throws UnsupportedEncodingException {
        int htmlIndex = content.indexOf("\"html\":");
        int htmlLastIndex = content.lastIndexOf(",\"data\":");
        String data = content.substring(htmlLastIndex+8);
        String s = content.substring(0,htmlIndex) + "}";
        Content con = JSONObject.parseObject(s, Content.class);
        con.setHtml(content.substring(htmlIndex+7,htmlLastIndex).getBytes());
        con.setData(mapStringToMap(data.substring(0,data.length()-1)));
        return con;
    }

    public static Map mapStringToMap(String str){
        if ("null".equals(str)) {
            return null;
        }
        str=str.substring(1, str.length()-1);
        String[] strs=str.split(",");
        Map<String,String> map = new HashMap<String, String>();
        for (String string : strs) {
            String key=string.split("=")[0];
            String value=string.split("=")[1];
            map.put(key, value);
        }
        return map;
//        return JSONObject.toJSON(map).toString().replaceAll("\"","\\\\\"");
    }

}
