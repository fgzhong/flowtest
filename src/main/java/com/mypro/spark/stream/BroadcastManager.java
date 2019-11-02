package com.mypro.spark.stream;

import com.google.common.collect.Maps;
import com.mypro.spark.stream.model.UnionKey;
import org.apache.spark.SparkContext;
import org.apache.spark.broadcast.Broadcast;
import scala.Tuple2;
import scala.reflect.ClassManifestFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/8/7
 */
public class BroadcastManager implements Serializable{

    public static void updata(SparkContext context, Broadcast<HashMap<UnionKey,Long>> fiveDataForDistance, List<Tuple2<UnionKey, Long>> newData) {
        if (fiveDataForDistance != null) {

            long nowTime = System.currentTimeMillis();
            System.out.println( "\n -------------------------------------------- " + nowTime);

            HashMap<UnionKey,Long> broadcast = Maps.newHashMapWithExpectedSize(newData.size());
            HashMap<UnionKey,Long> nowData = fiveDataForDistance.getValue();

            long dis = nowTime - 1000 * 60;

            nowData.entrySet().stream().filter(f -> dis < f.getValue()).forEach(f -> broadcast.put(f.getKey(), f.getValue()));
            newData.forEach(f -> broadcast.put(f._1, f._2));

            System.out.println( " ------------- store data -------- " + broadcast.size());

            fiveDataForDistance.unpersist(true);
            fiveDataForDistance = context.broadcast(broadcast, ClassManifestFactory.classType(HashMap.class));

            System.out.println( " -------------------------------------------- " + (nowTime -System.currentTimeMillis()) + "\n" );

        }
    }
}
