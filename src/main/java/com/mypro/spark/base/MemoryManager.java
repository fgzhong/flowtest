package com.mypro.spark.base;

/**
 * @author fgzhong
 * @description: 内存管理
 * @since 2019/7/24
 */
public class MemoryManager {

    /*
      1、内存管理机制——UnifiedMemoryManager
        1、Reserved Memory  系统预留的内存——300M
        2、User Memory  1-spark.memory.fraction 0.25/25%
           用户内存，用来存储用户自己的数据。比如 input data，map 操作后的 transform data
        3、Spark Memory
          spark.memory.fraction  0.6/60%
          1、Storage Memory
             Spark缓存数据和序列化数据”unroll”临时空间，以及存储 broadcast vars
             spark.memory.storageFraction  0.5/50%
          2、Execution Memory
             存储Spark task执行需要的对象，比如 shuffle、join、union、sort 等操作 buffer
             这块内存会 OOM，且无法被其他tasks clean
        4、15w qps + window 5min
          1、executor memory （10g ->16g ）
          2、spark.memory.fraction 的值（默认 0.6 -> 0.2）
      2、Executor 的内存管理
        1、堆内内存
          1、–executor-memory/spark.executor.memory
          2、缓存 RDD 数据和广播（Broadcast）数据时占用的内存被规划为存储（Storage）内存
            执行 Shuffle 时占用的内存被规划为执行（Execution）内存
        2、对外内存——优化内存的使用以及提高 Shuffle 时排序的效率
          1、spark.memory.offHeap.enabled
             spark.memory.offHeap.size




    */
}
