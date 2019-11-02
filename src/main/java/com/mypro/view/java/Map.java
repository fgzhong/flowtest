package com.mypro.view.java;

import java.util.HashMap;

/**
 * @author fgzhong
 * @description: HashMap
 * @since 2019/7/5
 */
public class Map {

    /*
      1、HashMap
        1、哈希桶（数组+链表）
          1、数组：根据下标快速定位
          2、链表：数据量少的时候，能非常快速查找、添加、删除
          3、红黑树：数据量大时查找、添加、删除表现较好，AVL树插入很快，添加、删除很差
        2、存：不安全
          1、根据hashCode找到所在数组
          2、根据equals查找是否存在
          3、存在覆盖，不存在链表头插入，红黑树要插入到合适位置
          4、当链表长度过长>8时，链表转为红黑树
        3、不安全
          1、hash碰撞：
            1、同时存储hashCode相同且不存在的数据时，
            2、同时往链表头插入数据，
            3、会覆盖掉其中一个数据
          2、rehash
            1、当数据达到一定数值时，数组扩容，
            2、同时存储不存在的数据时
            3、
        4、为什么链表转红黑树，不转二叉查找树
          1、在数据量少时，链表表现最好
          2、大数据量时，红黑树各个方面表现良好，二叉查找树只在查找方面好，其他方面不好
          3、Tree结构复杂牺牲了空间

      2、TreeMap
        1、红黑树
      3、CurrentHashMap
        1、1.7——Segment数组+链表
           1.8——去掉Segment类，保存Segment概念
        2、线程安全
          1、

    */

}
