package com.mypro.math.leetcode;

/**
 * @author fgzhong
 * @description: 对有序数组排重，只保留一个数
 * @link https://leetcode.com/problems/remove-duplicates-from-sorted-array/submissions/
 * @group 283,26,27,80
 * @since 2019/6/17
 */
public class RemoveDuplicatesFromSortedArray_26 {

    public int removeDuplicates(int[] nums) {
        if (nums.length == 0) {
            return 0;
        }
        int k=1;
        int val = nums[0];
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] != val) {
                nums[k++] = nums[i];
                val=nums[i];
            }
        }
        return k;
    }
}
