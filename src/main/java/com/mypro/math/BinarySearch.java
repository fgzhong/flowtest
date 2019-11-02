package com.mypro.math;

/**
 * @author fgzhong
 * @description: 二分查找
 * @since 2019/6/17
 */
public class BinarySearch {

    private static int binarySearch(int[] arr, int n, int target) {
        int l=0, r=n-1;
        while ( l<=r ) {
            int mid = l + (r-l)/2;  // 整型溢出
            if ( arr[mid] == target ) {
                return mid;
            } else if (target > arr[mid]) {
                l = mid + 1;
            } else {
                r = mid -1;
            }
        }
        return -1;
    }

}
