package com.mypro.spider.parse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/2/23
 */
public class ParseFactory {

    private static final Logger LOG = LoggerFactory
            .getLogger(MethodHandles.lookup().lookupClass());

    private final static Map<String, Parse> PARSE_CONTAINER = Maps.newHashMap();
    private final static List<String> PARSE_FAIL = Lists.newArrayList();

    public static List<Object> getParse(Content content) throws Exception{
        String parseClass = content.getParseClass();
        if (PARSE_FAIL.contains(parseClass)) { return null; }
        if (!PARSE_CONTAINER.containsKey(parseClass)) {
            synchronized (PARSE_CONTAINER) {
                if (!PARSE_CONTAINER.containsKey(parseClass)) {
                    try {
                        Parse parse = (Parse) Class.forName(parseClass).newInstance();
                        PARSE_CONTAINER.put(parseClass, parse);
                    } catch (Exception e) {
                        PARSE_FAIL.add(parseClass);
                        LOG.error(" 反编译 parse class 失败， class name ：{} ", parseClass);
                    }
                }
            }
        }
        return PARSE_CONTAINER.get(parseClass).parse(content.getUrl(), content);
    }


}
