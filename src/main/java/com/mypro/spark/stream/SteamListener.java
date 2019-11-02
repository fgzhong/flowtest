package com.mypro.spark.stream;

import com.mypro.kafka.util.DouyuClient;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQueryListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/7/17
 */
public class SteamListener {

    private final static Logger LOG = LoggerFactory.getLogger(SteamListener.class);


    public static void addListener(SparkSession sparkSession) {
        sparkSession.streams().addListener(new StreamingQueryListener() {
            @Override
            public void onQueryStarted(QueryStartedEvent event) {
                LOG.info("Query started", event.id());
            }

            @Override
            public void onQueryProgress(QueryProgressEvent event) {
                LOG.info("Query progress", event.progress());
            }

            @Override
            public void onQueryTerminated(QueryTerminatedEvent event) {
                LOG.info("Query terminated", event.id());
            }
        });
    }

}
