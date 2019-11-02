package com.mypro.spider.example;

import com.mypro.spider.config.ESConfig;
import org.apache.hadoop.conf.Configuration;

/**
 * @author fgzhong
 * @since 2019/1/12
 */
public class ESConfigExample {

    Configuration conf = new Configuration();


    /**  指定文档ID */
    private void id() {
        conf.set("es.mapping.id", "<field>");
    }


    /**
     * @Description:
     *     {"script":{"inline":"ctx._source.counter += params.count","lang":"painless","params":{"count":4}}}
     *     {"script":{"inline":"ctx._source.tags.add(params.tag)","lang":"painless","params":{"tag":"blue"}}}
     *     {"script":"ctx._source.new_field = \"value_of_new_field\""}
     *     {"script":"ctx._source.remove(\"new_field\")"}
     *     {"script":{"inline":"if (ctx._source.tags.contains(params.tag)) { ctx.op = \"delete\" } else { ctx.op = \"none\" }","lang":"painless","params":{"tag":"green"}}}
     *     {"script":{"inline":"ctx._source.counter += params.count","lang":"painless","params":{"count":4}},"upsert":{"counter":1}}
      */
    private void updateScript() {
        conf.set(ESConfig.UPDATE_SCRIPT_INLINE, "ctx._source.<field> += params.count");
        conf.set(ESConfig.UPDATE_SCRIPT_LANG, "painless");

        //  es.update.script.params = param1:number,param2:<123> ：number——文档的field；<123>——常量
        conf.set(ESConfig.UPDATE_SCRIPT_PARAMS, "count:<4>");

        conf.set(ESConfig.UPDATE_SCRIPT_PARAMS_JSON, "{\"param1\":1, \"param2\":2}");
    }

    /* 关闭 hadoop 推测机制*/
    @Deprecated
    private void oldDisableSpeculation() {
        //  当某个map所在节点资源有限执行慢时，hadoop会在资源多的节点启动相同的map/reduce task，哪个先完成，就把另一个kill，当访问外部数据库时，会导致并发冲突
        conf.setBoolean("mapred.map.tasks.speculative.execution", false);   // 禁用 map 的 推测执行；
        conf.setBoolean("mapred.reduce.tasks.speculative.execution", false); // 禁用 reduce 的 推测执行
    }

    private void newDisableSpeculation() {
        conf.setBoolean("mapreduce.map.speculative", false);
        conf.setBoolean("mapreduce.reduce.speculative", false);
    }

}
