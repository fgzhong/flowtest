package com.mypro.math.leetcode;

/**
 * @author fgzhong
 * @description: 把数组中0元素放到最后，其他元素按原顺序放到最前
 * @link https://leetcode.com/problems/move-zeroes/
 * @group 283,26,27,80
 * @since 2019/6/17
 */
public class MoveZeros_283 {

    /**
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     * @param nums
     */
    public void moveZeroes(int[] nums) {
        int[] result = new int[nums.length];
        int k=0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 0) {
                result[k++]=nums[i];
            }
        }
        for (int i = 0; i < nums.length; i++) {
            nums[i] = result[i];
        }
    }

    /**
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * @param nums
     */
    public void moveZeroesWithoutSpace(int[] nums) {
        int k=0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 0) {
                nums[k++]=nums[i];
            }
        }
        for (int i = k; i < nums.length; i++) {
            nums[i] = 0;
        }
    }

    /**
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * @param nums
     */
    public void moveZeroesWithSwap(int[] nums) {
        int k=0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 0 ) {
                if (i != k) {
                    swap(nums, k++, i);
                } else {
                    k++;
                }
            }
        }
    }

    private void swap(int[] arr, int a, int b) {
        int temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }

}
