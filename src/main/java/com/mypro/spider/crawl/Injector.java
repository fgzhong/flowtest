package com.mypro.spider.crawl;

import org.apache.hadoop.util.Tool;
import com.mypro.spider.utils.SpiderTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Map;

/**
 * @author fgzhong
 * @since 2019/1/9
 */
public class Injector extends SpiderTool implements Tool {

    private static final Logger _LOG = LoggerFactory
            .getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public int run(String[] strings) throws Exception {
        getConf();
        return 0;
    }

    @Override
    public Map<String, Object> run(Map<String, Object> args, String crawlId) throws Exception {
        return null;
    }
}
