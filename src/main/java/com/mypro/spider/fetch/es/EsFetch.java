package com.mypro.spider.fetch.es;

import com.mypro.spider.config.ESConfig;
import com.mypro.spider.config.SpiderConfig;
import com.mypro.spider.constant.CounterName;
import com.mypro.spider.example.example2pro.parse.WeiBoParse;
import com.mypro.spider.fetch.FetcherRunnable;
import com.mypro.spider.model.AbstractModel;
import com.mypro.spider.model.InjectInModel;
import com.mypro.spider.model.SpiderWritable;
import com.mypro.spider.protocol.ProtocolFactory;
import com.mypro.spider.protocol.httputil.HttpBase;
import com.mypro.spider.putformat.SpiderOutputFormat;
import com.mypro.spider.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.ByteWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.io.compress.SnappyCodec;
import org.apache.hadoop.mapred.JobContext;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.LazyOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.elasticsearch.hadoop.mr.EsInputFormat;
import org.elasticsearch.hadoop.mr.EsOutputFormat;
import org.elasticsearch.hadoop.mr.LinkedMapWritable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author fgzhong
 * @since 2019/1/9
 */
public class EsFetch extends SpiderTool implements Tool {

    private static final Logger LOG = LoggerFactory
            .getLogger(MethodHandles.lookup().lookupClass());
    private final static String DEFAULT_QUERY = "{\"query\":{\"bool\":{\"filter\":[{\"terms\": { \"status\": [\"4\",\"0\"] }},{\"range\":{\"retries\":{\"lte\":10}}}]}}}";



    private EsFetch() {
        super(new Configuration());
    }

