package com.mypro.spider.example.example2pro.parse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.mypro.spider.example.example2pro.model.ReviewModel;
import com.mypro.spider.example.example2pro.model.ProductModel;
import com.mypro.spider.parse.Content;
import com.mypro.spider.parse.Outlink;
import com.mypro.spider.parse.Parse;

import com.mypro.spider.protocol.ProtocolFactory;
import com.mypro.spider.protocol.ProtocolOutput;
import com.mypro.spider.protocol.httputil.jsoup.Jsoups;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author fgzhong
 * @since 2019/3/1
 */
public class JdProductParse implements Parse {


    private static final String HOME_PAGE = "http://list.jd.com/list.html\\?cat=\\d+,\\d+,\\d+&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0";
    private static final String LIST_PAGE_1 = "https://list.jd.com/list.html\\?cat=\\d+,\\d+,\\d+&page=\\d+&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0";
    private static final String LIST_PAGE_2 = "https://list.jd.com/list.html\\?cat=\\d+,\\d+,\\d+&page=\\d+&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0#J_main";
    private static final String DETAIL_PAGE_LIST_1 = "http://item.jd.com/\\d+.html\\?1";
    private static final String DETAIL_PAGE_LIST_2 = "http://item.jd.com/\\d+.html\\?2";
    private static final String DETAIL_PAGE_LIST_2_1 = "http://item.jd.com/%s.html?2";
    private static final String DETAIL_PAGE = "https://item.jd.com/\\d+.html";
    private static final String DETAIL_PAGE_1 = "https://item.jd.com/%s.html";

    private static final String REVIEW_PAGE_LIST_1 = "http://club.jd.com/comment/skuProductPageComments.action?productId=%s&score=0&sortType=5&page=0&pageSize=10&isShadowSku=0&fold=1";
    private static final String REVIEW_PAGE_LIST = "http://club.jd.com/comment/skuProductPageComments.action\\?productId=\\d+&score=0&sortType=5&page=0&pageSize=10&isShadowSku=0&fold=1";
    private static final String REVIEW_PAGE = "https://club.jd.com/comment/skuProductPageComments.action\\?productId=\\d+&score=0&sortType=5&page=\\d+&pageSize=10&isShadowSku=0&fold=1";

    private static final String PRICE = "https://p.3.cn/prices/mgets\\?skuIds=J_\\d+&type=1";


