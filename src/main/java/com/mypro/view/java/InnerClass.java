package com.mypro.view.java;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author fgzhong
 * @description: 内部类
 * @since 2019/6/25
 */
public class InnerClass {

    /*
      1、内部类对外部类的所有内容都可见
      2、内部类 + 接口 = 多继承
      3、分类
         1、成员内部类
         2、静态内部类：不需要依赖于外部类，
         3、局部内部类
         4、匿名内部类：是唯一一种没有构造器的类，用于接口回调，只是对继承方法的实现或是重写
      4、作用
         1、实现更好的封装；可以修饰为private等
         2、实现多重继承；可以实现继承多个类
         3、当外部类需继承的多个接口中有相同方法，可以用内部类继承接口
         4、内部类可以无条件访问外部类所有变量
      5、编译后会生成两个独立的class文件
      6、无条件访问外部类的成员
         1、编译器会默认为成员内部类添加了一个指向外部类对象的引用，
         2、在定义的内部类的构造器是无参构造器，编译器还是会默认添加一个参数，该参数的类型为指向外部类对象的一个引用
         3、成员内部类是依赖于外部类的
      7、局部内部类和匿名内部类只能访问局部final变量


      1、封装就是将同一类事物的特性与功能包装在一起，对外暴露调用的接口。
      2、多态
      3、
    */

    class Draw {     //内部类
        public void drawSahpe() {
            System.out.println("drawshape");
        }
    }

    public class Draw1 extends Draw{}
    protected class Draw2 extends Draw{}
    // 静态内部类：不依赖于外部类，不能访问外部非静态对象
    private static class Draw3 implements Closeable{
        @Override
        public void close() throws IOException {
        }
    }

    //局部内部类，只能访问局部final变量
    public void drawSahpe() {
        class Inner{}
        Inner inner = new Inner();
    }

    // 匿名内部类，只能访问局部final变量
    Thread thread = new Thread(){
        @Override
        public void run(){
            System.out.println("匿名内部类");
        }
    };


}
