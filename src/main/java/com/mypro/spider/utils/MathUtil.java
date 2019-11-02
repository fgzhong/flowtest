package com.mypro.spider.utils;

import java.util.Random;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/5/17
 */
public class MathUtil {

    private final static Random RANDOM = new Random();
    private final static String[] v = new String[]{"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};

    public static String getHex(){
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < 32; i++) {
            buffer.append(v[RANDOM.nextInt(v.length)]);
        }
        return buffer.toString();
    }


}
