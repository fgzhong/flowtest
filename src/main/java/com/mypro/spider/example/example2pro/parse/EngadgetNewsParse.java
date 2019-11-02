package com.mypro.spider.example.example2pro.parse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.mypro.spider.data.ExtendMap;
import com.mypro.spider.example.example2pro.model.ReviewModel;
import com.mypro.spider.example.example2pro.model.NewsModel;
import com.mypro.spider.parse.Content;
import com.mypro.spider.parse.Outlink;
import com.mypro.spider.parse.Parse;
import com.mypro.spider.protocol.ProtocolFactory;
import com.mypro.spider.protocol.ProtocolOutput;
import com.mypro.spider.protocol.httputil.jsoup.Jsoups;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.Lists;

/**
 * @author fgzhong
 * @since 2018/11/1
 */
public class EngadgetNewsParse implements Parse {

    private static final String HOME_PAGE = "http://cn.engadget.com/";
    private static final String LIST_PAGE = "http://cn.engadget.com/topics/[a-z]+/page/\\d+";
    private static final String LIST_PAGE_1 = "http://cn.engadget.com/topics/[a-z]+-[a-z]+/page/\\d+";
    private static final String LIST_PAGE_2 = "http://cn.engadget.com/features/page/\\d+";
    private static final String VIDEO = "https://hlsrv.vidible.tv/prod/%s/hls/segment_1920x1080_v2.m3u8?PR=E&S=38yUZj5GFRpy2STzSnXdoOFvs8MBNN6LQEkYOHC2TTPoieRXUirlMtMov4qDhtaV";

    private static final Log _LOG = LogFactory.getLog(EngadgetNewsParse.class);

    private final static SimpleDateFormat format = new SimpleDateFormat(
            "yyyy-MM-dd HH:00:00");

