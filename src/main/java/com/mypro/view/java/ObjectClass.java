package com.mypro.view.java;

/**
 * @author fgzhong
 * @description: Object Class
 * @since 2019/7/8
 */
public class ObjectClass {

    /*
      1、getClass() 运行时类Class
      2、hashCode() 存储地址值和对象信息算出来的
      3、equals(Object obj) {return (this == obj);}  比较两个对象的地址
      4、clone() 浅克隆，对象引用指向同一个对象
      5、toString() { return getClass().getName() + "@" + Integer.toHexString(hashCode()); }
      6、notify() // 唤醒在该对象上等待的某个线程, 不断尝试获取锁
      7、notifyAll() 唤醒在该对象上等待的所有线程, 不断尝试获取锁
      8、wait(long timeout)、wait()  // 线程必须持有该对象锁，synchronized，线程释放锁并等待再次获取锁
      9、finalize()  释放资源，一旦垃圾回收器准备好释放对象占用的存储空间，将首先调用其finalize()方法。并且在下一次垃圾回收动作发生时，才会真正回收对象占用的内存。
    */
}
