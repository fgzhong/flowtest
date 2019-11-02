package com.mypro.spark.stream;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/7/31
 */
public class Constant {

    public final static String CHECKPOINT_LOCATION = "checkpointLocation";

    public final static String TEN_SECONDS = "10 seconds";
    public final static String ONE_MINUTES = "1 minutes";
    public final static String TWO_MINUTES = "2 minutes";
    public final static String FIVE_MINUTES = "5 minutes";

    private final static String CHECKPOINT = "data";
    public final static String MSG_FIVE_WINDOW_COUNT_QUERY_NAME = "MSG_FIVE_WINDOW_COUNT_QUERY_NAME";
    public final static String MSG_FIVE_WINDOW_COUNT_CHECKPOINT = CHECKPOINT + "/msg/5/count";
    public final static String GF_FIVE_WINDOW_COUNT_QUERY_NAME = "GF_FIVE_WINDOW_COUNT_QUERY_NAME";
    public final static String GF_FIVE_WINDOW_COUNT_CHECKPOINT = CHECKPOINT + "/gf/5/count";

    public final static String PROJECT_VERSION = "project.version";
    public final static String DEFAULT_PROJECT_VERSION = "v1";
    public final static String KAFKA_GROUP_ID = "kafka.group.id";
    public final static String DEFAULT_KAFKA_GROUP_ID = "spark-stream-default-group-id-";
    public final static String STREAM_CHECKPOINT = "spark.stream.checkpoint";
    public final static String DEFAULT_STREAM_CHECKPOINT = "mypro/stream/v1/checkpoint";
    public final static String KAFKA_SERVER_ADDR = "kafka.bootstrap.servers";
    public final static String DEFAULT_KAFKA_SERVER_ADDR = "localhost:9092";
    public final static String KAFKA_TOPIC = "kafka.topic";
    public final static String DEFAULT_KAFKA_TOPIC = "spark-local";
    public final static String ES_ADDR = "es.addr";
    public final static String DEFAULT_ES_ADDR = "localhost:9200";
    public final static String ES_GIFT = "es.gift.index";
    public final static String DEFAULT_ES_GIFT = "spark-gift";

}
