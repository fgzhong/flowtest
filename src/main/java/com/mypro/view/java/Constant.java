package com.mypro.view.java;

/**
 * @author fgzhong
 * @description: 基本数据类型
 * @since 2019/7/2
 */
public class Constant {

    String a = "123";
    String b = new String("123");  // 生成两个对象：1、字符串常量池，2、堆中
    String c = new String(a);
    String d = "45";
    String e = a + d;
    String f = "12345";
    String g = "12345";
    String h = a + d;

    /*
      1、-128~127会缓存；当new Integer(i) i在此范围时，直接返回缓存中的指针，不会创建新对象
      2、parseInt 与 valueOf
        1、parseInt 返回 int类型
        2、valueOf -128~127之间返回int类型，其他返回新对象
        3、自动装箱/拆箱 == Integer.valueOf()/Integer.intValue()
        4、int 与 int/Integer 比较时，两端自动拆箱，比较值大小
        5、Integer 与 Integer 比较时，比较两个对象
    */
    int ia = 1;
    int ib = 127;
    int ic = 128;
    Integer ib1 = 127;
    Integer ic1 = 128;
    Integer id = new Integer(1);
    Integer ie = new Integer(128);
    Integer ih = new Integer(129);

    int ba = 1;
    byte bb = 2;

    public static void main(String[] args) {
        Constant c = new Constant();
        System.out.println(c.a == c.b);
        System.out.println(c.e == c.f);
        System.out.println(c.c == c.b);
        System.out.println(c.f == c.g);
        System.out.println(c.e == c.h);

        //+=操作符会进行隐式自动类型转换，此处a+=b隐式的将加操作的结果类型强制转换为持有结果的类型，而a=a+b则不会自动进行类型转换
        //c.bb = c.ba+c.bb;
        c.bb += c.ba;
    }


}
