package com.mypro.math;

import java.util.Arrays;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/4/9
 */
public class MathTest {

    public void sort(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr.length - i - 1; j++) {
                if (arr[j] < arr[j+1]) {
                    BaseUtil.swap(arr, j, j+1);
                }
            }

        }
    }

    public void sort2(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            int min = i;
            for (int j = 0; j < arr.length - i - 1; j++) {
                if (arr[min] > arr[j]) {
                    min = j;
                }
            }
            BaseUtil.swap(arr, i, min);
        }
    }

    public void sort3(int[] arr, int l, int r) {
        if (l>=r) {
            return;
        }
        int mid = partition(arr, l, r);
        sort3(arr, l, mid -1);
        sort3(arr, mid+1, r);
    }

    public int partition(int[] arr, int l, int r) {
        int tmp = arr[l];
        int j = l;
        for (int i = l+1; i <= r; i++) {
            if (arr[i] < tmp) {
                BaseUtil.swap(arr, i, ++j);
            }
        }
        BaseUtil.swap(arr, l, j);
        return j;
    }

    public int find(int[] arr, int target) {
        int l=0;
        int r=arr.length-1;
        while (l<=r){
            int mid = l+(r-l)/2;
            if (arr[mid] == target) {
                return mid;
            } else if (arr[mid] > target){
                r = mid-1;
            } else if (arr[mid] < target) {
                l = mid + 1;
            }
        }
        return -1;
    }

    public int find1(int[] arr, int target) {
        int l=0;
        int r=arr.length-1;
        while (l<r){
            int mid = l+(r-l)/2;
            if (arr[mid] < target){
                r = mid;
            } else {
                l = mid + 1;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        MathTest test = new MathTest();
        int [] arr={4,1,5,2,3,8,6,9};
        test.sort3(arr, 0, arr.length-1);
        System.out.println(Arrays.toString(arr));

    }
}
