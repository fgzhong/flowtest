package com.mypro.spider.example.example2pro.parse;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.mypro.spider.data.ExtendMap;
import com.mypro.spider.example.example2pro.model.ReviewModel;
import com.mypro.spider.example.example2pro.model.WeiboModel;
import com.mypro.spider.parse.Content;
import com.mypro.spider.parse.Outlink;
import com.mypro.spider.parse.Parse;
import com.mypro.spider.protocol.ProtocolFactory;
import com.mypro.spider.protocol.ProtocolOutput;
import com.mypro.spider.protocol.httputil.jsoup.Jsoups;
import com.mypro.spider.utils.ExtendMapUtil;
import com.mypro.spider.utils.ToolUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/1/28
 */
public class WeiBoParse implements Parse {


    private static final String HOME_PAGE = "https://weibo.com/[a-zA-Z0-9]+"; // 2403484123
    private static final String USER_ID = "userId";

    /*
//  private static final String HOME_PAGE_1 =     "https://weibo.com/lizexipablo\\?page=\\GenericWritableConfigurable+&total=\\GenericWritableConfigurable+";  // 2403484123
//  private static final String PERSON_INFO =     "https://m.weibo.cn/api/container/getIndex?type=uid&value=%s&containerid=100505%s";
    */
    private static final String LIST_PAGE = "https://m.weibo.cn/api/container/getIndex?type=uid&value=%s&containerid=107603%s";
    private static final String LIST_PAGE_1 = "https://m.weibo.cn/api/container/getIndex\\?type=uid&value=\\d+&containerid=107603\\d+";

    private static final String LIST_ALL_PAGE = "https://m.weibo.cn/api/container/getIndex?type=uid&value=%s&containerid=107603%s&page=%s";
    private static final String LIST_ALL_PAGE_1 = "https://m.weibo.cn/api/container/getIndex\\?type=uid&value=\\d+&containerid=107603\\d+&page=\\d+";

    public static final String CONTENT = "https://m.weibo.cn/status/%s";
    public static final String CONTENT_1 = "https://m.weibo.cn/status/\\d+";

    private static final String VIDEO_URL = "https://m.weibo.cn/status/%s#&video";

    @Deprecated
    private static final String FiRST_COMMENT = "https://weibo.com/aj/v6/comment/big?ajwvr=6&id=%s&filter=all&from=singleWeiBo";
    private static final String NEW_FiRST_COMMENT = "https://weibo.com/aj/v6/comment/big?ajwvr=6&id=%s&filter=all&page=1&sudaref=weibo.com&display=0&retcode=6102";
    private static final String NEW_FiRST_COMMENT_REGEX = "https://weibo.com/aj/v6/comment/big\\?ajwvr=6&id=\\d+&filter=all&page=1&sudaref=weibo.com&display=0&retcode=6102";
    public static final String NEW_COMMENT_REGEX = "http://weibo.com/aj/v6/comment/big\\?ajwvr=6&id=\\d+&filter=all&page=\\d+&sudaref=weibo.com&display=0&retcode=6102";
    private static final String LAST_COMMENT = "https://weibo.com/aj/v6/comment/big?%s";

    private static final String PARAM_1 = "%s&from=singleWeiBo";
    private static final String PARAM_2 = "ajwvr=6&%s&from=singleWeiBo";

    private static final ExtendMap PARAM = new ExtendMap();

    private final static SimpleDateFormat FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm");
    private final static SimpleDateFormat FORMAT1 = new SimpleDateFormat(
            "yyyy-MM-dd");
    private static final Logger _LOG = LoggerFactory
            .getLogger(MethodHandles.lookup().lookupClass());

    private final static Map<String, Long> userMap = Maps.newConcurrentMap();
//    https://weibo.com/ixap
//    http://weibo.com/u/3030737153
//    http://weibo.com/315141777
//    https://weibo.com/shumaC
//    http://weibo.com/273300598
//    http://weibo.com/517012284
//    http://weibo.com/234378688
//    http://weibo.com/836812865
//    http://weibo.com/Gaojiw

