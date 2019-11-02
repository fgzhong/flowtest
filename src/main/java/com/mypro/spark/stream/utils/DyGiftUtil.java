package com.mypro.spark.stream.utils;

import com.google.common.collect.Maps;
import com.mypro.spark.stream.model.DyGiftModel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/8/23
 */
public class DyGiftUtil {

    private final static Map<String, DyGiftModel> dyGiftModels = Maps.newConcurrentMap();

    public static DyGiftModel getDyGiftModel(String node, String id) {
        if (dyGiftModels.containsKey(id)) {
            return dyGiftModels.get(id);
        }
        DyGiftModel model = EsSearchUtil.getGiftModel(node, id);
        if (model != null) {
            dyGiftModels.put(id, model);
        }
        return model;
    }
}
