package com.mypro.spider.example.example2pro.parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.mypro.spider.example.example2pro.model.ReviewModel;
import com.mypro.spider.example.example2pro.model.NewsModel;
import com.mypro.spider.parse.Content;
import com.mypro.spider.parse.Outlink;
import com.mypro.spider.parse.Parse;
import com.mypro.spider.protocol.ProtocolFactory;
import com.mypro.spider.protocol.ProtocolOutput;
import org.apache.commons.lang3.StringUtils;
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
public class CnbetaNewsParse implements Parse {

    private static final String HOME_PAGE = "https://www.cnbeta.com/topics.htm";
    private static final String LIST_PAGE_1 = "https://www.cnbeta.com/category/[a-z]+.htm";
    private static final String LIST_PAGE_2 = "http://www.cnbeta.com/topics/\\d+.htm";
    private static final String LIST_ALL_PAGE = "https://www.cnbeta.com/home/more\\?type=[a-z]+\\|\\d+&page=\\d+&_csrf=&_=";
    private static final String DETAIL_PAGE_2 = "https://hot.cnbeta.com/articles/[a-z]+/\\d+";
    private static final String DETAIL_PAGE_1 = "https://www.cnbeta.com/articles/\\d+.htm";
    private static final String DETAIL_PAGE_3 = "https://www.cnbeta.com/articles/[a-z]+/\\d+.htm";
    private static final String DETAIL_PAGE_4 = "http://www.cnbeta.com/articles/[a-z]+/\\d+.htm";

    private static final Log _LOG = LogFactory.getLog(CnbetaNewsParse.class);

