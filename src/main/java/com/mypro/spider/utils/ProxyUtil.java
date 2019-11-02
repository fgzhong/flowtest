package com.mypro.spider.utils;

import com.google.common.collect.Lists;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.List;

/**
 * @author fgzhong
 * @since 2019/3/6
 */
public class ProxyUtil {

    private final static String IP_URL = "http://maplecloudy.v4.dailiyun.com/query.txt?key=NPEE573347&word=&count=200&rand=true&detail=false";

    public static List<String> getList() throws IOException{
        String eString = Jsoup.connect(IP_URL).ignoreContentType(true).timeout(120000).get().toString().trim();
        eString = eString.split("<body>")[1].split("</body>")[0].trim();
        String[] se = eString.split(" ");
        List<String> proxyList = Lists.newArrayList();
        for (String aSe : se) {
            String e = aSe.split(",")[0];
            if (e.contains(":")) {
                proxyList.add(aSe.split(",")[0]);
            }
        }
        return proxyList;
    }
}
