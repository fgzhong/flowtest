package com.mypro.math.sort;

import com.mypro.math.BaseUtil;

/**
 * @author fgzhong
 * @description: 冒泡排序
 * @since 2019/4/9
 */
public class BubbleSort {

    /* O(n2) */
    private static void baseSort(int[] arr) {


    }



    public static void main(String[] args) throws Exception{
        int[] a = BaseUtil.gen(1000, 1, 50000);
        long startTime = System.currentTimeMillis();
        baseSort(a);
        System.out.println("use time : " + (System.currentTimeMillis() - startTime));
        for (int i = 0; i < a.length; i++) {
            System.out.print(a[i] + " ");
        }
        BaseUtil.check(a);
    }

}
