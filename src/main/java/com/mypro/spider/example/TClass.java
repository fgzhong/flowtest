package com.mypro.spider.example;

import org.apache.hadoop.conf.Configuration;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/2/11
 */
public class TClass {

    private static final Class<?>[] EMPTY_ARRAY = new Class[0];
    private static final Map<Class<?>, Constructor<?>> CONSTRUCTOR_CACHE = new ConcurrentHashMap();

//    public static <T> T newInstance(Class<T> theClass, Configuration conf) {
//        T result;
//        try {
//            Constructor<T> meth = (Constructor)CONSTRUCTOR_CACHE.get(theClass);
//            if (meth == null) {
//                meth = theClass.getDeclaredConstructor(EMPTY_ARRAY);
//                meth.setAccessible(true);
//                CONSTRUCTOR_CACHE.put(theClass, meth);
//            }
//
//            result = meth.newInstance();
//        } catch (Exception var4) {
//            throw new RuntimeException(var4);
//        }
//
//        return result;
//    }

    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<K, V>();
    }
}
