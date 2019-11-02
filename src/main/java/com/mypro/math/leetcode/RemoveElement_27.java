package com.mypro.math.leetcode;

/**
 * @author fgzhong
 * @description: 把数组中某一值元素放到最后，其他元素按原顺序放到最前，并返回其他元素的个数
 * @link https://leetcode.com/problems/remove-element/
 * @group 283,26,27,80
 * @since 2019/6/17
 */
public class RemoveElement_27 {

    public int removeElement(int[] nums, int val) {
        int k=0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != val) {
                nums[k++]=nums[i];
            }
        }
        return k;
    }
}
