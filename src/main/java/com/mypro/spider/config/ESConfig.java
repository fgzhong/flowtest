package com.mypro.spider.config;

/**
 * @author fgzhong
 * @since 2019/1/10
 */
public interface ESConfig {


    String NODES = "es.nodes";   // 默认 localhost / localhost:9200 / localhost:9200;localhost:9200 (不必需添加每个节点，elasticsearch-hadoop 自动发现)
    String DEFAULT_NODES = "localhost:9200";
    String PORT = "es.port";   // 默认 9200
    String DEFAULT_PORT = "9200";
    String RESOURCE = "es.resource";   // 数据源 index/type
    String DEFAULT_RESOURCE = "array2/_doc";
    String RESOURCE_READ = "es.resource.read";  // 只读 数据源 index/type
    String RESOURCE_WRITE = "es.resource.write"; // 只写 数据源 index/type
    String NODES_PATH_PREFIX = "es.nodes.path.prefix"; // 所有对ES的请求添加前缀，代理、路由时采用

    String QUERY = "es.query"; // 查询语句， 支持URI、DSL、HDFS文件
    String INPUT_JSON = "es.input.json";  // 输入是否是JSON格式，默认 false
    String WRITE_OPERATION = "es.write.operation"; // 写操作模式； 默认 index
    String OPERATION_INDEX = "index"; // 添加新数据，若数据已存在，替换
    String OPERATION_CREATE = "create"; // 添加新数据，若数据已存在，则抛异常
    String OPERATION_UPDATE = "update"; // 更新已存在的数据，若不存在，则抛异常, 必须指定ID用于更新，es.mapping.id = doc.key
    String OPERATION_UPSERT = "upsert"; // 添加或更新，基于ID，必须指定ID用于更新，es.mapping.id = doc.key
    String OUTPUT_JSON = "es.output.json"; // 输出格式是否为json，默认 false
    String INGEST_PIPELINE = "es.ingest.pipeline"; // ???

    /*  批量 读写  */
    String BATCH_SIZE_BYTES = "es.batch.size.bytes";  //  批量写入 数据大小， 默认 1M
    String BATCH_SIZE_ENTRIES = "es.batch.size.entries";  //  批量写入 数量大小，默认 1000
    String BATCH_WRITE_REFRESH = "es.batch.write.refresh";  // 批量更新完是否调用索引刷新，默认 true
    String BATCH_WRITE_RETRY_COUNT = "es.batch.write.retry.count";  //  写 重试次数， 默认 3
    String BATCH_WRITE_RETRY_WAIT = "es.batch.write.retry.wait";  //  写 重试等待时间， 默认 10s
    String SER_READER_VALUE_CLASS = "es.ser.reader.value.class";  //  json - value 序列化类，ValueReader
    String SER_WRITER_VALUE_CLASS = "es.ser.writer.value.class";  //  value - json 序列化类，ValueWriter

    String MAPPING_ID = "es.mapping.id"; // 指定文档中某一field作为文档ID，默认无
    String DEFAULT_MAPPING_ID = "uid";
    String MAPPING_PARENT = "es.mapping.parent"; // 指定文档的父文档
    String MAPPING_JION = "es.mapping.join"; //
    String MAPPING_ROUTING = "es.mapping.routing"; //
    String MAPPING_VERSION = "es.mapping.version"; //
    @Deprecated
    String MAPPING_VERSION_TYPE = "es.mapping.version.type";
    @Deprecated
    String MAPPING_TTL = "es.mapping.ttl";
    @Deprecated
    String MAPPING_TIMESTAMP = "es.mapping.timestamp";
    String MAPPING_INCLUDE = "es.mapping.include";
    String MAPPING_EXCLUDE = "es.mapping.exclude";
    String MAPPING_DATE_RICH = "es.mapping.date.rich";

    String READ_FIELD_INCLUDE = "es.read.field.include";
    String READ_FIELD_EXCLUDE = "es.read.field.exclude";
    String READ_FIELD_AS_ARRAY_INCLUDE = "es.read.field.as.array.include";
    String READ_METADATA = "es.read.metadata";  // 结果是否包含元数据（ID/version），默认false
    String READ_METADATA_FIELD = "es.read.metadata.field"; // 返回元数据的字段
    String READ_METADATA_VERSION = "es.read.metadata.version"; // 是否返回元数据版本，默认false

    String UPDATE_SCRIPT_INLINE = "es.update.script.inline";
    @Deprecated
    String UPDATE_SCRIPT_FILE = "es.update.script.file";
    String UPDATE_SCRIPT_STORED = "es.update.script.stored";
    String UPDATE_SCRIPT_LANG = "es.update.script.lang";
    String UPDATE_SCRIPT_PARAMS = "es.update.script.params"; // 更新脚本
    String UPDATE_SCRIPT_PARAMS_JSON = "es.update.script.params.json";
    String UPDATE_RETRY_ON_CONFLICT = "es.update.retry.on.conflict"; // 并发冲突重试次数

