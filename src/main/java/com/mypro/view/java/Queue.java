package com.mypro.view.java;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author fgzhong
 * @description: 队列
 * @since 2019/7/8
 */
public class Queue {

    /*
      1、方法
        1、add(E)
        2、offer(E) 立即返回结果true、false
        3、put(E)   阻塞
        4、offer(E, T)
        5、take()
        6、poll()  //
        7、peek() //只拿
      2、实现
        1、ArrayBlockingQueue 定长数组
        2、LinkedBlockingQueue 链表
        3、DelayQueue 无限制的延时队列
        4、PriorityBlockingQueue 基于优先级的
        5、


    */


    // 阻塞队列
    public static class BlockQueue {
        private int[] data;
        private volatile int count = 0;

        public BlockQueue(int size) {
            data = new int[size];
        }

        public synchronized void add(int ele) throws InterruptedException{
            while (count == data.length) {
                wait();
            }
            data[count++] = ele;
            notify();
        }

        public synchronized int get() throws InterruptedException{
            while (count == 0) {
                wait();
            }
            int ele = data[--count];
            notify();
            return ele;
        }

    }

    public static class BlockQueueWithBlock {
        private int[] data;
        private volatile int count = 0;
        private final Lock lock = new ReentrantLock();
        final Condition notEmpty = lock.newCondition();
        final Condition notFull = lock.newCondition();

        public BlockQueueWithBlock(int size) {
            data = new int[size];
        }

        public void add(int ele) throws InterruptedException{
            lock.lock();
            try {
                while (count == data.length) {
                    notFull.await();
                }
                data[count++] = ele;
                notEmpty.signal();
            } finally {
                System.out.println(" -------- b -----");
                lock.unlock();
            }
        }

        public int get() throws InterruptedException{
            lock.lock();
            try {
                while (count == 0) {
                    notEmpty.await();
                }
                int ele = data[--count];
                notFull.signal();
                return ele;
            } finally {
                System.out.println(" -------- a -----");
                lock.unlock();
            }
        }

    }
}
