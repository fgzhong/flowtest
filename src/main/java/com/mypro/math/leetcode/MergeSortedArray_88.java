package com.mypro.math.leetcode;

/**
 * @author fgzhong
 * @description: 将有序数组2归并到有序数组1中，并保持数组1有序
 * @link https://leetcode.com/problems/merge-sorted-array/
 * @group 88,75,215
 * @since 2019/6/18
 */
public class MergeSortedArray_88 {

    public void merge(int[] nums1, int m, int[] nums2, int n) {
        if (n == 0 || nums1.length < m + n) {
            return;
        }
        int[] tmp = new int[m];
        for (int i = 0; i < m; i++) {
            tmp[i] = nums1[i];
        }

        int k1=0, k2=0;
        for (int i = 0; i < m+n; i++) {
            if (k1>=m) {
                nums1[i] = nums2[k2++];
            } else if (k2 >= n) {
                nums1[i] = tmp[k1++];
            } else if (tmp[k1] < nums2[k2]) {
                nums1[i] = tmp[k1++];
            } else {
                nums1[i] = nums2[k2++];
            }
        }
    }


}
