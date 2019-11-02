package com.mypro.spider.utils;

import com.google.common.collect.Maps;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fgzhong
 * @description: 反射，并缓存，提高性能
 * @since 2019/2/20
 */
public class Records {

    private static final Class<?>[] EMPTY_ARRAY = new Class[0];
    private static final Map<Class<?>, Constructor<?>> CONSTRUCTOR_CACHE = Maps.newConcurrentMap();

    public static <T> T newInstance(Class<T> theClass) {
        T model;
        try {
            Constructor<T> meth = (Constructor<T>)CONSTRUCTOR_CACHE.get(theClass);
            if (meth == null) {
                meth = theClass.getDeclaredConstructor(EMPTY_ARRAY);
                meth.setAccessible(true);
                CONSTRUCTOR_CACHE.put(theClass, meth);
            }
            model = meth.newInstance();
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }

        return model;
    }

    public static <T> T newInstance(Class<T> theClass, Configuration conf) {
        T model;
        try {
            Constructor<T> meth = (Constructor<T>) CONSTRUCTOR_CACHE.get(theClass);
            if (meth == null) {
                meth = theClass.getDeclaredConstructor(EMPTY_ARRAY);
                meth.setAccessible(true);
                CONSTRUCTOR_CACHE.put(theClass, meth);
            }
            model = meth.newInstance();
            setConf(model, conf);
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }

        return model;
    }

    private static void setConf(Object theObject, Configuration conf) {
        if (conf != null) {
            if (theObject instanceof Configurable) {
                ((Configurable) theObject).setConf(conf);
            }
        }
    }


}
