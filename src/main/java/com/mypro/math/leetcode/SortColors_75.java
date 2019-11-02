package com.mypro.math.leetcode;

/**
 * @author fgzhong
 * @description: 对只包含0、1、2的数组排序
 * @link https://leetcode.com/problems/sort-colors/
 * @group 88,75,215
 * @since 2019/6/17
 */
public class SortColors_75 {

    /**
     * 计数统计
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * @param nums
     */
    public void sortColors(int[] nums) {
        int k1=0,k2=0,k3=0;
        for (int i = 0; i < nums.length; i++) {
            switch (nums[i]) {
                case 0: k1++;break;
                case 1: k2++;break;
                case 2: k3++;break;
                default:;
            }
        }
        for (int i = 0; i < k1; i++) {
            nums[i] = 0;
        }
        for (int i = k1; i < k1+k2; i++) {
            nums[i] = 1;
        }
        for (int i = k1+k2; i < k1+k2+k3; i++) {
            nums[i] = 2;
        }
    }

    /**
     * 一次三路快排
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * @param nums
     */
    public void sortColorsByQUickSort(int[] nums) {
        int zero = -1;
        int two = nums.length;
        for (int i=0; i<two;) {
            if (nums[i] == 1) {
                i++;
            } else if (nums[i] == 2) {
                swap(nums,i , --two);
            } else {
                swap(nums, i++, ++zero);
            }
        }
    }

    private void swap(int[] arr, int a, int b) {
        int temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }

}
