package com.mypro.spider.example.example2pro.parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mypro.spider.example.example2pro.model.ReviewModel;
import com.mypro.spider.example.example2pro.model.NewsModel;
import com.mypro.spider.parse.Content;
import com.mypro.spider.parse.Outlink;
import com.mypro.spider.parse.Parse;
import com.mypro.spider.utils.ToolUtils;
import org.apache.commons.lang.StringUtils;
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
public class PcpopNewsParse implements Parse {

    private static final String HOME_PAGE = "http://www.pcpop.com/";
    private static final String LIST_PAGE = "http://[a-z]+.pcpop.com/";
    private static final String LIST_ALL_PAGE = "http://api.cms.it168.com/api/article_list_api/pcpop/\\?channel=\\d+"
            + "&size=1&index=1&ispager=&brand=\\d+&grade=0,1,2,3,4,5,6";
    private static final String DETAIL_ALL_PAGE = "http://api.cms.it168.com/api/article_list_api/pcpop/\\?channel=\\d+"
            + "&size=30&index=\\d+&ispager=&brand=\\d+&grade=0,1,2,3,4,5,6";
    private static final String DETAIL_PAGE = "http://www.pcpop.com/article/\\d+.shtml";


    @Override
    public List<Object> parse(String url, Content content) throws Exception {
        ArrayList<Object> list = Lists.newArrayList();
        if (content.getHtml() == null) {
            return list;
        }
        String html = new String(content.getHtml(), "utf-8");
        if (url.matches(HOME_PAGE)) {
            Document document = Jsoup.parse(html);
            Elements elements = document.select("div[class=nav] ul>li");
            elements.remove(elements.last());
            elements.remove(elements.last());
            for (Element e : elements) {
                Elements e1 = e.select("a");
                e1.remove(e1.first());
                for (Element e2 : e1) {
                    Outlink outlink = new Outlink(e2.attr("href"));
                    list.add(outlink);
                }
            }
            Outlink outlink1 = new Outlink("http://vr.pcpop.com/");
            list.add(outlink1);
            Outlink outlink2 = new Outlink("http://dv.pcpop.com/");
            list.add(outlink2);
            Outlink outlink3 = new Outlink("http://uav.pcpop.com/");
            list.add(outlink3);
            Outlink outlink4 = new Outlink("http://speaker.pcpop.com/");
            list.add(outlink4);
            Outlink outlink5 = new Outlink("http://projector.pcpop.com/");
            list.add(outlink5);

        } else if (url.matches(LIST_PAGE)) {
            Document document = Jsoup.parse(html);
            Elements elements = document.select("div[class=pinpai] a");
            String channel = document.head().select("script").last().toString()
                    .split("var channelId = ")[1].split(";")[0];
            for (Element e : elements) {
                String id = e.attr("onclick").split(",")[3].replaceAll("'", "")
                        .trim();
                Outlink outlink = new Outlink(
                        "http://api.cms.it168.com/api/article_list_api/pcpop/?"
                                + "channel=" + channel + "&size=1&index=1&ispager=&brand="
                                + id + "&grade=0,1,2,3,4,5,6");
                list.add(outlink);
            }
            if (elements.isEmpty()) {
                Outlink outlink = new Outlink(
                        "http://api.cms.it168.com/api/article_list_api/pcpop/?"
                                + "channel=" + channel
                                + "&size=1&index=1&ispager=&brand=0&grade=0,1,2,3,4,5,6");
                list.add(outlink);
            }

        } else if (url.matches(LIST_ALL_PAGE)) {
            JSONObject js1 = new JSONObject(ToolUtils.unicodetoString(html));
            Integer num = js1.getInt("count");
            int k = 1;
            while (k <= num / 30 + 1) {
                String s2 = url.replace("size=1", "size=" + 30).replace("index=1",
                        "index=" + k++);
                Outlink outlink = new Outlink(s2);
                list.add(outlink);
            }

        } else if (url.matches(DETAIL_ALL_PAGE)) {
            String s4 = ToolUtils.unicodetoString(html).replaceAll("</?[^>]+>", "")
                    .replaceAll("\n", "").trim();

            String[] s = s4.split("\"ArtUrl\":\"");
            int v = s.length;
            for (int c = 1; c < v; c++) {
                String s5 = s[c].split("\",", 2)[0].replaceAll("\\\\", "");
                if (s5.matches(DETAIL_PAGE)) {
                    Outlink outlink = new Outlink(s5);
                    list.add(outlink);
                }
            }
        } else if (url.matches(DETAIL_PAGE)) {
            NewsModel bm = new NewsModel("pcpop");
            bm.setUrl(url);
            Document document = Jsoup.parse(html);
            Element element;
            if (!document.getElementsByClass("l1").isEmpty()) {
                element = document.getElementsByClass("l1").get(0);
                bm.setTitle(element.getElementsByTag("h1").text());
                String txt = element.getElementsByClass("chuchu").text();
                bm.setStarterName(txt.split("作者:")[1].split("分享")[0].trim());
                bm.setPostTime(txt.split("出处")[0].trim());
                bm.setSource(txt.split("出处：")[1].split("作者")[0].trim());
            } else {
                element = document.getElementsByClass("content-box").get(0);
                bm.setTitle(element.getElementsByClass("title").text());
                Elements elements = element.select("div[class=ts-info]>span");
                bm.setPostTime(elements.get(0).text());
                bm.setStarterName(elements.get(1).text().substring(3));
                if (elements.size() > 2) {
                    bm.setEditor(elements.get(2).selectFirst("a").text());
                }
                if (elements.size() > 3) {
                    bm.setSource(elements.get(3).text().split("出处：")[1]);
                }
            }

            Elements element1 = document.getElementsByClass("crumbs").get(0)
                    .getElementsByTag("a");
            bm.setRoute(
                    Arrays.asList(element1.get(3).text(), element1.get(2).text()));

            bm.setContent(document.select("div[class=content-box]>p").html());
            List<String> pu = Lists.newArrayList();
            document.select("div[class=content-box] img")
                    .forEach(e -> pu.add(e.attr("src")));
            if (!pu.isEmpty()) {
                bm.setPicUrl(pu);
            }
            List<String> vu = Lists.newArrayList();
            document.select("div[class=content-box] embed")
                    .forEach(e -> vu.add(e.attr("src")));
            if (!vu.isEmpty()) {
                bm.setVideoUrl(vu);
            }
            String zan = document.select(".icon-praise").text();
            String regex = "(\\d+)人.+";
            Matcher matcher = Pattern.compile(regex).matcher(zan);
            if (matcher.find()) {
                zan = matcher.group(1);
            }
            if (zan != null && zan.length() > 0 && StringUtils.isNumeric(zan)) {
                bm.setUp(Integer.valueOf(zan));
            }

            String articleId = document.getElementById("articleid").attr("value");
            Outlink outlink = new Outlink(
                    "http://comment.it168.com/api/getcomments?page=1&articleid="
                            + articleId + "&num=1");
            outlink.addData("key", url);
            outlink.addData("articleid", articleId);
            list.add(outlink);

            list.add(bm);

        } else if (url.startsWith("http://comment.it168.com/api/getcomments")
                && url.endsWith("1")) {
            JSONObject js = new JSONObject(html);
            int rvNum = js.getJSONObject("data").getInt("total") / 30;
            for (int i = 1; i <= rvNum + 1; i++) {
                Outlink outlink = new Outlink(
                        "http://comment.it168.com/api/getcomments?page=" + i
                                + "&articleid=" + content.getValue("articleid") + "&num=30");
                outlink.addData("key", content.getValue("key"));
                list.add(outlink);
            }
        } else if (url.startsWith("http://comment.it168.com/api/getcomments")
                && url.endsWith("30")) {
            NewsModel bm = new NewsModel("pcpop");
            bm.setUrl(url);
            bm.setKey(content.getValue("key").toString());

            JSONObject jo = new JSONObject(ToolUtils.unicodetoString(html).trim());
            List<ReviewModel> rv = Lists.newArrayList();
            JSONArray ja = jo.getJSONObject("data").getJSONArray("items");
            int j = ja.length();
            for (int i = 0; i < j; i++) {
                ReviewModel rm = new ReviewModel();
                JSONObject jn = ja.getJSONObject(i);
                rm.setReviewName(jn.getString("nickname"));
                rm.setReviewTime(jn.getString("comment_time"));
                rm.setReviewContent(jn.getString("comment"));
                if (jn.has("uid")) {
                    rm.setReviewId(String.valueOf(jn.getInt("uid")));
                }
                if (jn.has("article_id")) {
                    rm.setReviewId(String.valueOf(jn.getInt("article_id")));
                }
                rm.setReviewUp(jn.getInt("digg_count"));
                rv.add(rm);
            }
            if (!rv.isEmpty()) {
                bm.setReviews(rv);
            }
            list.add(bm);

        } else if (url.startsWith("http://changyan.sohu.com/api/")) {
            NewsModel bm = new NewsModel("pcpop");
            bm.setUrl(url);
            bm.setKey(content.getValue("key").toString());

            JSONObject jo = new JSONObject(
                    html.replaceAll("</?[^>]+>", "").replaceAll("\n", "").trim());
            List<ReviewModel> rv = Lists.newArrayList();
            if (jo.has("comments")) {
                JSONArray ja = jo.getJSONArray("comments");
                int j = ja.length();
                for (int i = 0; i < j; i++) {
                    ReviewModel rm = new ReviewModel();
                    JSONObject jn = ja.getJSONObject(i);
                    rm.setReviewId(jn.getJSONObject("passport").getString("user_id"));
                    if (jn.getJSONObject("passport").has("nickname")) {
                        rm.setReviewName(
                                jn.getJSONObject("passport").getString("nickname"));
                    }
                    rm.setReviewTime(jn.getString("create_time"));
                    rm.setReviewContent(jn.getString("content"));
                    rv.add(rm);
                }
            }
            if (!rv.isEmpty()) {
                bm.setReviews(rv);
            }
            list.add(bm);
        }
        return list;
    }

    public static void main(String[] args) throws Exception {
    }
}
