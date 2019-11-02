package com.mypro.view.java;

/**
 * @author fgzhong
 * @description: 哈希值
 * @since 2019/7/5
 */
public class Hash {

    /*
      1、把任意长度的输入（又叫做预映射pre-image）通过散列算法变换成固定长度的输出，该输出就是散列值，是一种压缩映射
      2、
         1、h(k1)≠h(k2)则k1≠k2
         2、如果k1≠k2，h(k1)=h(k2) 则发生碰撞
         3、如果h(k1)=h(k2)，k1不一定等于k2
      3、
         1、Object类：对象的内存地址
         2、String类：h = 31 * h + val[i]
         3、Interger: value

      4、减少查找次数，提高程序效率
    */

}
