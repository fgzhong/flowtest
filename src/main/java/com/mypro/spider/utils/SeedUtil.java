package com.mypro.spider.utils;

import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/3/1
 */
public class SeedUtil {

//    private static String ip = "localhost";
    private static String ip = "192.168.1.88";
    private static String index = "weibo";
    private static String type = "_doc";
    private static String jdSeed = "{\"url\":\"%s\",\"uid\":\"%s\",\"status\":0,\"level\":0,\"depth\":0,\"retries\":0,\"parseClass\":\"%s\",\"mapWritableStr\" : { \"httpMethodClassName\" : \"com.mypro.spider.protocol.httputil.jsoup.Jsoups\" }}";

    public static void main(String[] args) throws Exception{
//        addWWbSeeds("com.mypro.spider.example.example2pro.parse.PcpopNewsParse", "http://www.pcpop.com/");
//        addWWbSeeds("com.mypro.spider.example.example2pro.parse.IthomeNewsParse", "https://www.ithome.com/sitemap/");
//        addWWbSeeds("com.mypro.spider.example.example2pro.parse.EngadgetNewsParse", "http://cn.engadget.com/");
//        addWWbSeeds("com.mypro.spider.example.example2pro.parse.CnbetaNewsParse", "https://www.cnbeta.com/topics.htm");
//        addWWbSeeds("com.mypro.spider.example.example2pro.parse.ChiphellBBSParse", "https://www.chiphell.com/forum.php");
        addWWbSeeds("com.mypro.spider.example.example2pro.parse.WeiBoParse", "https://weibo.com/lizexipablo");
        addWWbSeeds("com.mypro.spider.example.example2pro.parse.WeiBoParse", "https://weibo.com/techmessager");
        addWWbSeeds("com.mypro.spider.example.example2pro.parse.WeiBoParse", "https://weibo.com/webthinker123");
        addWWbSeeds("com.mypro.spider.example.example2pro.parse.WeiBoParse", "https://weibo.com/703723424");
        addWWbSeeds("com.mypro.spider.example.example2pro.parse.WeiBoParse", "https://weibo.com/206800072");
        addWWbSeeds("com.mypro.spider.example.example2pro.parse.WeiBoParse", "http://weibo.com/Gaojiw");
        addWWbSeeds("com.mypro.spider.example.example2pro.parse.WeiBoParse", "http://weibo.com/836812865");
        addWWbSeeds("com.mypro.spider.example.example2pro.parse.WeiBoParse", "http://weibo.com/234378688");
        addWWbSeeds("com.mypro.spider.example.example2pro.parse.WeiBoParse", "http://weibo.com/517012284");
        addWWbSeeds("com.mypro.spider.example.example2pro.parse.WeiBoParse", "http://weibo.com/273300598");
        addWWbSeeds("com.mypro.spider.example.example2pro.parse.WeiBoParse", "https://weibo.com/shumaC");
        addWWbSeeds("com.mypro.spider.example.example2pro.parse.WeiBoParse", "http://weibo.com/315141777");
        addWWbSeeds("com.mypro.spider.example.example2pro.parse.WeiBoParse", "http://weibo.com/u/3030737153");
        addWWbSeeds("com.mypro.spider.example.example2pro.parse.WeiBoParse", "https://weibo.com/ixap");
//        addSeeds();
//        getDocsNum();
    }

    public static long getDocsNum() throws Exception{
        RestHighLevelClient client =  new RestHighLevelClient(
                RestClient.builder(new HttpHost(ip, 9200, "http")));
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termsQuery("status", "0","4"))
                .must(QueryBuilders.rangeQuery("retries").lte(10));
        sourceBuilder.query(queryBuilder);
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.source(sourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        long totalHits = response.getHits().getTotalHits();
        System.out.println(totalHits);
        client.close();
        return totalHits;
    }


    public static void addWWbSeeds(String a, String b1) throws Exception {
        String pclass = a;
        RestHighLevelClient client =  new RestHighLevelClient(
                RestClient.builder(new HttpHost(ip, 9200, "http")));
        BulkRequest request = new BulkRequest();
        List<String> s = Arrays.asList(b1);
        for (String e : s) {
            String b = UrlLengthUtil.shortenCodeUrl(e);
            request.add(new IndexRequest(index, type, b)
                    .source(String.format(jdSeed, e,b,
                            pclass), XContentType.JSON));
        }

        try {
            client.bulk(request, RequestOptions.DEFAULT);
        } finally {
            client.close();
        }
    }


