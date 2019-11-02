package com.mypro.spider.example.example2pro.parse;

import com.google.common.collect.Lists;

import com.google.gson.Gson;
import com.mypro.spider.example.example2pro.model.ReviewModel;
import com.mypro.spider.example.example2pro.model.NewsModel;
import com.mypro.spider.parse.Content;
import com.mypro.spider.parse.Outlink;
import com.mypro.spider.parse.Parse;
import com.mypro.spider.protocol.ProtocolFactory;
import com.mypro.spider.protocol.ProtocolOutput;
import com.mypro.spider.protocol.httputil.jsoup.Jsoups;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author fgzhong
 * @since 2018/11/1
 */
public class ChiphellBBSParse implements Parse {

    private static final String HOME_PAGE = "https://www.chiphell.com/forum.php";
    private static final String LIST_PAGE = "https://www.chiphell.com/forum-\\d+-1.html";
    private static final String LIST_ALL_PAGE = "http://www.chiphell.com/forum-\\d+-\\d+.html";
    private static final String DETAIL_PAGE = "https://www.chiphell.com/thread-\\d+-1-\\d+.html";
    private static final String REVIEW_PAGE = "https://www.chiphell.com/thread-\\d+-\\d+-\\d+.html\\?";

    @Override
    public List<Object> parse(String url, Content content) throws Exception {
        ArrayList<Object> list = Lists.newArrayList();

        String html = new String(content.getHtml(), "utf-8");
        if (url.matches(HOME_PAGE)) {
            Document document = Jsoup.parse(html);
            Elements elements = document.getElementsByTag("dt");
            elements.remove(elements.first());
            for (Element e : elements) {
                Outlink outlink = new Outlink("https://www.chiphell.com/"
                        + e.getElementsByTag("a").attr("href"));
                list.add(outlink);
            }
        } else if (url.matches(LIST_PAGE)) {
            Document document = Jsoup.parse(html);
            String string = "";
            if (!document.getElementsByClass("last").isEmpty()) {
                Element element = document.getElementsByClass("last").get(0);
                string = element.text().substring(4);
            } else if (document.getElementById("fd_page_top").select("label span")
                    .isEmpty()) {
                string = "1";
            } else {
                Element element = document.getElementById("fd_page_top")
                        .selectFirst("label span");
                string = element.attr("title").replaceAll("共", "").replaceAll("页", "")
                        .trim();
            }

            int k = Integer.valueOf(string);
            k = Math.min(k, 500);
            for (int i = 1; i <= k; i++) {
                Outlink outlink = new Outlink(
                        url.replace("https", "http").replace("1.html", i + ".html"));
                list.add(outlink);
            }

        } else if (url.matches(LIST_ALL_PAGE)) {
            Document document = Jsoup.parse(html);
            if (url.matches("http://www.chiphell.com/forum-62-\\d+.html")) {
                Elements elements = document.getElementById("waterfall")
                        .getElementsByTag("li");
                for (Element e : elements) {
                    list.add(new Outlink(
                            "https://www.chiphell.com/" + e.selectFirst("a").attr("href")));
                }
            } else {
                Elements elements = document.getElementsByClass("s xst");
                elements.remove(0);
                elements.remove(0);
                elements.remove(0);
                for (Element e : elements) {
                    Outlink outlink = new Outlink(
                            "https://www.chiphell.com/" + e.attr("href"));
                    list.add(outlink);
                }
            }

        } else if (url.matches(DETAIL_PAGE)) {
            NewsModel bm = new NewsModel("chiphell");
            bm.setUrl(url);
            ArrayList<String> imgUrl = Lists.newArrayList();
            Document document = Jsoup.parse(html);
            String name = document.selectFirst(".authi").text();
            bm.setStarterName(name);
            bm.setPostTime(document.selectFirst("td[class=plc] div[class=authi] em")
                    .text().substring(4));
            Elements e1 = document.getElementById("pt").getElementsByTag("a"); /// ???
            bm.setRoute(Arrays.asList(e1.get(2).text(), e1.get(3).text()));
            bm.setTitle(document.getElementById("thread_subject").text());
            bm.setViewNum(Integer.valueOf(document.getElementsByClass("hm ptn")
                    .get(0).getElementsByTag("span").get(1).text()));
            bm.setContent(document.getElementById("postlist")
                    .selectFirst("td[class=t_f]").html());
            document.getElementById("postlist").selectFirst("td[class=t_f]")
                    .select("img").forEach(s -> {
                if (s.attr("zoomfile").endsWith("png")
                        || s.attr("zoomfile").endsWith("jpg")) {
                    imgUrl.add(s.attr("zoomfile"));
                }
            });
            bm.setPicUrl(imgUrl);
            String string = "";
            Element e2 = document.getElementById("postlist")
                    .selectFirst("td[class=t_f]");
            List<String> vu = Lists.newArrayList();
            e2.getElementsByTag("embed").forEach(s1 -> vu.add(s1.attr("src")));
            if (!vu.isEmpty()) {
                bm.setVideoUrl(vu);
            }

            if (!document.getElementsByClass("last").isEmpty()) {
                Element element = document.getElementsByClass("last").get(0);
                string = element.text().substring(4);
            } else if (document.getElementById("pgt").select("label span")
                    .isEmpty()) {
                string = "1";
            } else {
                Element element = document.getElementById("pgt")
                        .selectFirst("label span");
                string = element.attr("title").replaceAll("共", "").replaceAll("页", "")
                        .trim();
            }
            int m = Integer.valueOf(string);
            for (int i = 1; i <= m; i++) {
                Outlink outlink = new Outlink(url.replace("1-1.html", i + "-1.html?"));
                outlink.addData("key", url);
                list.add(outlink);
            }

            list.add(bm);

        } else if (url.matches(REVIEW_PAGE)) {
            List<ReviewModel> rv = Lists.newArrayList();
            NewsModel bm = new NewsModel("chiphell");
            bm.setKey(content.getValue("key").toString());
            bm.setUrl(url);

            Document document = Jsoup.parse(html);
            Elements elements = document.getElementById("postlist").select(">div");
            if (url.matches(".+-1-\\d+.html\\?")) {
                elements.remove(0);
            }
            elements.remove(elements.last());
            for (Element e : elements) {
                ReviewModel rm = new ReviewModel();
                Element e1 = e.selectFirst("td[class=pls] div[class=authi] a");
                rm.setReviewId(e1.attr("href").split("uid-")[1].split(".html")[0]);
                rm.setReviewName(e1.text());
                rm.setReviewTime(e.selectFirst("td[class=plc] div[class=authi] em")
                        .text().substring(4));
                Element e2 = e.selectFirst("div[class=pcb]");
                rm.setReviewContent(e2.text());
                List<String> pu = Lists.newArrayList();
                e2.getElementsByTag("img").forEach(s -> {
                    if (s.attr("zoomfile").endsWith("png")
                            || s.attr("zoomfile").endsWith("jpg")) {
                        pu.add(s.attr("zoomfile"));
                    }
                });
                List<String> vu = Lists.newArrayList();
                e2.getElementsByTag("embed").forEach(s -> vu.add(s.attr("src")));
                if (!vu.isEmpty()) {
                    rm.setReviewVideoUrl(vu);
                }
                if (!pu.isEmpty()) {
                    rm.setReviewPicUrl(pu);
                }
                if (!e.getElementsByTag("blockquote").isEmpty()) {
                    ReviewModel rm1 = new ReviewModel();
                    rm1.setReviewName(e.selectFirst("blockquote")
                            .getElementsByAttribute("color").text().split("发表于")[0]);
                    rm1.setReviewTime(e.selectFirst("blockquote")
                            .getElementsByAttribute("color").text().split("发表于")[1]);
                    if (e.selectFirst("blockquote").text().split(" ").length >=5) {
                        rm1.setReviewContent(
                                e.selectFirst("blockquote").text().split(" ")[4]);
                    }
                    rm.setReviewTo(rm1);
                }
                rv.add(rm);
            }
            bm.setReviews(rv);
            list.add(bm);
        }
        return list;
    }

    public static void main(String[] args) throws Exception {
        String url = "https://www.chiphell.com/forum.php";

        ProtocolOutput output = ProtocolFactory.getProtocol(Jsoups.class.getName()).getProtocolOutput(url, null);
        ChiphellBBSParse chiphellBBSParse = new ChiphellBBSParse();
        List<Object> list = chiphellBBSParse.parse(url, output.getContent());
        int k=0;
        for (Object o : list) {
            TimeUnit.SECONDS.sleep(1);
            System.out.println(new Gson().toJson(o));
            Outlink outlink = (Outlink) o;
            ProtocolOutput output1 = ProtocolFactory.getProtocol(Jsoups.class.getName()).getProtocolOutput(outlink.getUrl(), null);
            if (output1.getStatus() !=1) {
                System.out.println(" \\\\ " + outlink.getUrl() + "  " +output1.getStatus());
            } else {
                List<Object> list1 = chiphellBBSParse.parse(outlink.getUrl(), output1.getContent());
                System.out.println(list1.size());
                k+=list1.size();
            }

        }
        System.out.println( " ---- " + k);

    }
}
