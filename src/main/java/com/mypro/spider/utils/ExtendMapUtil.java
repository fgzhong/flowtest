package com.mypro.spider.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.mypro.spider.data.ExtendMap;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.cglib.beans.BeanMap;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * @author fgzhong
 * @description: map 与 ExtendMap 互转
 * @since 2019/2/21
 */
public class ExtendMapUtil {

    private final static ObjectMapper mapper = new ObjectMapper();

    public static Map<String, String> getHeaderMap(ExtendMap map) {
        Map<String, String> map1 = null;
        try {
            map1 = (Map<String, String>) mapper.readValue(mapper.writeValueAsString(map), Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map1;
    }

    public static Map<String, String> getAllMap(ExtendMap map) {
        Map<String, String> map1 = Maps.newHashMap();
        try {
            BeanMap beanMap = BeanMap.create(map);
            for (Object key : beanMap.keySet()) {
                if(beanMap.get(key) != null) {
                    map1.put(key.toString(), beanMap.get(key).toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map1;
    }


    public static ExtendMap mapToExtendMap(Map map) {
        ExtendMap extendMap = new ExtendMap();
        try {
            if (map != null && map.size() != 0) {
                BeanMap beanMap = BeanMap.create(extendMap);
                beanMap.putAll(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return extendMap;
    }

}
