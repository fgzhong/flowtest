package com.mypro.spider.parse;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.mypro.spider.putformat.SpiderInputFormat;
import com.mypro.spider.putformat.SpiderOutputFormat;
import com.mypro.spider.utils.Records;
import com.mypro.spider.utils.SpiderTool;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.io.compress.SnappyCodec;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.LazyOutputFormat;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author fgzhong
 * @since 2019/3/31
 */
public class ParseContent extends SpiderTool implements Tool {

    private static final Logger LOG = LoggerFactory
            .getLogger(MethodHandles.lookup().lookupClass());

    private ParseContent() {
        super(new Configuration());
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = getConf();
        System.out.println(conf.get("test"));

        /** map输出压缩 */
        conf.setBoolean("mapreduce.compress.map.out", true);
        conf.setClass("mapreduce.map.output.compression.codec", SnappyCodec.class, CompressionCodec.class);

        conf.setInt("mapreduce.map.memory.mb", conf.getInt("mapreduce.map.memory.mb", 2048));
        conf.setInt("mapreduce.reduce.memory.mb", conf.getInt("mapreduce.reduce.memory.mb", 2048));
        conf.setInt("mapreduce.reduce.cpu.vcores", 1);

        Job job = Job.getInstance(conf, "ParseContent");
        job.setJarByClass(ParseContent.class);

        job.setInputFormatClass(SpiderInputFormat.class);
        job.setOutputFormatClass(SpiderOutputFormat.class);
        LazyOutputFormat.setOutputFormatClass(job, SpiderOutputFormat.class);

        job.setMapperClass(ParseMap.class);
        job.setReducerClass(ParseReduce.class);
        job.setNumReduceTasks(200);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        SpiderOutputFormat.setOutputCompressorClass(job, GzipCodec.class);
        SpiderInputFormat.setMaxInputSplitSize(job, 536870912);
        SpiderInputFormat.setMinInputSplitSize(job, 536870912);

//        Path inputPath = new Path("/Users/zhuowenwei/Documents/mypro/data/part-r-00020");
//        Path outputPath = new Path("/Users/zhuowenwei/Documents/mypro/data");

        Path inputPath = new Path("/user/maplecloudy/mypro/data-fix/content-v3");
        Path outputPath = new Path("/user/maplecloudy/mypro/data-fix/parse-v3");

        SpiderInputFormat.addInputPath(job, inputPath);
        SpiderOutputFormat.setOutputPath(job, outputPath);

        FileSystem fs = FileSystem.get(conf);
        for (FileStatus file : fs.listStatus(new Path(
                "/user/maplecloudy/mypro/bin"))) {
            job.addArchiveToClassPath(file.getPath());
        }

        return job.waitForCompletion(true) ? 0 : 1;
    }

    private static class ParseMap extends Mapper <Text, Text, Text, Text>{

        private HashMap<String, Parse> parseClasses = Maps.newHashMap();
        private Text mapValue = new Text();


        @Override
        protected void map(Text key, Text value, Context context) throws IOException, InterruptedException {
            Content js = JSONObject.parseObject(value.toString(),Content.class);
            String parseClass = js.getParseClass();
            if (!parseClasses.containsKey(parseClass)) {
                try {
                    Parse parse = Records.newInstance((Class<Parse>) Class.forName(parseClass));
                    parseClasses.put(parseClass, parse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (parseClasses.containsKey(parseClass)) {
                Parse parse = parseClasses.get(parseClass);
                try {
                    List<Object> oj = parse.parse(js.getUrl(), js);
                    for (Object o : oj) {
                        if (o instanceof ParseModel) {
                            mapValue.set(ParseModel.toJson((ParseModel)o));
                            context.write(key, mapValue);
                        }
                    }
                } catch (Exception e) {
                    String error = StringUtils.stringifyException(e);
                    LOG.info(" html parse error for url : {} , reason : {}", js.getUrl(), error.substring(0,Math.min(error.length(), 500) ));
                }
            }
        }
    }

    private static class ParseReduce extends Reducer <Text, Text, Text, Text>{

        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            Iterator value = values.iterator();
            while (value.hasNext()) {
                context.write(key, (Text) value.next());
            }
        }
    }


    @Override
    public Map<String, Object> run(Map<String, Object> args, String crawlId) throws Exception {
        return null;
    }

    public static void main(String[] args) throws Exception{
        int res = ToolRunner.run(SpiderTool.creatConfiguration(), new ParseContent(), args);
        System.exit(res);
    }
}
