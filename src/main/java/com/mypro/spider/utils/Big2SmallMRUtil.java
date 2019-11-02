package com.mypro.spider.utils;

import com.mypro.spider.putformat.SpiderInputFormat;
import com.mypro.spider.putformat.SpiderOutputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.LazyOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.HashPartitioner;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 * @author fgzhong
 * @description: 大 变 小
 * @since 2019/4/13
 */
public class Big2SmallMRUtil extends SpiderTool implements Tool {

    private Big2SmallMRUtil() {
        super(new Configuration());
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = getConf();
        System.out.println(conf.get("test"));

        Job job = Job.getInstance(conf, "Big2SmallMRUtil");
        job.setJarByClass(Big2SmallMRUtil.class);

        job.setInputFormatClass(SpiderInputFormat.class);
        job.setOutputFormatClass(SpiderOutputFormat.class);
        LazyOutputFormat.setOutputFormatClass(job, SpiderOutputFormat.class);

        job.setMapperClass(UtilMap.class);
        job.setReducerClass(UtilReduce.class);
        job.setNumReduceTasks(30);
        job.setPartitionerClass(RandomPartitoner.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        SpiderOutputFormat.setOutputCompressorClass(job, GzipCodec.class);
        SpiderInputFormat.setMaxInputSplitSize(job, 134217728);
        SpiderInputFormat.setMinInputSplitSize(job, 134217728);

        Path inputPath = new Path("/user/maplecloudy/mypro/data-fix/content-v3/part-r-00000");
        Path outputPath = new Path("/user/maplecloudy/mypro/small-data");

        SpiderInputFormat.addInputPath(job, inputPath);
        SpiderOutputFormat.setOutputPath(job, outputPath);

        FileSystem fs = FileSystem.get(conf);
        for (FileStatus file : fs.listStatus(new Path(
                "/user/maplecloudy/mypro/bin"))) {
            job.addArchiveToClassPath(file.getPath());
        }

        return job.waitForCompletion(true) ? 0 : 1;
    }

    private static class UtilMap extends Mapper<Text, Text, Text, Text> {

        @Override
        protected void map(Text key, Text value, Context context) throws IOException, InterruptedException {
            context.write(key, value);
        }
    }

    private static class UtilReduce extends Reducer<Text, Text, Text, Text> {

        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            Iterator value = values.iterator();
            while (value.hasNext()) {
                context.write(key, (Text) value.next());
            }
        }
    }

    private static class RandomPartitoner extends Partitioner {

        private Random random = new Random();

        @Override
        public int getPartition(Object o, Object o2, int numPartitions) {
            return random.nextInt(numPartitions);
        }
    }


    @Override
    public Map<String, Object> run(Map<String, Object> args, String crawlId) throws Exception {
        return null;
    }

    public static void main(String[] args) throws Exception{
        int res = ToolRunner.run(SpiderTool.creatConfiguration(), new Big2SmallMRUtil(), args);
        System.exit(res);
    }
}
