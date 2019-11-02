package com.mypro.spider.utils;

import com.google.common.collect.Lists;
import com.mypro.spider.parse.Content;
import com.mypro.spider.parse.Parse;
import com.mypro.spider.parse.ParseFactory;
import com.mypro.spider.protocol.ProtocolFactory;
import com.mypro.spider.protocol.httputil.HttpBase;
import com.mypro.spider.protocol.httputil.Response;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/2/27
 */
public class LocalHttpUtil {

    private final static String IP_URL = "http://maplecloudy.v4.dailiyun.com/query.txt?key=NPEE573347&word=&count=200&rand=true&detail=false&ltime=180";


    public static List<Object> getResult(Class<? extends HttpBase> httpClass, String url, Map<String, Object> map, Parse parse) throws Exception{
        map.put("parseClass", parse.getClass().getName());
        HttpBase httpBase = Records.newInstance(httpClass);
        Response response = httpBase.getLocalResponse(url, ExtendMapUtil.mapToExtendMap(map));
        Content content = Content.newContent(url, response.getContent(), map);
        content.setParseClass(parse.getClass().getName());
        return ParseFactory.getParse(content);
    }

    public static List<String> getProxys() throws IOException{
        String eString = Jsoup.connect(IP_URL).ignoreContentType(true).get().toString().trim();
        eString = eString.split("<body>")[1].split("</body>")[0].trim();
        String[] se = eString.split(" ");
        List<String> proxyList = Lists.newArrayList();
        for (String aSe : se) {
            proxyList.add(aSe.split(",")[0]);
        }
        return proxyList;
    }

}
