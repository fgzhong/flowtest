package com.mypro.view.java;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * @author fgzhong
 * @description: synchronized
 * @since 2019/6/25
 */
public class Synchronized {

    /*
     1、导致线程安全的原因：线程运行时拥有自己的栈空间，把共享数据复制到栈空间进行操作，然后赋值回去
     2、底层实现：监视器monitor。同一时刻只有一个线程能够获取到monitor
     3、一个对象有多个synchronized方法，只要一个线程访问了其中的一个synchronized方法，其它线程不能同时访问这个对象中任何一个synchronized方法
     4、对象和类monitor不是同一个
     5、锁
       1、无锁状态、偏向锁、轻量级锁和重量级锁
       2、自旋锁与自适应自旋
     6、具体实现
       1、对象在堆中的具体内容：对象头、实例变量、填充数据。
         1、对象头：
    */


    // 实例方法，锁的是该类的实例对象
    public synchronized void method1(){
        System.out.println("1");
    }

    // 静态方法，锁的是类对象
    public synchronized static void method2(){
        System.out.println("2");
    }

    private final String lock = "";
    private void method3(){
        // 同步代码块，锁的是该类的实例对象
        synchronized (this){}
        // 同步代码块，锁的是类对象
        synchronized (Synchronized.class){}
        // 同步代码块，锁的是配置的实例对象
        synchronized (lock){}
    }

    public static void main(String[] args) {
        Synchronized s = new Synchronized();
        new Thread(){
            @Override
            public void run(){
                for (int i = 0; i < 10; i++) {
                    System.out.print(i + " v- ");
                    Synchronized.method2();
                    CloseableHttpClient client = HttpClients.createDefault();
                }
            }
        }.start();
        new Thread(){
            @Override
            public void run(){
                for (int i = 0; i < 10; i++) {
                    System.out.print(i + " p- ");
                    s.method1();
                    CloseableHttpClient client = HttpClients.createDefault();

                }
            }
        }.start();
    }
}
