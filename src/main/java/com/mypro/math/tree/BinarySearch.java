package com.mypro.math.tree;

/**
 * @author fgzhong
 * @description: 二分查找 —— 有序数据查找
 * @since 2019/6/21
 */
public class BinarySearch {

    /* O(logn) */
    public int baseFind(int[] arr, int target) {
        int l=0, r=arr.length-1;
        while (l <= r) {
            int mid = l + (r-l)/2;
            if (arr[mid] == target) {
                return mid;
            }

            if (target < arr[mid]) {
                r = mid - 1;
            }
            if (target > arr[mid]) {
                l = mid + 1;
            }
        }
        return -1;
    }

    /*
       floor 取target出现最早的位置,或取比target小的最大值
             思路：取比target小的最大值，往后取一位
    */
    public int floorFind(int[] arr, int target) {
        int l=0, r=arr.length-1;
        while (l < r) {
            int mid = l + (r-l)/2;
            if (arr[mid] < target) {
                l = mid;
            } else {
                r = mid-1;
            }
        }
        if (arr.length >= l+1 && arr[l+1] == target) {
            return l+1;
        }
        return l;
    }

    /* ceil 取target出现最晚的位置 或取比target大的最小值*/
    public int ceilFind(int[] arr, int target) {
        int l=0, r=arr.length-1;
        while (l < r) {
            int mid = l + (r-l)/2;
            if (arr[mid] > target) {
                r = mid;
            } else {
                l = mid+1;
            }
        }
        if (r > 0 && arr[r-1] == target) {
            return r-1;
        }
        return r;
    }

}
