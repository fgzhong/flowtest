package com.mypro.spider.putformat;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.FileAlreadyExistsException;
import org.apache.hadoop.mapred.InvalidJobConfException;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.security.TokenCache;

import java.io.IOException;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/2/25
 */
public class SpiderOutputFormat extends SequenceFileOutputFormat {

    @Override
    public void checkOutputSpecs(JobContext job) throws IOException {
        Path outDir = getOutputPath(job);
        if (outDir == null) {
            throw new InvalidJobConfException("Output directory not set.");
        } else {
            TokenCache.obtainTokensForNamenodes(job.getCredentials(), new Path[]{outDir}, job.getConfiguration());
            if (outDir.getFileSystem(job.getConfiguration()).exists(outDir)) {
            }
        }
    }


    public static void setOutputPath(Job job, Path outputDir) {
        try {
            if (!outputDir.getFileSystem(job.getConfiguration()).exists(outputDir)) {
                outputDir = outputDir.getFileSystem(job.getConfiguration()).makeQualified(outputDir);
            }
        } catch (IOException var3) {
            throw new RuntimeException(var3);
        }

        job.getConfiguration().set("mapreduce.output.fileoutputformat.outputdir", outputDir.toString());
    }
}