    @SuppressWarnings("deprecation")
    @Override
    public List<Object> parse(String url, Content content) throws Exception {
        ArrayList<Object> list = Lists.newArrayList();

        String html = new String(content.getHtml(), "utf-8");
        if (url.matches(HOME_PAGE)) {
            Document document = Jsoup.parse(html);
            Elements elements = document.select("ul[class=category-list] a");
            for (Element e : elements) {
                for (int i = 1; i < 300; i++) {
                    Outlink outlink = new Outlink(
                            "http://cn.engadget.com" + e.attr("href") + "page/" + i);
                    list.add(outlink);
                }
            }
            for (int i = 1; i < 300; i++) {
                Outlink outlink1 = new Outlink(
                        "http://cn.engadget.com/topics/vr-ar/page/" + i);
                list.add(outlink1);
                Outlink outlink = new Outlink(
                        "http://cn.engadget.com/features/page/" + i);
                list.add(outlink);
            }
        } else if (url.matches(LIST_PAGE) || url.matches(LIST_PAGE_1)
                || url.matches(LIST_PAGE_2)) {
            Document document = Jsoup.parse(html);
            Elements elements = document.select(
                    "div[class=grid@tl+__cell col-8-of-12@tl col-11-of-15@d flex-1]>div");
            elements.remove(elements.last());
            for (Element e : elements) {
                Outlink outlink = new Outlink(
                        "http://cn.engadget.com" + e.selectFirst("a").attr("href"));
                list.add(outlink);
            }

        } else if (url.startsWith("https://www.spot.im/api")) {

            NewsModel bm = new NewsModel("engadget");
            bm.setKey(content.getValue("key").toString());
            bm.setUrl(url);

            String s = html.trim();

            if (!s.endsWith("}")) {
                throw new Exception("content error : " + html.substring(0,100));
            }
            JSONObject js = new JSONObject(s);
            if (!js.has("comments")) {
                return list;
            }
            JSONArray ja = js.getJSONArray("comments");
            int j = ja.length();
            List<ReviewModel> rv = new ArrayList<>();
            for (int i = 0; i < j; i++) {
                ReviewModel rm = new ReviewModel();
                JSONObject jo = ja.getJSONObject(i);
                rm.setReviewId(jo.getString("user_id"));
                rm.setReviewName(js.getJSONObject("users")
                        .getJSONObject(jo.getString("user_id")).getString("user_name"));
                rm.setReviewContent(
                        jo.getJSONArray("content").getJSONObject(0).getString("text"));
                rm.setReviewTime(String.valueOf(jo.getLong("time")));
                if (jo.getJSONArray("content").length() > 1) {
                    rm.setReviewPicUrl(Arrays.asList(jo.getJSONArray("content")
                            .getJSONObject(1).getString("originalUrl")));
                }
                if (jo.has("rank")) {
                    rm.setReviewUp(jo.getJSONObject("rank").getInt("+"));
                    rm.setReviewDown(jo.getJSONObject("rank").getInt("-"));
                }
                if (jo.has("replies")) {
                    JSONArray jb = jo.getJSONArray("replies");
                    int k = jb.length();
                    List<ReviewModel> rs = new ArrayList<>();
                    for (int l = 0; l < k; l++) {
                        ReviewModel r = new ReviewModel();
                        JSONObject jt = jb.getJSONObject(l);
                        r.setReviewId(jt.getString("user_id"));
                        r.setReviewContent(jt.getJSONArray("content").getJSONObject(0)
                                .getString("text"));
                        r.setReviewTime(String.valueOf(jt.getLong("time")));
                        if (jt.getJSONArray("content").length() > 1) {
                            rm.setReviewPicUrl(Arrays.asList(jt.getJSONArray("content")
                                    .getJSONObject(1).getString("originalUrl")));
                        }
                        if (jt.has("rank")) {
                            r.setReviewUp(jt.getJSONObject("rank").getInt("+"));
                            r.setReviewDown(jt.getJSONObject("rank").getInt("-"));
                        }
                        rs.add(r);
                    }
                    rm.setReviewModels(rs);
                }
                rv.add(rm);
            }
            if (!rv.isEmpty()) {
                bm.setReviews(rv);
            }
            list.add(bm);
        } else {

            NewsModel bm = new NewsModel("engadget");
            bm.setUrl(url);

            Document document = Jsoup.parse(html);
            Element ele1 = document.selectFirst(
                    "div[class=o-title_mark@tp+ bc-gray-1 col-10-of-12@tl+]");

            bm.setTitle(ele1.selectFirst("h1").text());
            if (!ele1.getElementsByTag("h2").isEmpty()) {
                bm.setBakeTitle(ele1.selectFirst("h2").text());
            }
            Element ele2 = document
                    .selectFirst("div[class=t-meta-small@s t-meta@m+]");
            if (!ele2.getElementsByTag("a").isEmpty()) {
                bm.setStarterName(ele2.selectFirst("a").text());
            }

            String time = ele2.selectFirst(">div").text();
            if (time.contains("小时")) {
                Date date = new Date();
                date.setHours(
                        date.getHours() - Integer.valueOf(time.split("小时")[0].trim()));
                time = format.format(date);
            }
            bm.setPostTime(time);

            Element elemen = document.selectFirst("div[id=page_body]");
            Element els = elemen.selectFirst("div[class=flush-top flush-bottom]");
            List<String> pu = Lists.newArrayList();
            els.getElementsByTag("img").forEach(e -> pu.add(e.attr("src")));
            if (!pu.isEmpty()) {
                bm.setPicUrl(pu);
            }
            List<String> vu = Lists.newArrayList();
            els.getElementsByTag("iframe").forEach(e -> {
                if (!e.attr("src").equals("")) {
                    vu.add(e.attr("src"));
                }
            });
            els.getElementsByTag("video").forEach(e -> {
                if (!e.attr("src").equals("")) {
                    vu.add(e.attr("src"));
                }
            });
            els.getElementsByClass("vdb_player").forEach(e -> {
                if (e.hasAttr("id")) {
                    String[] date = url.split("com/")[1].split("/");
                    vu.add(String.format(VIDEO, e.attr("data-placeholder").split("/")[6]
                            + "/" + date[0] + "-" + date[1] + "-" + date[2]));
                }
            });
            if (!vu.isEmpty()) {
                bm.setVideoUrl(vu);
            }
            bm.setContent(els.html());

            Elements elemen1 = elemen.select("div[class=mt-5]");
            String s1 = els.text().substring(els.text().length() - 30);
            if (!elemen1.isEmpty()) {
                if (elemen1.text().contains("经由")) {
                    bm.setEditor(elemen1.get(0).text().substring(3));
                    if (elemen1.text().contains("来源")) {
                        bm.setSource(elemen1.get(1).text().substring(3));
                    }
                } else if (s1.contains("经由") && s1.contains("来源")) {
                    bm.setEditor(s1.split("经由：")[1].split("引用")[0]);
                    bm.setSource(s1.split("引用来源：")[1]);
                }
                if (elemen1.text().contains("标签")) {
                    bm.setTag(elemen1.last().getElementsByTag("a").stream().map(e -> {
                        return e.text();
                    }).collect(Collectors.toList()));
                }
            }

            String postId = document.head().selectFirst("meta[name=post_id]")
                    .attr("content");
            String spotId = document
                    .selectFirst("div[id=page_body] section[id=recirculation]>div")
                    .attr("data-spot-id");

            String s = "https://www.spot.im/api/conversation-read/spot/%s/post/post-%s/get";
            Outlink outlink = new Outlink(String.format(s, spotId, postId));
            outlink.addData("key", url);
            ExtendMap httpParameters = new ExtendMap().postHttpType();
            httpParameters.setRequestBody(
                    "{\"count\":100,\"sort_by\":\"newest\",\"cursor\":{\"offset\":0,\"comments_read\":0}}");
            httpParameters.setContentType("application/json");
            outlink.addData(httpParameters);
            list.add(outlink);
            list.add(bm);
        }
        return list;
    }


    public static void main(String[] args) throws Exception {
        String url = "http://cn.engadget.com/2007/05/06/magnetic-signals-could-cure-chronic-insomnia/";

        ProtocolOutput output = ProtocolFactory.getProtocol(Jsoups.class.getName()).getProtocolOutput(url, null);
        EngadgetNewsParse parse = new EngadgetNewsParse();
        List<Object> list = parse.parse(url, output.getContent());
        for (Object o : list) {
            System.out.println(new Gson().toJson(o));
        }

    }

}
