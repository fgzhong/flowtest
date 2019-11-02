package com.mypro.view.java;

import java.util.HashMap;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/7/31
 */
public class SingleTon {

    private static SingleTon singleTon;
    private HashMap<String, String> data;

    private SingleTon() {
        this.data = new HashMap<>();
        this.data.put("1","2");
    }

    public static SingleTon getInstance(){
        if (singleTon == null) {
            synchronized (SingleTon.class) {
                if (singleTon == null) {
                    singleTon = new SingleTon();
                }
            }
        }
        return singleTon;
    }

    public String get(String key) {
        return this.data.get(key);
    }
}
