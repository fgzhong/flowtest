package com.mypro.view.java;

/**
 * @author fgzhong
 * @description: 线程
 * @since 2019/6/25
 */
public class ThreadState {

    /*
    1、创建(new)、就绪(runnable)、运行(running)、阻塞(blocked)、time waiting、waiting、消亡（dead）
    */

    static Thread thread = new Thread();

    public static void main(String[] args) throws Exception{
        thread.start();
        thread.wait(); // 会释放CPU，也会释放monitor
        Thread.sleep(1); // 会释放CPU，但不会释放monitor
        thread.join();
        thread.notify();
        thread.interrupt();
        Thread.currentThread().wait();
    }
}
