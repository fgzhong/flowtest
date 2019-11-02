package com.mypro.spider.crawl.hdfs;

import com.mypro.spider.utils.SpiderTool;
import org.apache.hadoop.util.Tool;

import java.util.Map;

/**
 * @author fgzhong
 * @since 2019/1/9
 */
public class HdfsInjector extends SpiderTool implements Tool {
    @Override
    public int run(String[] strings) throws Exception {
        return 0;
    }

    @Override
    public Map<String, Object> run(Map<String, Object> args, String crawlId) throws Exception {
        return null;
    }
}
