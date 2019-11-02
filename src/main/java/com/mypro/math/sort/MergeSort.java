package com.mypro.math.sort;

import com.mypro.math.BaseUtil;

/**
 * @author fgzhong
 * @description: 归并排序 ———— 分治算法
 * @since 2019/4/9
 */
public class MergeSort {

    /* O(nlogn) */
    private static void baseSort(int[] arr,int l, int r) {
        if (l>=r) {
            return;
        }
        // 当l-r小于某一小值时，可用插入排序

        int mid = l + (r-l)/2;
        baseSort(arr, l, mid);
        baseSort(arr, mid+1, r);
        if (arr[mid] > arr[mid+1]) {
            merge(arr, l, mid, r);
        }
    }

    /**
     *
     * 不通过索引直接获取元素，适合链表排序
     * @param arr
     * @param n
     */
    private static void bottomToTop(int[] arr,int n) {
        for (int i = 0; i < n; i+=i) {
            for (int j = 0; j+i < n; j+=i+i) {
                merge(arr,j , j+i-1, Math.min( j+i+i-1, n-1));
            }
        }
    }

    private static void merge(int[] arr,int l, int mid, int r) {
        int[] tmp = new int[r-l+1];
        for (int i = l; i <=r; i++) {
            tmp[i-l] = arr[i];
        }
        int i=l, j=mid+1;
        for (int k = l; k <=r ; k++) {
            if (i>mid) {
                arr[k] = tmp[(j++)-l];
            } else if (j>r) {
                arr[k] = tmp[(i++)-l];
            } else if (tmp[i-l] < tmp[j-l]) {
                arr[k] = tmp[(i++)-l];
            } else {
                arr[k] = tmp[(j++)-l];
            }
        }
    }



    public static void main(String[] args) throws Exception{
        int[] a = BaseUtil.gen(10000, 1, 50000);
        long startTime = System.currentTimeMillis();
        baseSort(a,0, a.length-1);
        System.out.println("use time : " + (System.currentTimeMillis() - startTime));
        for (int i = 0; i < Math.min(a.length, 100); i++) {
            System.out.print(a[i] + " ");
        }
        BaseUtil.check(a);
    }

}