    @Override
    public List<Object> parse(String url, Content content) throws Exception {
        List<Object> list = Lists.newArrayList();
        String html = new String(content.getHtml(), "gbk");
        if (url.matches(HOME_PAGE)) {
            Document document = Jsoup.parse(html);
            String page = document.selectFirst("span[class=fp-text] i").text();
            for (int i = 1; i <= Integer.valueOf(page); i++) {
                Outlink outlink = new Outlink(url.replace("page=1", "page=" + i).replaceAll("http", "https"));
                list.add(outlink);
            }
        } else if (url.matches(LIST_PAGE_1) || url.matches(LIST_PAGE_2)) {
            Document document = Jsoup.parse(html);
            Elements elements = document.select("div[id=plist]>ul>li");
            for (Element element : elements) {
                Outlink outlink = new Outlink("http:"
                        + element.selectFirst("a[target=_blank]").attr("href") + "?1");
                list.add(outlink);
            }
        } else if (url.matches(DETAIL_PAGE_LIST_1)) {
            Document document = Jsoup.parse(html);
            Elements elements = document.select("div[id=choose-attrs]");
            if (!elements.isEmpty()) {
                Elements eles = document.select("div[id=choose-attrs]>div");
                eles.remove(eles.last());
                if (eles.size() >= 1) {
                    for (Element ele : eles) {
                        Elements es = ele.select("div[class=dd]>div");
                        for (Element e : es) {
                            Outlink outlink = new Outlink(
                                    String.format(DETAIL_PAGE_LIST_2_1, e.attr("data-sku")));
                            list.add(outlink);
                        }
                    }
                } else {
                    Outlink outlink = new Outlink(
                            url.replace("http", "https").split("\\?")[0]);
                    list.add(outlink);
                }
            } else {
                Outlink outlink = new Outlink(
                        url.replace("http", "https").split("\\?")[0]);
                list.add(outlink);
            }
        } else if (url.matches(DETAIL_PAGE_LIST_2)) {
            Document document = Jsoup.parse(html);
            Elements elements = document.select("div[id=choose-attrs]");
            if (!elements.isEmpty()) {
                Elements eles = document.select("div[id=choose-attrs]>div");
                eles.remove(eles.last());
                for (Element ele : eles) {
                    Elements es = ele.select("div[class=dd]>div");
                    for (Element e : es) {
                        Outlink outlink = new Outlink(
                                String.format(DETAIL_PAGE_1, e.attr("data-sku")));
                        list.add(outlink);
                    }
                }
            }
        } else if (url.matches(DETAIL_PAGE)) {

            ProductModel pm = new ProductModel("jd");
            pm.setProductUrl(url);
            pm.setWeb("jd");

            Document document = Jsoup.parse(html);

            List<String> list2 = Lists.newArrayList();
            list2.add(document
                    .select("div[class=crumb fl clearfix] div[class=item first]")
                    .text());
            Elements types = document
                    .select("div[class=crumb fl clearfix] div[class=item]");
            for (Element element : types) {
                if (element.childNodeSize() == 1) {
                    list2.add(element.text());
                } else {
                    list2.add(element.select("div[class=head]").text());
                }
            }
            list2.add(document
                    .select("div[class=crumb fl clearfix] div[class=item ellipsis]")
                    .text());
            pm.setProductRoute(list2);
            pm.setProductType(document.select("div[class=sku-name]").text());
            pm.setProductName(list2.get(list2.size() - 1));
            if (document.getElementById("choose-attrs") != null) {
                pm.setProductModel(document.getElementById("choose-attrs")
                        .getElementsByAttributeValue("class", "item  selected").text());
            }
            List<String> pp = Lists.newArrayList();
            Element imgEles = document.getElementById("spec-list");
            if (imgEles != null) {
                imgEles.getElementsByTag("img").forEach(e -> pp.add("https:" + e.attr("src")));
            }
            pm.setThumbnail(pp);
            Elements elements = document.getElementsByClass("Ptable-item");
            Map<String, String> pMap = Maps.newHashMap();
            for (Element element : elements) {
                pMap.put(element.selectFirst("h3").text(),
                        element.selectFirst("dl").text());
            }
            pm.setProductParameters(pMap);
            //获取重要参数
            Elements elementsMain = document.select(".parameter1.p-parameter-list p");
            Map<String, String> pMapMain = Maps.newHashMap();
            for (Element element : elementsMain) {
                pMapMain.put(element.text().split("：")[0], element.text().split("：")[1]);
            }
            Elements elementsMain2 = document.select(".parameter2.p-parameter-list li");
            for (Element element : elementsMain2) {
                pMapMain.put(element.text().split("：")[0], element.text().split("：")[1]);
            }
            pm.setMainparam(pMapMain);
            Elements imageE = document.select("div[id=J-detail-content] img");
            List<String> urls = Lists.newArrayList();
            for (Element e : imageE) {
                urls.add(e.attr("src"));
            }
            pm.setProductPicUrl(urls);
            list.add(pm);

            Outlink o1 = new Outlink(String.format(REVIEW_PAGE_LIST_1,
                    url.split("com/")[1].split(".html")[0]));
            o1.addData("key", url);
            list.add(o1);
            Outlink o2 = new Outlink(
                    String.format("https://p.3.cn/prices/mgets?skuIds=J_%s&type=1",
                            url.split("com/")[1].split(".html")[0]));
            o2.addData("key", url);
            list.add(o2);
        } else if (url.matches(PRICE)) {
            ProductModel bm = new ProductModel("jd");
            bm.setKey(content.getValue("key").toString());
            bm.setProductUrl(url);
            Document doc = Jsoup.parse(new String(content.getHtml(), "utf-8"));
            String text = doc.select("body").text();
            JSONArray jsonArray = new JSONArray(text);
            String price = jsonArray.getJSONObject(0).getString("p");
            bm.setProductValue(price);
            list.add(bm);
        }
        if (url.matches(REVIEW_PAGE_LIST)) {

            ProductModel bm = new ProductModel("jd");
            bm.setKey(content.getValue("key").toString());
            bm.setProductUrl(url);
            JSONObject js = new JSONObject(html);
            bm.setProductScore(String.valueOf(
                    js.getJSONObject("productCommentSummary").getInt("goodRateShow")));
            List<String> productTag = Lists.newArrayList();
            JSONArray jsonArray = js.getJSONArray("hotCommentTagStatistics");
            for (int i = 0; i < jsonArray.length(); i++) {
                productTag.add(jsonArray.getJSONObject(i).getString("name"));
            }
            if (!productTag.isEmpty()) {
                bm.setProductTag(productTag);
            }
            list.add(bm);

            if (js.has("maxPage")) {
                int page = js.getInt("maxPage");
                for (int i = 0; i < page; i++) {
                    Outlink o1 = new Outlink(
                            url.replace("http", "https").replace("page=0", "page=" + i));
                    o1.addData("key", content.getValue("key").toString());
                    list.add(o1);
                }
            }
        } else if (url.matches(REVIEW_PAGE)) {
            List<ReviewModel> reviewModels = Lists.newArrayList();

            ProductModel bm = new ProductModel("jd");
            bm.setKey(content.getValue("key").toString());
            bm.setProductUrl(url);
            JSONArray jsonArray = new JSONObject(html).getJSONArray("comments");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ReviewModel model = new ReviewModel();
                model.setReviewTime(jsonObject.getString("creationTime"));
                model.setReviewName(jsonObject.getString("nickname"));
                model.setReviewScore(String.valueOf(jsonObject.getInt("score")));
                model.setReviewContent(jsonObject.getString("content"));
                model.setReviewId(String.valueOf(jsonObject.getString("id")));
                if (jsonObject.has("videos")) {
                    List<String> vu = Lists.newArrayList();
                    JSONArray js = jsonObject.getJSONArray("videos");
                    for (int j = 0; j < js.length(); j++) {
                        vu.add(js.getJSONObject(j).getString("remark"));
                    }
                    if (!vu.isEmpty()) {
                        model.setReviewVideoUrl(vu);
                    }
                }
                if (jsonObject.has("images")) {
                    List<String> vu = Lists.newArrayList();
                    JSONArray js = jsonObject.getJSONArray("images");
                    for (int j = 0; j < js.length(); j++) {
                        vu.add(js.getJSONObject(j).getString("imgUrl"));
                    }
                    if (!vu.isEmpty()) {
                        model.setReviewVideoUrl(vu);
                    }
                }
                if (jsonObject.has("afterUserComment")) {
                    ReviewModel model1 = new ReviewModel();
                    model1.setReviewTime(jsonObject.getJSONObject("afterUserComment")
                            .getString("created"));
                    model1.setReviewContent(jsonObject.getJSONObject("afterUserComment")
                            .getJSONObject("hAfterUserComment").getString("content"));
                    model.setReviewModels(Arrays.asList(model1));
                }
                reviewModels.add(model);
            }

            bm.setProductReview(reviewModels);
            list.add(bm);
        }
        return list;
    }

    public static void main(String[] args) throws Exception {
//        private static final String REVIEW_PAGE = "https://club.jd.com/comment/skuProductPageComments.action\\?productId=\\d+&score=0&sortType=5&page=\\d+&pageSize=10&isShadowSku=0&fold=1";

        String url = "http://club.jd.com/comment/skuProductPageComments.action?productId=24384605547&score=0&sortType=5&page=0&pageSize=10&isShadowSku=0&fold=1";
        ProtocolOutput output = ProtocolFactory.getProtocol(Jsoups.class.getName()).getProtocolOutput(url, null);
        JdProductParse parse = new JdProductParse();
        List<Object> list = parse.parse(url, output.getContent());
        for (Object o : list) {
            System.out.println(new Gson().toJson(o));
        }
    }

}
