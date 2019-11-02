package com.mypro.spider.crawl.es;

import com.mypro.spider.config.ESConfig;
import com.mypro.spider.model.InjectInModel;
import com.mypro.spider.utils.SpiderTool;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.util.Tool;
import org.elasticsearch.hadoop.mr.EsInputFormat;
import org.elasticsearch.hadoop.mr.EsOutputFormat;
import org.elasticsearch.hadoop.mr.LinkedMapWritable;

import java.io.IOException;
import java.util.Map;

/**
 * @author fgzhong
 * @since 2019/1/9
 */
public class ESInjector extends SpiderTool implements Tool {

    private final static String QUERY = "injector.es.query";
    private final static String DEFAULT_QUERY = "?q=*";
    private final static String DEFAULT_UPDATE_SCRIPT_INLINE = "if(doc.uid == 1) ctx._source.uid+=1";

    public ESInjector() {
        super(new Configuration());
    }

    @Override
    public int run(String[] strings) throws Exception {
        Configuration conf = getConf();
        conf.set(ESConfig.NODES, getConf().get(ESConfig.NODES, ESConfig.DEFAULT_NODES));
        conf.set(ESConfig.RESOURCE, getConf().get(ESConfig.RESOURCE, "test/_doc"));
        conf.set(ESConfig.QUERY, getConf().get(QUERY, DEFAULT_QUERY));
        conf.set(ESConfig.MAPPING_ID, ESConfig.DEFAULT_MAPPING_ID);
        conf.set(ESConfig.WRITE_OPERATION, ESConfig.OPERATION_UPSERT);
        conf.set(ESConfig.UPDATE_SCRIPT_INLINE, DEFAULT_UPDATE_SCRIPT_INLINE);
        conf.set(ESConfig.UPDATE_SCRIPT_PARAMS_JSON, "{\"param1\":1, \"param2\":2}");


        conf.setBoolean("mapreduce.map.speculative", false);
        conf.setBoolean("mapreduce.reduce.speculative", false);

        Job job = Job.getInstance(conf, "ESInjector");

        job.setJarByClass(ESInjector.class);
        job.setInputFormatClass(EsInputFormat.class);
        job.setOutputFormatClass(EsOutputFormat.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LinkedMapWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LinkedMapWritable.class);

        job.setMapperClass(ESMap.class);
        job.setReducerClass(ESReduce.class);

        job.waitForCompletion(true);
        return 0;
    }

    @Override
    public Map<String, Object> run(Map<String, Object> args, String crawlId) throws Exception {
        return null;
    }

    public static class ESMap extends Mapper<Text, LinkedMapWritable, Text, LinkedMapWritable> {
        @Override
        public void map(Text key, LinkedMapWritable value, Context context)
                throws IOException, InterruptedException {
            System.out.println(key);
            context.write(key, value);
        }
    }

    public static class ESReduce extends Reducer<Text, LinkedMapWritable, Text, LinkedMapWritable> {
        @Override
        public void reduce(Text key, Iterable<LinkedMapWritable> values, Context context)
                throws IOException, InterruptedException {
            System.out.println(key);
            LinkedMapWritable writ = new LinkedMapWritable();
            writ.put(new Text("uid"), new IntWritable(1));
            context.write(new Text("3"), writ);
        }
    }

    public static void main(String[] args) throws Exception{
        new ESInjector().run(null);
    }
}
