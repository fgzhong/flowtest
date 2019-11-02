package com.mypro.view.java;

/**
 * @author fgzhong
 * @description: 静态关键字
 * @since 2019/6/30
 */
public class Static {

    static {
        i = -1;
        // 静态语句块只能访问到定义在静态语句块之前的变量，定义在它之后的变量，在前面的静态语句块可以赋值，但是不能访问
        // System.out.println(i);
    }
    static int i=1;
    // 最终结果i=1；因为初始化按语句顺序执行赋值操作
    // 通过子类引用父类的静态字段，不会导致子类初始化。
    // 常量在编译阶段会存入调用类的常量池中，本质上并没有直接引用到定义常量的类，因此不会触发定义常量的类的初始化

    private static Static c = new Static(); // 只会调用构造函数
    static {
        i=2;
    }

    static {
        System.out.println("1111");
    }

    public Static() {
        System.out.println(i);
    }
    public void x(){
        System.out.println("3333");
    }
}