    static {
        userMap.put("lizexipablo", 2403484123L);
        userMap.put("techmessager", 1734741902L);
        userMap.put("webthinker123", 2827386331L);
        userMap.put("703723424", 1094293345L);
        userMap.put("206800072", 2554757470L);
        userMap.put("ixap", 1898587152L);
        userMap.put("3030737153", 3030737153L);
        userMap.put("315141777", 2628081005L);
        userMap.put("shumaC", 1866792355L);
        userMap.put("273300598", 1870795791L);
        userMap.put("517012284", 2030935042L);
        userMap.put("234378688", 1923390687L);
        userMap.put("836812865", 5132930861L);
        userMap.put("Gaojiw", 2788711695L);
        PARAM.setContentType("application/x-www-form-urlencoded");
        PARAM.setCookie(
                "YF-V5-G0=4e19e5a0c5563f06026c6591dbc8029f; ULV=1542856153895:3:3:3:6946673129773.87.1542856153870:1542268290741; SINAGLOBAL=6774783524360.861.1542168187658; SUB=_2AkMstxFCf8NxqwJRmP0RxWrjbYx2yQDEieKa6-CZJRMxHRl-yT9jqlAGtRB6Bzc_rT4PWhpZGGOkFTRbIF-_rOsCdiKg; SUBP=0033WrSXqPxfM72-Ws9jqgMF55529P9D9WFr.bqve9YeaEMqXpRp7PJU"
        );

    }

