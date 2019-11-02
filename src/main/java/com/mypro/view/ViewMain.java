package com.mypro.view;

import com.mypro.view.java.Queue;

/**
 * @author fgzhong
 * @description: main 测试
 * @since 2019/6/30
 */
public class ViewMain {

    public static void main(String[] args) throws Exception{
        Queue.BlockQueueWithBlock blockQueue = new Queue.BlockQueueWithBlock(1);

        new Thread(){
            @Override
            public void run(){
                try {
                    System.out.println("get1-");
                    blockQueue.get();
                    System.out.println("get1");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        new Thread(){
            @Override
            public void run(){
                try {
                    System.out.println("get2-");
                    blockQueue.get();
                    System.out.println("get2");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        new Thread(){
            @Override
            public void run(){
                try {
                    System.out.println("add-");
                    blockQueue.add(1);
                    System.out.println("add");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        new Thread(){
            @Override
            public void run(){
                try {
                    System.out.println("get-");
                    blockQueue.add(1);
                    System.out.println("get");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
