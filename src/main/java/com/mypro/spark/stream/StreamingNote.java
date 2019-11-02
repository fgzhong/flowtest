package com.mypro.spark.stream;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/7/23
 */
public class StreamingNote {

    /*
      1、数据结构
        1、[key] = byte ; [value] = byte; [topic] = string;
          [partition] = int; [offset] = long; [timestamp] = timestamp; [timestampType] = int
      2、source设置
        1、assign/subscribe/subscribePattern  指定topic，只能设置一个
          1、assign：{“topicA”:[0,1],“topicB”:[2,4]} 指定 TopicPartitions 来消费
          2、subscribe：逗号分隔的 topics 列表   要订阅的 topic 列表
          3、subscribePattern：正则      用于订阅 topic(s) 的 pattern（模式）
        2、kafka.bootstrap.servers： 逗号分隔的 host:port 列表；    Kafka 中的 “bootstrap.servers” 配置
        3、可选项
          1、startingOffsets：
            1、[earliest]：batch批处理，流处理
            2、latest：流处理
            3、json {“topicA”:{“0”:23,“1”:-1},“topicB”:{“0”:-2}}
          2、endingOffsets ： 批处理查询
            1、[latest]：从最新偏移量
            2、json：指定偏移量
          3、failOnDataLoss：[true]/false，流处理，数据丢失时失败
          4、kafkaConsumer.pollTimeoutMs：[512ms]
          5、fetchOffset.numRetries
          6、fetchOffset.retryIntervalMs
          7、maxOffsetsPerTrigger  对每个触发器间隔处理的偏移量的最大数量的速率限制
       3、OutputModes 输出模型
         1、OutputModes
         Complete Mode：整个更新的结果表将被写入外部存储。由存储连接器（storage connector）决定如何处理整个表的写入
Append Mode：只有结果表中自上次触发后附加的新行将被写入外部存储。这仅适用于不期望更改结果表中现有行的查询。
Update Mode：只有自上次触发后结果表中更新的行将被写入外部存储（自 Spark 2.1.1 起可用）。 请注意，这与完全模式不同，因为此模式仅输出自上次触发以来更改的行。如果查询不包含聚合操作，它将等同于附加模式。
           1、Complete
           2、Append
           3、Update
       4、Structured Streaming 将依靠 watermark 机制来限制状态存储的无限增长、并（对 Append 模式）尽早输出不再变更的结果。
       5、输出
         1、StreamingQuery
           1、outputMode
           2、queryName：
           3、trigger
           4、Checkpoint location （检查点位置） .option(“checkpointLocation”, “path/to/HDFS/dir”)
       6、sink
         1、File sink 输出存储到目录  Append
            writeStream
                .format("parquet")        // can be "orc", "json", "csv", etc.
                .option("path", "path/to/destination/dir")
                .start()
         2、Foreach sink 对 output 中的记录运行 arbitrary computation ，一般很常用，可以将流数据保存到数据库等，详细用法后面会提到
           Append, Update, Compelete
           writeStream
                .foreach(...)
                .start()
         3、Console sink  每次触发时，将输出打印到 console/stdout
           Append, Update, Compelete
           numRows: 每个触发器需要打印的行数（默认:20） truncate: 如果输出太长是否截断（默认: true）
           writeStream
                .format("console")
                .start()
         4、Memory sink  输出作为 in-memory table （内存表）存储在内存中
           Append, Compelete
           writeStream
                .format("memory")
                .queryName("tableName")
                .start()
         5、Kafka sink将数据输出至Kafka
           StreamingQuery ds = df
              .selectExpr("topic", "CAST(key AS STRING)", "CAST(value AS STRING)")
              .writeStream()
              .format("kafka")
              .option("kafka.bootstrap.servers", "host1:port1,host2:port2")
              .start()
          6、es  Append
            people.writeStream()
                .option("checkpointLocation", "/save/location")
                .format("es")
                .start("spark/people");
         /*
                 dataset.writeStream()
                .queryName("mongodb" + collectionName)
                .foreach(new ForeachWriter<Row>() {

                    Map<String, String> writeOverrides = new HashMap<String, String>() {{
                        put("uri", MongoDbConfig.MONGO_DB_URI);
                        put("database", MongoDbConfig.MONGO_MOFANG_TSP_DATA_DB);
                        put("collection", collectionName);
                    }};
                    WriteConfig writeConfig = WriteConfig.create(jsc).withOptions(writeOverrides);
                    MongoConnector mongoConnector = null;
                    ArrayList<Row> list = null;

                    @Override
                    public void process(Row value) {
                        list.add(value);
                    }

                    @Override
                    public void close(Throwable errorOrNull) {
                        if (!list.isEmpty()) {
                            mongoConnector.withCollectionDo(writeConfig, Document.class, (MongoCollection<Document> mongoCollection) -> {
                                for (Row row : list) {
                                    Map<String, Object> map = new HashMap<>();
                                    String[] fieldNames = row.schema().fieldNames();
                                    for (String s : fieldNames) {
                                        map.put(s, row.getAs(s));
                                    }
                                    Document document = new Document(map);
                                    mongoCollection.insertOne(document);
                                }
                                return null;
                            });
                        }
                    }

                    @Override
                    public boolean open(long partitionId, long version) {
                        mongoConnector = MongoConnector.apply(writeConfig.asOptions());
                        list = new ArrayList<>();
                        return true;
                    }
                })
                .start();
              }

          7、spark streaming 与 structured streaming
            1、Processing Time （数据到达 Spark 被处理的时间） 而不是 Event Time
            2、DStream （Spark Streaming 的数据模型）提供的 API 类似 RDD 的 API 的，非常的 low level
            3、input 接入 Spark Streaming 和 Spark Straming 输出到外部存储的语义往往需要用户自己来保证
            4、批流代码不统一
            5、简洁的模型。Structured Streaming 的模型很简洁，易于理解。用户可以直接把一个流想象成是无限增长的表格
            6、一致的 API
            7、卓越的性能，使用了 Spark SQL 的 Catalyst 优化器和 Tungsten，数据处理性能十分出色
            8、

    */

}
