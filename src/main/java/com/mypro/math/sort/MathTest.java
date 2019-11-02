package com.mypro.math.sort;

import com.mypro.math.BaseUtil;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/9/3
 */
public class MathTest {

    private static void baseSort(int[] arr,int l, int r) {
        if (l>=r) {
            return;
        }
        int mid = partition(arr, l, r);
        baseSort(arr,l , mid-1);
        baseSort(arr, mid+1, r);

    }

    public static int partition(int[] arr,int l, int r) {
        int tmp = arr[l];
        int j = l;
        for (int i=l+1;i<=r;i++) {
            if (arr[i]<tmp) {
                BaseUtil.swap(arr, i, ++j);
            }
        }
        BaseUtil.swap(arr, l, j);
        return j;
    }

    public static void main(String[] args) {
        int[] a = BaseUtil.gen(10000, 1, 50000);
        long startTime = System.currentTimeMillis();
        baseSort(a,0, a.length-1);
        System.out.println("use time : " + (System.currentTimeMillis() - startTime));
        for (int i = 0; i < Math.min(a.length, 100); i++) {
            System.out.print(a[i] + " - ");
        }
        BaseUtil.check(a);
    }
}
