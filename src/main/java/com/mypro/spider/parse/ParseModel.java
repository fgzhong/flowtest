package com.mypro.spider.parse;

import com.alibaba.fastjson.JSON;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/2/23
 */
public interface ParseModel {

    public static String toJson(ParseModel model) {
        return JSON.toJSONString(model);
    }

}