    @Override
    public List<Object> parse(String url, Content content) throws Exception {
        ArrayList<Object> list = Lists.newArrayList();

        String html = new String(content.getHtml(), "utf-8");
        if (url.matches(HOME_PAGE)) {

            Long userId = userMap.get(url.split("com/")[1]);
            Outlink outlink = new Outlink(String.format(LIST_PAGE, userId, userId));
            outlink.addData(PARAM);
            outlink.addData(USER_ID, userId.toString());
            list.add(outlink);
        } else if (url.matches(LIST_PAGE_1)) {

            String userId = (String) content.getValue(USER_ID);
            String s = html.replaceAll("</?[^>]+>", "").replaceAll("\n", "").trim();
            JSONObject js = new JSONObject(s);
            int total = js.getJSONObject("data").getJSONObject("cardlistInfo")
                    .getInt("total");
            int page = total / 10 + 1;
            for (int i = 1; i <= page; i++) {
                Outlink outlink = new Outlink(
                        String.format(LIST_ALL_PAGE, userId, userId, i));

                outlink.addData(PARAM);
                outlink.addData(USER_ID, userId);
                list.add(outlink);
            }

        } else if (url.matches(LIST_ALL_PAGE_1)) {
            String s = html.replaceAll("\n", "").trim();
            JSONObject js = new JSONObject(s);
            JSONArray ja = js.getJSONObject("data").getJSONArray("cards");
            int k = ja.length();
            for (int i = 0; i < k; i++) {
                JSONObject jo = ja.getJSONObject(i);
                if (jo.has("mblog")) {
                    Outlink outlink = new Outlink(String.format(CONTENT,
                            jo.getJSONObject("mblog").getString("id")));

                    outlink.addData(PARAM);
                    list.add(outlink);
                }
            }

        } else if (url.matches(CONTENT_1)) {

            if (Jsoup.parse(html).title().contains("出错了")) {
                return list;
            }

            WeiboModel bm = new WeiboModel("weibo");
            bm.setUrl(url);

            String s = html.split("var \\$render_data = \\[")[1].split("]\\[0]",
                    2)[0];

            JSONObject js = new JSONObject(s).getJSONObject("status");
            JSONObject info = js.getJSONObject("user");

            bm.setStarterId(String.valueOf(info.getLong("id")));
            bm.setStarterName(info.getString("screen_name"));
            bm.setTitle(js.getString("status_title"));
            bm.setPostTime(js.getString("created_at"));
            bm.setContent(js.getString("text"));
            bm.setReposts(js.getInt("reposts_count"));
            bm.setUp(js.getInt("attitudes_count"));
            if (js.has("retweeted_status")) {
                WeiboModel bm1 = new WeiboModel("weibo");
                JSONObject js1 = js.getJSONObject("retweeted_status");
                JSONObject info1 = js1.getJSONObject("user");

                bm1.setStarterId(String.valueOf(info1.getLong("id")));
                bm1.setStarterName(info1.getString("screen_name"));
                bm1.setContent(js1.getString("text"));
                if (js1.getBoolean("isLongText")) {
                    bm1.setContent(
                            js1.getJSONObject("longText").getString("longTextContent"));
                }
                bm1.setReposts(js1.getInt("reposts_count"));
                bm1.setUp(js1.getInt("attitudes_count"));
                bm1.setPostTime(js1.getString("created_at"));
                Document sd = Jsoup.parse(js1.getString("text"));

                List<String> pu = Lists.newArrayList();
                sd.getElementsByTag("img").forEach(e -> {
                    if (e.attr("src").endsWith("jpg")) {
                        pu.add(e.attr("src").startsWith("http") ? e.attr("src")
                                : "http:" + e.attr("src"));
                    }
                });

                if (js1.has("pics")) {
                    JSONArray jsonArray = js1.getJSONArray("pics");
                    int i = jsonArray.length();
                    for (int j = 0; j < i; j++) {
                        pu.add(jsonArray.getJSONObject(j).getString("url"));
                    }
                }
                if (!pu.isEmpty()) {
                    bm1.setPicUrl(pu);
                }

                List<String> vu = Lists.newArrayList();
                if (js1.has("page_info") && js1.getJSONObject("page_info")
                        .getString("type").equals("video")) {
                    vu.add(js1.getJSONObject("page_info").getJSONObject("media_info")
                            .getString("stream_url"));
                }
                bm1.setReviewNum(js.getInt("comments_count"));
                bm.setRetweeted(bm1);
            }

            Document sd = Jsoup.parse(js.getString("text"));

            List<String> pu = Lists.newArrayList();
            sd.getElementsByTag("img").forEach(e -> {
                if (!e.attr("src").contains("emoticon")
                        && !e.attr("src").contains("small_video_default")) {
                    pu.add(e.attr("src").startsWith("http") ? e.attr("src")
                            : "http:" + e.attr("src"));
                }
            });
            if (js.has("pics")) {
                JSONArray jsonArray = js.getJSONArray("pics");
                int i = jsonArray.length();
                for (int j = 0; j < i; j++) {
                    String pic = jsonArray.getJSONObject(j).getString("url");
                    if (!pic.contains("emoticon")) {
                        pu.add(jsonArray.getJSONObject(j).getString("url"));
                    }
                }
            }

            List<String> vu = Lists.newArrayList();
            if (js.has("page_info")) {
                if ("video".equals(js.getJSONObject("page_info").getString("type"))) {
                    vu.add(js.getJSONObject("page_info").getJSONObject("media_info")
                            .getString("stream_url"));
                    bm.setVideoUrl(vu);
                    bm.setThumbnail(Arrays.asList(js.getJSONObject("page_info")
                            .getJSONObject("page_pic").getString("url")));
                } else {
                    pu.add(js.getJSONObject("page_info").getJSONObject("page_pic")
                            .getString("url"));
                }
            }

            bm.setReviewNum(js.getInt("comments_count"));
            if (!pu.isEmpty()) {
                bm.setPicUrl(pu);
            }

            list.add(bm);
            Outlink outlink = new Outlink(
                    String.format(NEW_FiRST_COMMENT, js.getString("id")));
            outlink.addData(PARAM);
            outlink.addData("key", url);
            list.add(outlink);
        } else if (url.matches(NEW_FiRST_COMMENT_REGEX)) {
            if (!html.startsWith("{") || !html.endsWith("}"))  {
                throw new Exception(" HTML 數據出錯 : " + url + "   " + html.substring(0, 100));
            }
            JSONObject js = new JSONObject(html);
            int page = js.getJSONObject("data").getJSONObject("page")
                    .getInt("totalpage");
            for (int i = 1; i <= page; i++) {
                Outlink outlink = new Outlink(
                        url.replace("https", "http").replace("page=1", "page=" + i));
                outlink.addData(PARAM);
//                outlink.addData("key", content.getValue("key"));
                list.add(outlink);
            }
        } else if (url.matches(NEW_COMMENT_REGEX)) {
            WeiboModel bm = new WeiboModel("weibo");
            bm.setUrl(url);
//            bm.setKey(content.getValue("key").toString());

            List<ReviewModel> rv = Lists.newArrayList();

            String json = ToolUtils.unicodetoString(html).replaceAll("\n", "");
            if (!json.startsWith("{") || !json.endsWith("}"))  {
                throw new Exception(" HTML 數據出錯 " + url);
            }
            JSONObject js = new JSONObject(json).getJSONObject("data");
            Document document = Jsoup.parse(js.getString("html"));
            Elements elements = document.getElementsByClass("list_li");
            for (Element e : elements) {
                ReviewModel rm = new ReviewModel();
                Element e1 = e.selectFirst("div[class=WB_text]>a");
                rm.setReviewName(e1.text());
                rm.setReviewId(e1.attr("usercard").substring(3));
                Element e2 = e.selectFirst("div[class=WB_text]");
                List<String> pic = Lists.newArrayList();
                String attr = e.select(".WB_pic").select("img").attr("src");
                if (attr != null && attr.length() > 0) {
                    if (!attr.startsWith("http")) {
                        attr = "http:" + attr;
                    }
                    pic.add(attr);
                }
                e2.getElementsByTag("img").forEach(p -> {
                    if (!p.attr("src").contains("emotion")
                            && !p.attr("src").contains("appstyle")) {
                        pic.add(p.attr("src"));
                    }
                });
                if (!pic.isEmpty()) {
                    rm.setReviewPicUrl(pic);
                }
                rm.setReviewContent(e2.text().substring(e2.text().indexOf("：") + 1));
                int up = "赞".equals(e.select("span[class=line S_line1]>a>span>em").last().text())
                        ? 0
                        : Integer
                        .valueOf(e.select("span[class=line S_line1]>a>span>em")
                                .last().text());
                rm.setReviewUp(up);
                String time = e.selectFirst("div[class=WB_from S_txt2]").text();
                if (time.contains("分钟")) {
                    Date date = new Date();
                    date.setMinutes(date.getMinutes()
                            - Integer.valueOf(time.split("分钟")[0].trim()));
                    time = FORMAT.format(date);
                } else if (time.contains("今天")) {
                    Date date = new Date();
                    time = FORMAT1.format(date) + time.split("今天")[1];
                }
                rm.setReviewTime(time);
                rv.add(rm);
            }

            if (!rv.isEmpty()) {
                bm.setReviews(rv);
                list.add(bm);
            } else {
                throw new Exception("retry");
            }


        }
        return list;
    }

    public static void main(String[] args) throws Exception {

        String url = "http://weibo.com/aj/v6/comment/big?ajwvr=6&id=4347224517494465&filter=all&page=1&sudaref=weibo.com&display=0&retcode=6102";

        ProtocolOutput output = ProtocolFactory.getProtocol(Jsoups.class.getName()).getProtocolOutput(url, (Map) ExtendMapUtil.getAllMap(PARAM));
        WeiBoParse parse = new WeiBoParse();
        List<Object> list = parse.parse(url, output.getContent());
        for (Object o : list) {
            System.out.println(JSON.toJSONString(com.alibaba.fastjson.JSONObject.parseObject(JSON.toJSONString(o))));
        }
    }

}
