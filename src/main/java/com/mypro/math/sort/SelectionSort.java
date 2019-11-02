package com.mypro.math.sort;

import com.mypro.math.BaseUtil;

/**
 * @author fgzhong
 * @description: 选择排序，遍历整个数组，选出最小数所在位置与第一位互换
 * @since 2019/4/9
 */
public class SelectionSort {

    /* O(n2) */
    private static void baseSort(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            int min = i;
            for (int j = i+1; j < arr.length; j++) {
                if (arr[j] < arr[min]) {
                    min = j;
                }
            }
            BaseUtil.swap(arr, i, min);
        }

    }



    public static void main(String[] args) throws Exception{
        int[] a = BaseUtil.gen(10000, 1, 10000000);
        long startTime = System.currentTimeMillis();
        baseSort(a);
        System.out.println("use time : " + (System.currentTimeMillis() - startTime) + "ms");
        for (int i = 0; i < Math.min(a.length, 100); i++) {
            System.out.print(a[i] + " ");
        }
        BaseUtil.check(a);
    }

}
