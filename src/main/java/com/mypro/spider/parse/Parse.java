package com.mypro.spider.parse;

import java.util.List;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/1/28
 */
public interface Parse {

    List<Object> parse(String url, Content content) throws Exception;

}