    public static void addSeeds() throws Exception {
        String pclass = "com.mypro.spider.example.example2pro.parse.JdProductParse";
        RestHighLevelClient client =  new RestHighLevelClient(
                RestClient.builder(new HttpHost(ip, 9200, "http")));
        BulkRequest request = new BulkRequest();
        List<String> s = Arrays.asList("http://list.jd.com/list.html?cat=9987,653,655&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=9987,653,659&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=9987,830,13658&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=9987,830,860&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=9987,830,863&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=9987,830,13659&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=9987,830,862&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=9987,830,867&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=9987,830,1099&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=9987,830,13660&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=9987,830,13661&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=9987,830,866&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=9987,830,864&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=9987,830,13657&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=9987,830,868&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=9987,830,11301&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=9987,830,11302&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=9987,830,12809&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=9987,830,12811&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,794,798&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,794,870&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,794,878&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,794,880&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,794,823&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,794,965&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,794,1199&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,794,12392&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,794,877&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,794,12524&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,794,12525&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,794,12526&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,794,12527&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,794,12401&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,13297,13298&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,13297,1300&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,13297,13690&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,13297,13691&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,13297,18577&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,13297,18578&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,13297,1301&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,13297,13117&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,752,755&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,752,13116&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,752,753&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,752,881&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,752,756&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,752,761&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,752,758&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,752,759&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,752,757&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,752,899&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,752,902&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,752,762&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,752,9249&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,752,760&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,752,882&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,752,754&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,752,13118&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,752,901&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,752,803&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,752,12397&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,752,12398&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,738,747&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,738,749&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,738,748&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,738,12394&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,738,745&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,738,1279&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,738,1052&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,738,806&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,738,897&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,738,1283&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,738,12395&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,738,801&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,738,751&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,738,1278&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,738,825&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,738,12396&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,738,898&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,738,750&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,1276,739&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,1276,742&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,1276,741&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,1276,740&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,1276,795&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,1276,1287&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,1276,12400&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,1276,1291&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,1276,967&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,1276,963&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=9192,9197,12189&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,1276,1289&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=9192,9197,12187&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=9192,9197,12588&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,1276,1290&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=9855,9857,17489&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=737,738,14383&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=9855,9857,17490&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,654,831&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,654,5012&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,654,832&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,654,833&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,654,7170&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,654,12342&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,654,834&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,654,12343&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,654,12344&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,654,844&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,829,845&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,829,846&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,829,835&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,829,836&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,829,847&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,829,848&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,829,851&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,829,10971&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,829,10972&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,829,854&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,829,12810&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,12345,12347&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,12345,12348&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,12345,12349&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,12345,12350&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,12345,12351&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,12345,12352&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,12345,12353&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,12345,12354&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,12345,12355&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,12345,12806&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,12345,12807&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,828,837&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,828,842&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,828,13662&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,828,841&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,828,12808&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,828,869&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,828,962&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,828,5270&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,12346,12358&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,12346,12357&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,12346,12359&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,12346,840&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,12346,1203&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,12346,838&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=652,12346,12356&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,671,672&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,671,2694&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,671,5146&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,671,673&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,671,674&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,671,675&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,671,12798&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,677,678&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,677,681&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,677,679&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,677,683&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,677,11303&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,677,680&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,677,687&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,677,691&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,677,688&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,677,684&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,677,682&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,677,5008&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,677,5009&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,677,11762&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,686,693&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,686,694&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,686,690&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,686,689&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,686,826&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,686,692&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,686,698&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,686,695&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,686,1047&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,686,1049&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,686,1048&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,686,696&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,686,1051&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,686,12799&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,12800,12801&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,12800,12802&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,12800,12803&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,12800,12805&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,699,700&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,699,701&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,699,702&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,699,983&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,699,1098&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,699,11304&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,699,12370&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,716,722&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,716,5010&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,716,720&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,716,717&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,716,718&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,716,725&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,716,721&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,716,719&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,716,723&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,716,724&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,716,7373&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,716,7375&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,716,2601&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,716,4839&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,716,7374&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,729,730&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,729,731&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,729,733&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,729,736&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,729,7372&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,729,728&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=670,729,4838&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=6728,6740,11867&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=6728,6740,9959&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=6728,6740,6964&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=6728,6740,9961&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=6728,6740,9962&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=6728,6740,6965&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=6728,6740,6807&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=6728,6740,6749&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=6728,6740,12409&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=6728,6740,13247&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=6728,6740,6752&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=6728,6740,13248&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=6728,6740,6753&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=6728,6740,13249&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0",
                "http://list.jd.com/list.html?cat=6728,6740,13250&page=1&stock=0&sort=sort_winsdate_desc&trans=1&JL=4_7_0");
        for (String e : s) {
            String b = UrlLengthUtil.shortenCodeUrl(e);
            request.add(new IndexRequest(index, type, b)
                    .source(String.format(jdSeed, e,b,
                            pclass), XContentType.JSON));
        }

        client.bulk(request, RequestOptions.DEFAULT);
        client.close();
    }

}
