package com.mypro.spider.parse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.mypro.spider.config.ESConfig;
import com.mypro.spider.putformat.SpiderInputFormat;
import com.mypro.spider.putformat.SpiderOutputFormat;
import com.mypro.spider.utils.Map2Writable;
import com.mypro.spider.utils.SpiderTool;
import com.mypro.spider.utils.UrlLengthUtil;
import org.apache.avro.mapreduce.AvroJob;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.io.compress.SnappyCodec;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.output.LazyOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.elasticsearch.hadoop.mr.EsInputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @author fgzhong
 * @since 2019/3/31
 */
public class FixContent extends SpiderTool implements Tool {

    private static final Logger LOG = LoggerFactory
            .getLogger(MethodHandles.lookup().lookupClass());

    private FixContent() {
        super(new Configuration());
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = getConf();
        System.out.println(conf.get("test"));
        conf.setLong("out.reg", System.currentTimeMillis());

        conf.set(ESConfig.NODES, conf.get(ESConfig.NODES, ESConfig.DEFAULT_NODES));
        conf.set(ESConfig.RESOURCE, conf.get(ESConfig.RESOURCE, "array3/_doc"));
        conf.set(ESConfig.QUERY,  "?q=*");
        conf.setBoolean("mapreduce.map.speculative", false);

        /** map输出压缩 */
        conf.setBoolean("mapreduce.compress.map.out", true);
        conf.setClass("mapreduce.map.output.compression.codec", SnappyCodec.class, CompressionCodec.class);

        conf.setInt("mapreduce.map.memory.mb", conf.getInt("mapreduce.map.memory.mb", 2048));
        conf.setInt("mapreduce.reduce.memory.mb", conf.getInt("mapreduce.reduce.memory.mb", 4096));
        conf.setInt("mapreduce.reduce.cpu.vcores", 1);
        conf.setInt("mapreduce.job.reduces", 200);
//        conf.set("mapreduce.reduce.java.opts", "-Xmx4018m");
//        conf.setInt("mapreduce.job.running.map.limit", conf.getInt("mapreduce.job.running.map.limit", 50));
//        conf.setInt(JobContext.NUM_REDUCES, conf.getInt("mapreduce.job.reduces", SpiderConfig.default_reduce_num));
//        conf.setInt("mapreduce.job.running.reduce.limit", 10);
        conf.setInt(ESConfig.INPUT_MAX_DOCS_PER_PARTITION, 300000);
        conf.setFloat("mapreduce.reduce.shuffle.memory.limit.percent", 0.1f);
        conf.setInt("mapreduce.reduce.shuffle.parallelcopies", 10);


        Job job = Job.getInstance(conf, "Fix-Content");

        job.setJarByClass(FixContent.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setReducerClass(FixReduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        LazyOutputFormat.setOutputFormatClass(job, SpiderOutputFormat.class);

//        Path inputPath = new Path("/Users/zhuowenwei/Documents/mypro/data/1554636001577-r-00000");
//        Path outputPath = new Path("/Users/zhuowenwei/Documents/mypro/data");

        Path inputPath = new Path("/user/maplecloudy/mypro/data-v/content");
        Path outputPath = new Path("/user/maplecloudy/mypro/data-fix/content-v3");

        MultipleInputs.addInputPath(job, outputPath, EsInputFormat.class,EsMap.class);
        MultipleInputs.addInputPath(job, inputPath, SpiderInputFormat.class,UnFixContentMap.class);
//        Math.max(minSize, Math.min(maxSize, blockSize))
        //536870912  268435456   134217728
        SpiderInputFormat.setMaxInputSplitSize(job, 536870912);
        SpiderInputFormat.setMinInputSplitSize(job, 536870912);
        SpiderOutputFormat.setOutputPath(job, outputPath);
        SpiderOutputFormat.setOutputCompressorClass(job, GzipCodec.class);
        FileSystem fs = FileSystem.get(conf);
        for (FileStatus file : fs.listStatus(new Path(
                "/user/maplecloudy/mypro/bin"))) {
            job.addArchiveToClassPath(file.getPath());
        }

        return job.waitForCompletion(true) ? 0 : 1;
    }

    private static class UnFixContentMap extends Mapper <Text, Text, Text, Text>{


        private Text mapKey = new Text();

        @Override
        protected void map(Text key, Text value, Context context) throws IOException, InterruptedException {
            JSONObject content = JSON.parseObject(value.toString());
            mapKey.set(UrlLengthUtil.shortenCodeUrl(content.getString("url")));
            context.write(mapKey, value);
        }
    }

    private static class EsMap extends Mapper <Text, MapWritable, Text, Text>{

        private Text uid = new Text("uid");
        private Text mapValue = new Text();
        @Override
        protected void map(Text key, MapWritable value, Context context) throws IOException, InterruptedException {
            mapValue.set(JSON.toJSONString(Map2Writable.deepForWritableToMap(value, null)));
            context.write((Text) value.get(uid), mapValue);
        }
    }

    private static class FixReduce extends Reducer <Text, Text, Text, Writable>{

        private Text reduceValue = new Text();
        private JSONObject esData;
        private List<JSONObject> contentList = Lists.newArrayList();
        private JSONObject readyContent = new JSONObject();


        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            Iterator value = values.iterator();

            while (value.hasNext()) {
                JSONObject json = JSONObject.parseObject(value.next().toString());
                if (json.containsKey("uid")) {
                    esData = json;
                } else {
                    contentList.add(json);
                }
            }
            for (JSONObject content : contentList) {
                readyContent.putAll(content);
                readyContent.put("data", esData.get("mapWritableStr"));
                reduceValue.set(readyContent.toJSONString());
                context.write(key, reduceValue);
                readyContent.clear();
            }
            contentList.clear();
            context.getCounter("FixReduce", "count").increment(1);

        }
    }


    @Override
    public Map<String, Object> run(Map<String, Object> args, String crawlId) throws Exception {
        return null;
    }

    public static void main(String[] args) throws Exception{
        int res = ToolRunner.run(SpiderTool.creatConfiguration(), new FixContent(), args);
        System.exit(res);
    }
}