    @Override
    public int run(String[] strings) throws Exception {
        Configuration conf = getConf();

        int  docPerPartiton= Math.min(Math.max(Integer.valueOf(strings[0])/20, 1000), 10000);

        conf.set(ESConfig.NODES, conf.get(ESConfig.NODES, ESConfig.DEFAULT_NODES));
        conf.set(ESConfig.RESOURCE, conf.get(ESConfig.RESOURCE, "weibo/_doc"));
//        conf.set(ESConfig.QUERY, conf.get(ESConfig.QUERY, DEFAULT_QUERY));
        conf.set(ESConfig.MAPPING_ID, conf.get(ESConfig.MAPPING_ID, ESConfig.DEFAULT_MAPPING_ID));
        conf.set(ESConfig.WRITE_OPERATION, conf.get(ESConfig.WRITE_OPERATION, ESConfig.OPERATION_UPSERT));
//        conf.setInt(ESConfig.INPUT_MAX_DOCS_PER_PARTITION, conf.getInt(ESConfig.INPUT_MAX_DOCS_PER_PARTITION, ESConfig.DEFAULT_INPUT_MAX_DOCS_PER_PARTITION));
        conf.setInt(ESConfig.INPUT_MAX_DOCS_PER_PARTITION,  docPerPartiton);
        conf.setInt(JobContext.NUM_REDUCES, conf.getInt("mapreduce.job.reduces", SpiderConfig.default_reduce_num));

        conf.setLong("out.reg", System.currentTimeMillis());

        conf.setBoolean("mapreduce.map.speculative", false);
        conf.setBoolean("mapreduce.reduce.speculative", false);

        /** map输出压缩 */
        conf.setBoolean("mapreduce.compress.map.out", true);
        conf.setClass("mapreduce.map.output.compression.codec", SnappyCodec.class, CompressionCodec.class);

        conf.setInt("mapreduce.map.memory.mb", conf.getInt("mapreduce.map.memory.mb", 2048));
        conf.setInt("mapreduce.reduce.memory.mb", conf.getInt("mapreduce.reduce.memory.mb", 5192));
        conf.setInt("mapreduce.map.cpu.vcores", 4);
        conf.setInt("mapreduce.reduce.cpu.vcores", 1);
        conf.set("mapreduce.reduce.java.opts", "-Xmx4018m");
        conf.setInt("mapreduce.job.running.map.limit", conf.getInt("mapreduce.job.running.map.limit", 20));
        conf.setInt("mapreduce.job.running.reduce.limit", 10);


        Job job = Job.getInstance(conf, "EsFetch");


        job.setJarByClass(EsFetch.class);
        job.setInputFormatClass(EsInputFormat.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(SpiderWritable.class);

        LazyOutputFormat.setOutputFormatClass(job, SpiderOutputFormat.class);
        MultipleOutputs.addNamedOutput(job, "Parse", SpiderOutputFormat.class, Text.class, Text.class);
        MultipleOutputs.addNamedOutput(job, "Content", SpiderOutputFormat.class, Text.class, Text.class);
        MultipleOutputs.addNamedOutput(job, "TOES", EsOutputFormat.class, Text.class, LinkedMapWritable.class);

        job.setMapperClass(EsMap.class);
        job.setReducerClass(EsReduce.class);
        Path outputPath = strings != null && strings.length >= 1
                ? new Path(strings[0]) : new Path("/Users/zhuowenwei/Documents/mypro/data");

//        SpiderOutputFormat.setOutputPath(job, new Path("/Users/zhuowenwei/Documents/mypro/data"));


        SpiderOutputFormat.setOutputPath(job, new Path("/user/maplecloudy/mypro/data-weibo"));
        SpiderOutputFormat.setOutputCompressorClass(job, GzipCodec.class);
        FileSystem fs = FileSystem.get(conf);
        for (FileStatus file : fs.listStatus(new Path(
                "/user/maplecloudy/mypro/bin"))) {
            job.addArchiveToClassPath(file.getPath());
        }

        return job.waitForCompletion(true) ? 0 : 1;
    }

    @Override
    public Map<String, Object> run(Map<String, Object> args, String crawlId) {
        return null;
    }

    public static class EsMap extends Mapper<Text, LinkedMapWritable, Text, SpiderWritable> {

        private final static Text STATUS = new Text("status");
        private volatile boolean flag = false;

        private SpiderWritable spiderWritable = new SpiderWritable();

        Configuration conf;
//        TimeoutThreadPoolExecutor executor;
        ThreadPoolExecutor executor;
        ScheduledExecutorService timeoutExecutor;

        Counter allCtr;
        Counter talCtr;
        Counter sucCtr;
        Counter failCtr;
        Counter rtyCtr;

        int threadNum;
        int threadIntervalTime;
        boolean isTimer;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            this.conf = context.getConfiguration();
            threadNum = this.conf.getInt(SpiderConfig.fetchThreadNum, SpiderConfig.default_fetchThreadNum);
            threadIntervalTime = this.conf.getInt(SpiderConfig.fetchThreadWait, SpiderConfig.default_fetchThreadWait);
            executor = new ThreadPoolExecutor(threadNum, threadNum,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>());;
//            executor = TimeoutThreadPoolExecutor.newFixedThreadPool(threadNum,
//                    this.conf.getInt(SpiderConfig.fetchThreadTimeout, SpiderConfig.default_fetchThreadTimeout), TimeUnit.SECONDS);
            isTimer = this.conf.getBoolean(SpiderConfig.fetchTimer, SpiderConfig.default_fetchTimer);

            String httpClassesStr = this.conf.get(SpiderConfig.fetchHttpClasses, SpiderConfig.default_fetchHttpClass);
            if (!httpClassesStr.contains(SpiderConfig.default_fetchHttpClass)) {
                httpClassesStr = httpClassesStr + "," + SpiderConfig.default_fetchHttpClass;
            }
            String[] httpClasses = httpClassesStr.split(",");
            for (String httpClass : httpClasses) {
                if (StringUtils.isBlank(httpClass) || ProtocolFactory.hasClass(httpClass)) { continue; }
                try {
                    HttpBase httpBase = Records.newInstance((Class<HttpBase>) Class.forName(httpClass), this.conf);
                    ProtocolFactory.addHttpClass(httpClass, httpBase);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            allCtr = context.getCounter(CounterName.FETCH_GROUP,CounterName.FETCH_ALL);
            talCtr= context.getCounter(CounterName.FETCH_GROUP,CounterName.FETCH_TOTAL);
            sucCtr = context.getCounter(CounterName.FETCH_GROUP,CounterName.FETCH_SUCCESS);
            failCtr = context.getCounter(CounterName.FETCH_GROUP,CounterName.FETCH_FAIL);
            rtyCtr = context.getCounter(CounterName.FETCH_GROUP,CounterName.FETCH_RETRY);

            timeoutExecutor = new ScheduledThreadPoolExecutor(1,
                    new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());
            timeoutExecutor.scheduleAtFixedRate(
                    () -> {
                        LOG.info(" http all {}, total {}, suc num : {}, rty num : {}, fail num : {},", allCtr.getValue(), talCtr.getValue(), sucCtr.getValue(), rtyCtr.getValue(), failCtr.getValue());
                        context.setStatus(" http all " + allCtr.getValue() +", total " + talCtr.getValue() +", suc num : " + sucCtr.getValue() +", rty num : " + rtyCtr.getValue() +", fail num : " + failCtr.getValue() +",");
                    },
                    0, 1, TimeUnit.MINUTES);
            if (isTimer) {
                timeoutExecutor.scheduleAtFixedRate(() ->{
                    List<HttpBase> httpBases = ProtocolFactory.getHttpClasses();
                    while (true) {
                        try {
                            List<String> proxyList = ProxyUtil.getList();
                            for (HttpBase httpBase : httpBases) {
                                httpBase.addProxyStr(proxyList);
                            }
                            flag = true;
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },0,3, TimeUnit.MINUTES);
                while (!flag) {
                    TimeUnit.MILLISECONDS.sleep(500);
                }
            }
            super.setup(context);
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            executor.shutdown();
            timeoutExecutor.shutdownNow();
            super.cleanup(context);
        }

        @Override
        public void map(Text key, LinkedMapWritable value, Context context) throws IOException, InterruptedException {
            this.allCtr.increment(1);

            if (((ByteWritable)value.get(STATUS)).get() == (byte) 0x00 && ((ByteWritable)value.get(STATUS)).get() < (byte) 0x05) {
                this.talCtr.increment(1);
                InjectInModel model = Records.newInstance(InjectInModel.class);
                model.readMap(value);
                FetcherRunnable runnable = new FetcherRunnable(context, model, true, sucCtr,failCtr,rtyCtr);
                executor.execute(runnable);
            } else {
                spiderWritable.set(value);
                context.write(key, spiderWritable);
            }
        }

        @Override
        public void run(Context context) throws IOException, InterruptedException {
            this.setup(context);
            try {
                while (context.nextKeyValue()) {
                    while (executor.getActiveCount() == threadNum) {
                        TimeUnit.MILLISECONDS.sleep(threadIntervalTime);
                    }
                    this.map(context.getCurrentKey(), context.getCurrentValue(), context);
                }
                long toEndTime = System.currentTimeMillis();
                while (executor.getActiveCount() > 0 && System.currentTimeMillis() - toEndTime < 300000) {
                    TimeUnit.MILLISECONDS.sleep(threadIntervalTime*10);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                LOG.info(" http all {}, total {}, suc num : {}, rty num : {}, fail num : {},", allCtr.getValue(), talCtr.getValue(), sucCtr.getValue(), rtyCtr.getValue(), failCtr.getValue());
                this.cleanup(context);
            }
        }
    }


    public static class EsReduce extends Reducer<Text, SpiderWritable, Text, Writable> {

        private MultipleOutputs mos;
        private Long rex;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);
            rex = context.getConfiguration().getLong("out.reg", System.currentTimeMillis());
            mos = new MultipleOutputs<>(context);
        }

        @Override
        protected void reduce(Text key, Iterable<SpiderWritable> values, Context context) throws IOException, InterruptedException {
            Iterator<SpiderWritable> valueList = values.iterator();
            if (key.toString().startsWith("_model_")) {
                while (valueList.hasNext()) {
                    mos.write("Parse", key, valueList.next().get(),"parse/"+ rex);
                }
            } else if (key.toString().startsWith("_content_")) {
                while (valueList.hasNext()) {
                    mos.write("Content", key, valueList.next().get(), "content/"+ rex);
                }
            } else {
                AbstractModel model = null;
                MapWritable map = null;
                while (valueList.hasNext()) {
                    Writable value = valueList.next().get();
                    if (value instanceof AbstractModel) {
                        if (model == null ) { model = (AbstractModel) value; }
                        else {
                            AbstractModel nextModel = (AbstractModel) value;
                            if (model.getDepth() > nextModel.getDepth()) {
                                model = nextModel;
                            }
                        }
                    } else if (value instanceof MapWritable) {
                        map = (MapWritable) value;
                    } else {
                        System.out.println(value);
                    }
                }
                if (map == null && model != null) {
                    mos.write("TOES", key, model.writeMap());
                }
            }
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            mos.close();
            super.cleanup(context);
        }
    }



    public static void main(String[] args) throws Exception{
        long num = 200000;
        int res = ToolRunner.run(SpiderTool.creatConfiguration(), new EsFetch(), new String[]{""+num});
        System.exit(res);
//        while (num > 0) {
//            int res = ToolRunner.run(SpiderTool.creatConfiguration(), new EsFetch(), new String[]{""+num});
//            if (res != 0) {System.exit(res);}
//            num = SeedUtil.getDocsNum();
//        }
    }
}
