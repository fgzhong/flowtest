package com.mypro.view.java;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/7/2
 */
public class Final {


    /*
      1、不可变对象就是那些一旦被创建，它们的状态就不能被改变的对象，每次对它们的改变都是产生了新的对象
      2、可变对象就是那些创建后，状态依然可以被改变的对象
    */

    // 不可变对象
    public final class A{
        String s =  new String("1");

        public void setS(String s) {
            this.s = s;
        }
    }
}
