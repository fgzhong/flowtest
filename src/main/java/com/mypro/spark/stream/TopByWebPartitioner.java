package com.mypro.spark.stream;

import org.apache.spark.Partitioner;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/9/1
 */
public class TopByWebPartitioner <GfRank> extends Partitioner {


    private int partiton;

    public TopByWebPartitioner(int partiton){
        this.partiton = partiton;
    }

    @Override
    public int numPartitions() {
        return partiton;
    }

    @Override
    public int getPartition(Object key) {
        int partId = key.toString().split("_")[0].hashCode()%partiton;
        if (partId < 0) return partiton-1;
        return partId;
    }
}
