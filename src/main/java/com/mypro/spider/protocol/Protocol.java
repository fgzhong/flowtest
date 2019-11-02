package com.mypro.spider.protocol;

import java.util.Map;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/1/29
 */
public interface Protocol {

    ProtocolOutput getProtocolOutput(String url, Map<String, Object> map);

}