    @Override
    public List<Object> parse(String url, Content content) throws Exception {
        ArrayList<Object> list = Lists.newArrayList();

        String html = new String(content.getHtml(), "utf-8");
        if (url.matches(HOME_PAGE)) {
            Document document = Jsoup.parse(html);
            Elements ele1 = document.select("div[class=topic-list]>div").get(2)
                    .select("a");
            for (Element e : ele1) {
                if (StringUtils.isNotBlank(e.attr("href"))
                        && (e.attr("href").matches(LIST_PAGE_1)
                        || e.attr("href").matches(LIST_PAGE_2))) {
                    Outlink outlink = new Outlink(e.attr("href"));
                    list.add(outlink);
                }
            }

        } else if (url.matches(LIST_PAGE_1) || url.matches(LIST_PAGE_2)) {
            Document document = Jsoup.parse(html);
            String catid = document.selectFirst(
                    "div[class=w1200] div[class=cnbeta-update-list category-update]")
                    .attr("data-type");
            String token = document.head()
                    .getElementsByAttributeValue("name", "csrf-token").get(0)
                    .attr("content");
            int p = 1;
            String html2 = Jsoup
                    .connect("https://www.cnbeta.com/home/more?&type=" + catid
                            + "&page=" + p + "&_csrf=" + token + "&_=")
                    .header("accept", "application/json, text/javascript, */*; q=0.01")
                    .header("accept-encoding", "gzip, deflate, br")
                    .header("accept-language", "zh-CN,zh;q=0.9").header("referer", url)
                    .header("user-agent",
                            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36")
                    .header("x-requested-with", "XMLHttpRequest")
                    .ignoreContentType(true).get().toString();

            boolean falg = true;
            while (falg) {
                JSONObject js = new JSONObject(html2.replaceAll("\n|\r| ", "")
                        .replaceAll(",\"topic\":\".+?\",\"comments\"", ",\"comments\"")
                        .replaceAll("<[^>]+>", ""));
                if (js.has("result") && !js.get("result").toString().equals("[]")
                        && js.getJSONObject("result").getJSONArray("list").length() > 0) {
                    JSONArray ja = js.getJSONObject("result").getJSONArray("list");
                    for (int i = 0; i < ja.length(); i++) {
                        String u = ja.getJSONObject(i).getString("url_show").startsWith(
                                "http") ? ja.getJSONObject(i).getString("url_show")
                                : "https:" + ja.getJSONObject(i).getString("url_show");
                        Outlink outlink = new Outlink(u);
                        list.add(outlink);
                    }
                    token = js.getString("token");
                    p = p + 1;
                    if (p > 1000) {
                        break;
                    }
                    html2 = Jsoup
                            .connect("https://www.cnbeta.com/home/more?&type=" + catid
                                    + "&page=" + p + "&_csrf=" + token + "&_=")
                            .header("accept",
                                    "application/json, text/javascript, */*; q=0.01")
                            .header("accept-encoding", "gzip, deflate, br")
                            .header("accept-language", "zh-CN,zh;q=0.9")
                            .header("referer", url)
                            .header("user-agent",
                                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36")
                            .header("x-requested-with", "XMLHttpRequest")
                            .ignoreContentType(true).get().toString();
                } else {
                    falg = false;
                }
            }

        } else if (url.startsWith("https://www.cnbeta.com/home/more?type=")
                || url.startsWith("https://www.cnbeta.com/home/more?&type=")) {
            JSONObject js = new JSONObject(html);
            if (js.has("result") && !js.get("result").toString().equals("[]")
                    && js.getJSONObject("result").getJSONArray("list").length() > 0) {
                JSONArray ja = js.getJSONObject("result").getJSONArray("list");
                for (int i = 0; i < ja.length(); i++) {
                    String u = ja.getJSONObject(i).getString("url_show")
                            .startsWith("http") ? ja.getJSONObject(i).getString("url_show")
                            : "https:" + ja.getJSONObject(i).getString("url_show");
                    Outlink outlink = new Outlink(u);
                    list.add(outlink);
                }
            }

        } else if (url.matches(DETAIL_PAGE_1) || url.matches(DETAIL_PAGE_2)
                || url.matches(DETAIL_PAGE_3) || url.matches(DETAIL_PAGE_4)) {

            NewsModel bm = new NewsModel("cnbeta_new");
            bm.setUrl(url);

            Document document = Jsoup.parse(html);

            Element article = document.getElementsByClass("cnbeta-article").get(0);

            bm.setTitle(article.getElementsByTag("h1").text());

            if (url.matches(DETAIL_PAGE_1)) {
                bm.setRoute(Arrays.asList(document.getElementsByClass("topic").get(0)
                        .getElementsByTag("img").attr("title")));

            } else {
                String type1 = url.split("articles/")[1].split("/")[0];
                bm.setRoute(Arrays.asList(document.getElementsByClass(type1).text()));
            }

            List<String> vu = Lists.newArrayList();
            article.getElementsByTag("video").forEach(e -> vu.add(e.attr("src")));
            article.select("iframe").forEach(s -> {
                vu.add(s.attr("src"));
            });
            if (!vu.isEmpty()) {
                bm.setVideoUrl(vu);
            }

            bm.setEditor(
                    document.getElementsByClass("article-author").text().split("：")[1]);
            String[] forms = article.getElementsByClass("source").text().split("：");
            if (forms.length == 2) {
                bm.setSource(forms[1]);
            }
            bm.setPostTime(article.selectFirst("div[class=meta]>span").text());
            bm.setContent(document.getElementById("artibody").html());
            List<String> pu = Lists.newArrayList();
            document.getElementById("artibody").getElementsByTag("img")
                    .forEach(e -> {
                        if (e.attr("src").endsWith("png")
                                || e.attr("src").endsWith("jpg")) {
                            pu.add(e.attr("src"));
                        }
                    });
            if (!pu.isEmpty()) {
                bm.setPicUrl(pu);
            }
            String[] sid2sn = html.split("GV.DETAIL = \\{SID:\"", 2)[1]
                    .split("\"\\};", 2)[0].split("\",SN:\"");
            String csrf = document.head()
                    .getElementsByAttributeValue("name", "csrf-token").attr("content");
            Outlink outlink = new Outlink(
                    "https://www.cnbeta.com/comment/read?_csrf=" + csrf + "&op=1,"
                            + sid2sn[0] + "," + sid2sn[1]);
            outlink.addData("key", url);
            list.add(outlink);
            list.add(bm);

        } else if (url.startsWith("https://www.cnbeta.com/comment/read")) {
            NewsModel vm = new NewsModel("cnbeta_new");
            vm.setUrl(url);
            vm.setKey(content.getValue("key").toString());
            JSONObject json = new JSONObject(html).getJSONObject("result");
            if (json.has("view_num")) {
                vm.setUrl(url);
                vm.setKey(content.getValue("key").toString());
                vm.setViewNum(
                        new JSONObject(html).getJSONObject("result").getInt("view_num"));
                if (json.has("good_num")) {
                    vm.setUp(json.getInt("good_num") + 1);
                }
                if (json.has("bad_num")) {
                    vm.setDown(json.getInt("bad_num") + 1);
                }
                list.add(vm);
            }
            if (!new JSONObject(html).getJSONObject("result").has("cmntstore")) {
                return list;
            }
            JSONObject js = new JSONObject(html).getJSONObject("result")
                    .getJSONObject("cmntstore");
            List<ReviewModel> rv = Lists.newArrayList();
            Iterator<?> keys = js.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                JSONObject jn1 = js.getJSONObject(key);
                ReviewModel rm1 = new ReviewModel();
                rm1.setReviewTime(jn1.getString("date"));
                rm1.setReviewContent(jn1.getString("comment"));
                rm1.setReviewId(jn1.getString("userid"));
                rm1.setReviewName(jn1.getString("name"));
                rm1.setReviewUp(Integer.valueOf(jn1.getString("score")));
                if (!jn1.getString("pid").equals("0")
                        && js.has(jn1.getString("pid"))) {
                    JSONObject jn = js.getJSONObject(jn1.getString("pid"));
                    ReviewModel rm = new ReviewModel();
                    rm.setReviewTime(jn.getString("date"));
                    rm.setReviewContent(jn.getString("comment"));
                    rm.setReviewId(jn.getString("userid"));
                    rm.setReviewName(jn.getString("name"));
                    rm.setReviewUp(Integer.valueOf(jn.getString("score")));
                    rm1.setReviewTo(rm);
                }
                rv.add(rm1);
            }
            if (!rv.isEmpty()) {
                vm.setReviews(rv);
                list.add(vm);
            }
        } else {
        }
        return list;
    }

    public static void main(String[] args) throws Exception {
        String url = "https://www.cnbeta.com/topics.htm";
//        Content content = Content.newContent(url, Jsoup.connect(url).get().toString().getBytes(),null);
//        ChiphellBBSParse chiphellBBSParse = new ChiphellBBSParse();
//        List<Object> list = chiphellBBSParse.parse(url,content);
//        for (Object o : list) {
//            System.out.println(new Gson().toJson(o));
//        }
        ProtocolOutput output = ProtocolFactory.getProtocol(null).getProtocolOutput(url,null);
        CnbetaNewsParse chiphellBBSParse = new CnbetaNewsParse();
        List<Object> list = chiphellBBSParse.parse(url,output.getContent());
        for (Object o : list) {
            System.out.println(new Gson().toJson(o));
        }
    }
}
