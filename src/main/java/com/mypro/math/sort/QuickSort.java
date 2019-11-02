package com.mypro.math.sort;

import com.mypro.math.BaseUtil;

import java.util.Random;

/**
 * @author fgzhong
 * @description: 快速排序 ———— 分治算法
 * @since 2019/4/10
 */
public class QuickSort {

    private final static Random RANDOM = new Random();

    /* O(nlogn) */
    private static void baseSort(int[] arr,int l, int r) {
        if (l>=r) {
            return;
        }
        int mid = partition(arr, l, r);
        baseSort(arr,l ,mid-1 );
        baseSort(arr,mid+1, r );
    }

//    public static int partition(int[] arr,int l, int r) {
//        // 优化1, 随机指定目标点
//        BaseUtil.swap(arr, l, RANDOM.nextInt(r-l) + l);
//        int tmp = arr[l];
//        int j=l;
//        for (int i=l+1;i <= r; i++) {
//            if (arr[i] < tmp) {
//                BaseUtil.swap(arr, i, ++j);
//            }
//        }
//        BaseUtil.swap(arr, l, j);
//        return j;
//    }

    private static void baseSortWithThree(int[] arr,int l, int r) {
        if (l>=r) {
            return;
        }
        int[] mid = partitionWithThree(arr, l, r);
        baseSort(arr,l ,mid[0]-1 );
        baseSort(arr,mid[1], r );
    }

    public static int partition(int[] arr,int l, int r) {
        // 优化1, 随机指定目标点
        BaseUtil.swap(arr, l, RANDOM.nextInt(r-l) + l);
        int tmp = arr[l];
        int j=l;
        for (int i=l+1;i <= r; i++) {
            if (arr[i] < tmp) {
                BaseUtil.swap(arr, i, ++j);
            }
        }
        BaseUtil.swap(arr, l, j);
        return j;
    }

    public static int partition2(int[] arr,int l, int r) {
        BaseUtil.swap(arr, l, RANDOM.nextInt(r-l) + l);
        int tmp = arr[l];
        int i=l+1,j=r;
        while (i<j) {
            if (arr[i] < tmp) {
                i++;
            } else if (arr[j] > tmp) {
                j--;
            } else {
                BaseUtil.swap(arr,i++,j--);
            }
        }
        BaseUtil.swap(arr, l, j);
        return j;
    }

    /**
     * 三路快排：普通快排分为两部分 <[=] tmp 和 >= tmp ;
     *          三路快排分为三部分 < tmp 、= tmp 和 > tmp ;
     * @param arr
     * @param l
     * @param r
     * @return
     */
    public static int[] partitionWithThree(int[] arr,int l, int r) {
        // 优化1, 随机指定目标点
        BaseUtil.swap(arr, l, RANDOM.nextInt(r-l) + l);
        int tmp = arr[l];
        int lt = l, gt = r;
        for (int i = l+1; i < gt; ) {
            if (arr[i] < tmp) {
                BaseUtil.swap(arr, ++lt, i++);
            } else if (arr[i] > tmp) {
                BaseUtil.swap(arr, --gt, i);
            } else {
                i++;
            }
        }
        BaseUtil.swap(arr, lt, l);
        return new int[]{lt,gt};
    }


    public static void main(String[] args) {
        int[] a = BaseUtil.gen(10000, 1, 50000);
        long startTime = System.currentTimeMillis();
        baseSortWithThree(a,0, a.length-1);
        System.out.println("use time : " + (System.currentTimeMillis() - startTime));
        for (int i = 0; i < Math.min(a.length, 100); i++) {
            System.out.print(a[i] + " - ");
        }
        BaseUtil.check(a);
    }

}
