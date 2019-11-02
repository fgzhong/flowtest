package com.mypro.math.sort;

import com.mypro.math.BaseUtil;

/**
 * @author fgzhong
 * @description: 插入排序，优于选择排序，更适用于近乎有序的数组
 * @since 2019/4/9
 */
public class InsertionSort {


    /* O(n2) 从最小数组集开始，从后往前比较相邻两组数据，小于互换，大于退出，*/
    private static void baseSort(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = i; j > 0; j--) {
                if (arr[j] < arr[j-1]) {
                    BaseUtil.swap(arr, j, j-1);
                } else {
                    break;
                }
            }
        }
    }

    /* O(n2) 从最小数组集开始，最后一位与之前顺序比较，小于前一位往后移动，大于退出把拿出的放到该位置，*/
    private static void oneSort(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            int tmp = arr[i];
            int j;
            for (j = i; j > 0; j--) {
                if (tmp < arr[j-1]) {
                    arr[j] = arr[j-1];
                } else {
                    break;
                }
            }
            arr[j] = tmp;
        }

    }



    public static void main(String[] args) throws Exception{
        int[] a = BaseUtil.gen(10000, 1, 50000);
        long startTime = System.currentTimeMillis();
        baseSort(a);
        System.out.println("use time : " + (System.currentTimeMillis() - startTime));
        for (int i = 0; i < a.length; i++) {
            System.out.print(a[i] + " ");
        }
        BaseUtil.check(a);
    }

}