    String INDEX_AUTO_CREATE = "es.index.auto.create";  // 写入数据时是否创建索引，默认true
    String INDEX_READ_MISSING_AS_EMPTY = "es.index.read.missing.as.empty";  // 是否允许读取不存在的index，true——返回空data，false——抛异常
    String FIELD_READ_EMPTY_AS_NULL = "es.field.read.empty.as.null";  //   是否将空字段视为null，默认 true
    String FIELD_READ_VALIDATE_PRESENCE = "es.field.read.validate.presence";  //  发现错误数据是否警告，默认 warn
    String IGNORE_PRESENCE = "ignore";  //  不进行验证
    String DEFAULT_WARN_PRESENCE = "warn";  //  验证失败，记录警告消息
    String STRICT_PRESENCE = "strict";  //  验证字段丢失，则抛出异常，暂停作业
    String READ_SOURCE_FILTER = "es.read.source.filter";  //  字段过滤，以，隔开
    String INDEX_READ_ALLOW_RED_STATUS = "es.index.read.allow.red.status";   // 集群状态异常时，是否继续作业，默认 false
    String INPUT_MAX_DOCS_PER_PARTITION = "es.input.max.docs.per.partition";  //  scroll时，每个输入分区的最大文档数 ？？？
    int DEFAULT_INPUT_MAX_DOCS_PER_PARTITION = 50000;
    String INPUT_USE_SLICED_PARTITIONS = "es.input.use.sliced.partitions";  //  ???
    String NODES_DISCOVERY = "es.nodes.discovery";   // 是否自动发现节点，默认 true
    String NODEX_CLIENT_ONLY = "es.nodes.client.only";  // 是否使用client节点或负载平衡器，默认 false
    String NODES_DATA_ONLY = "es.nodes.data.only";  //  是否只访问数据节点，默认 true，可避免其他节点的压力
    String NODES_INGEST_ONLY = "es.nodes.ingest.only";  //  默认 false ？？？
    String NODES_WAN_ONLY = "es.nodes.wan.only";  //  默认 false
    String NODES_RESOLVE_HOSTNAME = "es.nodes.resolve.hostname";  //   是否将主机名解析为IP地址，默认 true
    String HTTP_RETRIES = "es.http.retries";  //  http/rest 重试次数，默认 3
    String HTTP_TIMEOUT = "es.http.timeout";  //  http/rest 连接超时，默认 1m
    String SCROLL_KEEPALIVE = "es.scroll.keepalive";  //  scroll之间时间最大间隔，默认 10m
    String SCROLL_SIZE = "es.scroll.size";  // scroll请求结果数量，默认 50
    String SCROLL_LIMIT = "es.scroll.limit"; //  scroll 请求返回最大 数据量，默认 -1（返回所有）
    String ACTION_HEART_BEAT_LEAD = "es.action.heart.beat.lead";  // 失败持续运行时间，默认 15s，防止hadoop重启任务
//    String ES_NET_HTTP_HEADER_

    /* 安全设置  */
    String KEYSTORE_LOCATION = "es.keystore.location";
    String NET_HTTP_AUTH_USER = "es.net.http.auth.user";
    String NET_HTTP_AUTH_PASS = "es.net.http.auth.pass";

    String NET_SSL = "es.net.ssl";
    String NET_SSL_KEYSTORE_LOCATION = "es.net.ssl.keystore.location";
    String NET_SSL_KEYSTORE_PASS = "es.net.ssl.keystore.pass";
    String NET_SSL_KEYSTORE_TYPE = "es.net.ssl.keystore.type";
    String NET_SSL_TRUSTSTORE_LOCATION = "es.net.ssl.truststore.location";
    String NET_SSL_TRUSTSTORE_PASS = "es.net.ssl.truststore.pass";
    String NET_SSL_CERT_ALLOW_SELF_SIGNED = "es.net.ssl.cert.allow.self.signed";
    String NET_SSL_PROTOCOL = "es.net.ssl.protocol";

    /*  代理  */
    String NET_PROXY_HTTP_HOST = "es.net.proxy.http.host";  //
    String NET_PROXY_HTTP_PORT = "es.net.proxy.http.port";
    String NET_PROXY_HTTP_USER = "es.net.proxy.http.user";
    String NET_PROXY_HTTP_PASS = "es.net.proxy.http.pass";
    String NET_PROXY_HTTP_USE_SYSTEM_PROPS = "es.net.proxy.http.use.system.props";
    String NET_PROXY_HTTPS_HOST = "es.net.proxy.https.host";
    String NET_PROXY_HTTPS_PORT = "es.net.proxy.https.port";
    String NET_PROXY_HTTPS_USER = "es.net.proxy.https.user";
    String NET_PROXY_HTTPS_PASS = "es.net.proxy.https.pass";
    String NET_PROXY_HTTPS_USE_SYSTEM_PROPS = "es.net.proxy.https.use.system.props";
    String NET_PROXY_SOCKS_HOST = "es.net.proxy.socks.host";
    String NET_PROXY_SOCKS_PORT = "es.net.proxy.socks.port";
    String NET_PROXY_SOCKS_USER = "es.net.proxy.socks.user";
    String NET_PROXY_SOCKS_PASS = "es.net.proxy.socks.pass";
    String NET_PROXY_SOCKS_USE_SYSTEM_PROPS = "es.net.proxy.socks.use.system.props";

}
