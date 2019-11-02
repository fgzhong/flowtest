package com.mypro.spider.example.example2pro.parse;

import java.util.ArrayList;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.Lists;

/**
 * @author fgzhong
 * @since 2018/11/1
 */
public class IthomeNewsParse implements Parse {

    private static final String HOME_PAGE = "https://www.ithome.com/sitemap/";
    private static final String LIST_PAGE = "http://[a-zA-Z0-9]+.ithome.com/[a-zA-Z0-9]+/";
    private static final String LIST_PAGE_2 = "http://[a-zA-Z0-9]+.ithome.com/[a-zA-Z0-9]+_[a-zA-Z0-9]+/";
    private static final String DATIAL_PAGE_1 = "https://www.ithome.com/html/\\S+/\\d+.htm";
    private static final String DATIAL_PAGE_2 = "https://www.ithome.com/0/\\d+/\\d+.htm";
    private static final String DATIAL_PAGE_3 = "https://\\S+.ithome.com/html/\\S+/\\d+.htm";

    @Override
    public List<Object> parse(String url, Content content) throws Exception {
        ArrayList<Object> list = new ArrayList<>();
        String html = new String(content.getHtml(), "utf-8");

        if (url.matches(HOME_PAGE)) {
            Document document = Jsoup.parse(html);
            Elements elements = document.select("ul[class=map_list]");
            for (Element e : elements) {
                Elements e1 = e.select("a");
                for (Element e2 : e1) {
                    String s = e2.attr("href");
                    if (s.matches(LIST_PAGE) || s.matches(LIST_PAGE_2)) {
                        Outlink outlink = new Outlink(s);
                        list.add(outlink);
                    }
                }
            }

        } else if (url.matches(LIST_PAGE) || url.matches(LIST_PAGE_2)) {
            Document document = Jsoup.parse(html);
            String s = document.selectFirst("div[id=wrapper] div[class=sidebar] ul")
                    .select("li").last().selectFirst("script").toString();
            String id = s.split("categoryID =")[1].split("</script>")[0];

            String u = url.split(".ithome")[0].replace("http", "https")
                    + ".ithome.com/ithome/getajaxdata.aspx";

            for (int j = 1; j < 100; j++) {
                Outlink outlink = new Outlink(u + "?detail=" + j + "&id=" + id);
                ExtendMap parm = new ExtendMap().postHttpType();
                parm.setRequestBody(
                        "categoryid=" + id + "&type=pccategorypage&page=" + j);
                parm.setContentType(
                        "application/x-www-form-urlencoded; charset=UTF-8");
                outlink.addData(parm);
                list.add(outlink);
            }

        } else if (url.matches(
                "https://\\S+.ithome.com/ithome/getajaxdata.aspx\\?detail=\\S+&id=\\S+")) {
            Document doc = Jsoup.parse(html);
            Elements elements = doc.select("h2");
            for (Element e : elements) {
                String s1 = e.selectFirst("a").attr("href");
                if (s1.matches(DATIAL_PAGE_1) || s1.matches(DATIAL_PAGE_2)
                        || url.matches(DATIAL_PAGE_3)) {
                    Outlink outlink = new Outlink(s1);
                    list.add(outlink);
                } else {
                }
            }

        } else if (url.matches(DATIAL_PAGE_1) || url.matches(DATIAL_PAGE_2)
                || url.matches(DATIAL_PAGE_3)) {
            NewsModel bm = new NewsModel("ithome");
            List<String> route = Lists.newArrayList();
            bm.setUrl(url);

            Document document = Jsoup.parse(html);
            Element element = document.selectFirst("div[id=wrapper]");
            if (element == null) {
                element = document.getElementById("con");
            }
            if (element == null) {
                element = document.getElementById("content");
            }
            if (element == null) {
                element = document.getElementById("wp");
            }
            Elements elemen1 = element.getElementsByClass("current_nav");
            if (elemen1.isEmpty()) {
                elemen1 = element.getElementsByClass("sub_nav");
            }
            if (elemen1.isEmpty()) {
                elemen1 = element.getElementsByClass("location");
            }
            if (elemen1.isEmpty()) {
                elemen1 = element.getElementsByClass("bm cl");
            }
            Elements ele1 = elemen1.select("a"); // ??
            ele1.select("a").forEach(s -> {
                route.add(s.text());
            });
//        if (ele1.size() > 1)
//          bm.setRoute(Arrays.asList(ele1.get(1).text(), ele1.get(2).text()));
            bm.setRoute(route);

            Element ele2 = document.selectFirst("div[class=post_title]");
            if (ele2 == null) {
                ele2 = document.selectFirst("div[class=t_title]");
            }
            if (ele2 == null) {
                ele2 = document.selectFirst("div[class=title]");
            }
            if (ele2 == null) {
                ele2 = document.selectFirst("div[class=pl bm]");
            }
            bm.setTitle(
                    ele2.selectFirst("h1") != null ? ele2.selectFirst("h1").text()
                            : ele2.selectFirst("h2").text());
            Elements ele3 = ele2.select(">span>span");
            if (!ele3.isEmpty()) {
                bm.setPostTime(ele3.get(0).text());
                bm.setSource(ele3.get(1).text().split("：")[1].trim());
                bm.setStarterName(ele3.get(2).text().split("：")[1].trim());
                bm.setEditor(ele3.get(3).text().split("：")[1].trim());
            } else {
                bm.setPostTime(ele2.getElementsByClass("posantime").text());
                bm.setStarterName(ele2.getElementsByClass("posantime").text());
            }
            bm.setTag(
                    document.select("div[class=hot_tags] span a").stream().map(e -> {
                        return e.text();
                    }).collect(Collectors.toList()));
            Element ele4 = element.selectFirst("div[class=post_content]");
            if (ele4 == null) {
                ele4 = element.selectFirst("div[class=postcontent]");
            }
            if (ele4 == null) {
                ele4 = element.selectFirst("div[class=paragraph]");
            }
            bm.setContent(ele4.html());
            List<String> pu = new ArrayList<>();
            ele4.select("img").forEach(e -> {
                if (e.attr("src") != null) {
                    pu.add(e.attr("data-original"));
                }
            });
            if (!pu.isEmpty()) {
                bm.setPicUrl(pu);
            }
            List<String> vu = new ArrayList<>();
            ele4.select("iframe").forEach(e -> {
                if (e.attr("src") != null) {
                    vu.add(e.attr("src"));
                }
            });
            if (!vu.isEmpty()) {
                bm.setVideoUrl(vu);
            }
            list.add(bm);
            Elements elements = document.getElementsByTag("iframe");
            for (Element element2 : elements) {
                if (element2.hasAttr("data")) {
                    Outlink outlink = new Outlink(
                            "https://dyn.ithome.com/comment/" + element2.attr("data"));
                    outlink.addData("key", url);
                    list.add(outlink);
                    Outlink outlink1 = new Outlink(
                            "https://dyn.ithome.com/grade/" + element2.attr("data"));
                    outlink1.addData("key", url);
                    list.add(outlink1);
                }
            }

        } else if (url.matches("https://dyn.ithome.com/grade/\\d+")) {
            NewsModel bm = new NewsModel("ithome");
            bm.setUrl(url);
            if (!html.contains("gradestr")) {
                return list;
            }
            Document document = Jsoup
                    .parse(html.split("var gradestr = '")[1].split("';")[0]);
            bm.setKey(content.getValue("key").toString());
            bm.setScore(document.getElementsByClass("ss").text());
            Elements elements = document.select("div[class=bt] div");
            bm.setUp(Integer.valueOf(elements.get(2).text()));
            bm.setMiddle(Integer.valueOf(elements.get(1).text()));
            bm.setDown(Integer.valueOf(elements.get(0).text()));
            list.add(bm);

        } else if (url.startsWith("https://dyn.ithome.com/comment/")) {
            String str = html;
            String hash = str.split("ch11 = '")[1].split("';</")[0];
            String rvUrl = "https://dyn.ithome.com/api/comment/count?newsid="
                    + url.split("comment/")[1];
            Outlink outlink = new Outlink(rvUrl);
            outlink.addData("key", content.getValue("key").toString());
            outlink.addData("hash", hash);
            outlink.addData("postid", url.split("comment/")[1]);
            list.add(outlink);

        } else if (url
                .matches("https://dyn.ithome.com/api/comment/count\\?newsid=\\d+")) {
            int num = Integer.valueOf(html.split("innerHTML = '")[1].split("'")[0]);
            for (int i = 1; i <= num / 50 + 1; i++) {
                Outlink outlink = new Outlink(
                        "https://dyn.ithome.com/ithome/getajaxdata.aspx?postid="
                                + content.getValue("postid") + "&j=" + i);
                outlink.addData("key", content.getValue("key"));
                ExtendMap parm = new ExtendMap().postHttpType();
                parm.setRequestBody("newsID=" + content.getValue("postid") + "&hash="
                        + content.getValue("hash") + "&type=commentpage&page=" + i
                        + "&order=false");
                parm.setContentType(
                        "application/x-www-form-urlencoded; charset=UTF-8");
                outlink.addData(parm);
                list.add(outlink);
            }
        } else if (url.startsWith(
                "https://dyn.ithome.com/ithome/getajaxdata.aspx?postid")) {
            List<ReviewModel> rv = Lists.newArrayList();
            NewsModel bm = new NewsModel("ithome");
            bm.setKey(content.getValue("key").toString());
            bm.setUrl(url);

            Elements element1 = Jsoup.parse(html).select("li[class=entry]");
            element1.forEach(e -> {
                ReviewModel rm = new ReviewModel();
                rm.setReviewId(
                        e.selectFirst("span[class=nick] a").attr("title").split("：")[1]);
                rm.setReviewName(e.selectFirst("span[class=nick] a").text());
                rm.setReviewTime(
                        e.selectFirst("span[class=posandtime]").text().split(" ", 2)[1]);
                rm.setReviewContent(e.select("div[class=comm] p").text());
                rm.setReviewUp(Integer.valueOf(
                        e.select("a[class=s]").text().split("\\(")[1].split("\\)")[0]));
                rm.setReviewDown(Integer.valueOf(
                        e.select("a[class=a]").text().split("\\(")[1].split("\\)")[0]));
                List<ReviewModel> rvv = new ArrayList<>();
                e.select("ul[class=reply] li[class=gh]").forEach(e1 -> {
                    ReviewModel r = new ReviewModel();
                    r.setReviewId(e1.selectFirst("span[class=nick] a").attr("title")
                            .split("：")[1]);
                    r.setReviewName(e1.selectFirst("span[class=nick] a").text());
                    r.setReviewTime(
                            e1.select("span[class=posandtime]").text().split(" ", 2)[1]);
                    r.setReviewContent(e1.select("div[class=re_comm] p").text());
                    r.setReviewUp(
                            Integer.valueOf(e1.select("a[class=s]").text().split("\\(")[1]
                                    .split("\\)")[0]));
                    r.setReviewDown(
                            Integer.valueOf(e1.select("a[class=a]").text().split("\\(")[1]
                                    .split("\\)")[0]));
                    rvv.add(r);
                });
                if (!rvv.isEmpty()) {
                    rm.setReviewModels(rvv);
                }
                rv.add(rm);
            });
            if (!rv.isEmpty()) {
                bm.setReviews(rv);
                list.add(bm);
            }
        } else {
        }
        return list;
    }

    public static void main(String[] args) throws Exception {
        String url = "https://www.ithome.com/html/digi/365451.htm";

        ProtocolOutput output = ProtocolFactory.getProtocol(Jsoups.class.getName()).getProtocolOutput(url, null);
        IthomeNewsParse parse = new IthomeNewsParse();
        List<Object> list = parse.parse(url, output.getContent());
        for (Object o : list) {
            System.out.println(new Gson().toJson(o));
        }

    }

}
