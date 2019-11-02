package com.mypro.math.sort;

import com.mypro.math.BaseUtil;

/**
 * @author fgzhong
 * @description: 堆排序
 * @since 2019/6/19
 */
public class HeapSort{
    /**
     * 普通队列：先进后出；后进后出
     * 优先队列：出队顺序与入队顺序无关；和优先级相关
     *
     */
    /*  O(nlogn)  */
    private static void baseSort(int[] arr) {
        BinaryHeap heapSort = new BinaryHeap(arr.length);
        for (int i = 0; i < arr.length; i++) {
            heapSort.add(arr[i]);
        }
        for (int i = arr.length-1; i >= 0; i--) {
            arr[i] = heapSort.extractMax();
        }
    }
    /*  O(n)  */
    private static void baseSort2(int[] arr) {
        BinaryHeap heapSort = new BinaryHeap(arr);
        for (int i = arr.length-1; i >= 0; i--) {
            arr[i] = heapSort.extractMax();
        }
    }

    /*  O(n)  */
    private static void baseSort3(int[] arr) {
        BinaryHeap heapSort = new BinaryHeap(arr);
        for (int i = arr.length-1; i >= 0; i--) {
            arr[i] = heapSort.extractMax();
        }
    }


    /**
     *  二叉树堆 Binary Heap
     *  完全二叉树（最大堆）
     *  用数组存储二叉树：1-n存储，子节点 < 根节点——最大堆；子节点 > 根节点 —— 最小堆
     *  第i个节点：i/2——父节点
     *  左子节点：2*i，右子节点2*i+1
     */
    private static class BinaryHeap {

        private int[] data;
        private int count;
        public BinaryHeap(int capacity) {
            data = new int[capacity+1];
            count = 0;
        }

        public BinaryHeap(int[] arr) {
            data = new int[arr.length + 1];
            for (int i = 0; i < arr.length; i++) {
                data[i+1] = arr[i];
            }
            count = arr.length;
            for (int i = count/2; i >= 1; i--) {
                shiftDown(i);
            }
        }

        /* 添加元素 */
        public void add(int d) {
            if (count+1 < data.length) {
                data[count++ +1] = d;
                shiftUp(count);
            }
        }
        /* 取出最大值 */
        public int extractMax() {
            assert count > 0 : " BinaryHeap's size must > 0 ";
            int max = data[1];
            BaseUtil.swap(data, 1, count--);
            shiftDown(1);
            return max;
        }

        // 保证新插入一个元素后，整个结构还是最大堆
        private void shiftUp(int k) {
            while (k>1 && data[k/2] < data[k]) {
                BaseUtil.swap(data, k/2, k);
                k/=2;
            }
        }

        // 保证取出一个元素后，整个结构还是最大堆
        private void shiftDown(int k) {
            while (2*k <= count) {
                int j = 2*k;
                if (j+1 <= count && data[j+1] > data[j]) {
                    j+=1;
                }

                if (data[k] >= data[j]) {
                    break;
                }
                BaseUtil.swap(data, k, j);
                k=j;
            }
        }

        public int size() {
            return count;
        }

        public boolean isEmpty() {
            return count == 0;
        }

        public int[] getData() {
            return data;
        }
    }

    public static void main(String[] args) {
        int[] a = BaseUtil.gen(10000, 1, 50000);
        long startTime = System.currentTimeMillis();
        baseSort(a);
        System.out.println("use time : " + (System.currentTimeMillis() - startTime));
        for (int i = 0; i < Math.min(a.length, 100); i++) {
            System.out.print(a[i] + " - ");
        }
        BaseUtil.check(a);
    }

}
