package com.mypro.math;

import java.util.Random;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/4/9
 */
public class BaseUtil {


    public static int[] gen(int n, int rangeL, int rangeR) {
        Random random = new Random();
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = random.nextInt(rangeR - rangeL + 1) + rangeL;
        }
        return arr;
    }

    public static void swap(int[] arr, int i, int j) {
        int arrI = arr[i];
        arr[i] = arr[j];
        arr[j] = arrI;
    }

    public static void check(int[] arr){
        for (int i = 0; i < arr.length-2 ; i++) {
            if (arr[i] > arr[i+1]) {
                System.out.println(" \n排序失败 : i = " + i + " " + arr[i]);
                break;
            }
        }
    }

}
