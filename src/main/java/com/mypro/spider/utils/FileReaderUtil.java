package com.mypro.spider.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.FileReader;
import org.apache.avro.file.SeekableInput;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.mapred.FsInput;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.hadoop.ParquetReader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fgzhong
 * @description: 各种文件读取
 * @since 2019/5/18
 */
public class FileReaderUtil {

    /**
     * AvroFile
     * @param pathstr
     * @throws Exception
     */
    public static void readAvro(String pathstr) throws Exception {
        Path path = new Path(pathstr);
        Configuration config = new Configuration();
        SeekableInput input = new FsInput(path, config);
        DatumReader<GenericRecord> reader = new GenericDatumReader<>();
        FileReader<GenericRecord> fileReader = DataFileReader.openReader(input, reader);
        Schema avroSchema = fileReader.getSchema();
        System.out.println(avroSchema);
        for (GenericRecord datum : fileReader) {
                System.out.println(datum.toString());
        }
        fileReader.close();
        System.exit(0);
    }

    /**
     * SequenceFile
     * @param pathStr
     * @throws Exception
     */
    public static void readSequence(String pathStr) throws Exception {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(pathStr), conf);
        Path path = new Path(pathStr);
        SequenceFile.Reader reader = null;
        try {
            reader = new SequenceFile.Reader(fs, path, conf);
            Writable key = (Writable)
                    ReflectionUtils.newInstance(reader.getKeyClass(), conf);
            Writable value = (Writable)
                    ReflectionUtils.newInstance(reader.getValueClass(), conf);
            long position = reader.getPosition();
            while (reader.next(key, value)) {
                String syncSeen = reader.syncSeen() ? "*" : "";
                System.out.printf("[%s%s]\t%s\t%s\n", position, syncSeen, key, value);
                position = reader.getPosition(); // beginning of next record
            }
        } finally {
            IOUtils.closeStream(reader);
        }
    }


    /** 从Sequence file中反序列化avro数据 */
    public static void s() throws Exception{
        Schema avroSchema = new Schema.Parser().parse(new File("src/main/resources/***.avsc"));
        Path sequencePath = new Path("hdfs://ip:9000/xx.file");
        SequenceFile.Reader sequenceReader = new SequenceFile.Reader(new Configuration(), SequenceFile.Reader.file(sequencePath));

        int i = 0;
        try {
            Text key = new Text();
            BytesWritable value = new BytesWritable();
            GenericRecord record;
            while (sequenceReader.next(key, value)) {
                i++;
                ByteArrayInputStream in = new ByteArrayInputStream(value.getBytes());
                BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(in, null);
                DatumReader<GenericRecord> avroReader = new SpecificDatumReader<>(avroSchema);
                record = avroReader.read(null, decoder);
            }
        } finally {
            IOUtils.closeStream(sequenceReader);
        }
    }

    /**
     * ParquetFile
     * @param path
     * @param maxLine
     * @return
     * @throws IOException
     */
    public static Map<String,List<String[]>> readParquet(String path, int maxLine) throws IOException {
        Map<String,List<String[]>> parquetInfo=new HashMap<>();
        List<String[]> dataList=new ArrayList<>();
        Schema.Field[] fields = null;
        String[] fieldNames = new String[0];
        try (
                ParquetReader<GenericData.Record> reader =
                        AvroParquetReader.<GenericData.Record>builder(new Path(path)).build()
        ){
            int  x=0;
            GenericData.Record record;
            //解析Parquet数据逐行读取
            while ((record = reader.read()) != null && x<maxLine) {
                //读取第一行获取列头信息
                if (fields == null) {
                    final List<Schema.Field> fieldsList = record.getSchema().getFields();
                    fieldNames = getFieldNames(fields = fieldsList.toArray(new Schema.Field[0]));
                    System.out.println("列头:"+String.join(",", fieldNames));
                    dataList.add(fieldNames);
                    parquetInfo.put("head",dataList);
                    dataList=new ArrayList<>();
                }
                int i = 0;
                String[]dataString=new String[fieldNames.length];
                //读取数据获取列头信息
                for (final String fieldName : fieldNames) {
                    String recordData=record.get(fieldName).toString();
                    if(recordData.contains("type")){
                        List<HashMap> dataFormValue= JSONArray.parseArray(JSONObject.parseObject(recordData).get("values").toString(),HashMap.class);
                        StringBuilder datas = new StringBuilder();
                        for(HashMap data:dataFormValue){
                            datas.append(data.get("element").toString()).append(",");
                        }
                        datas.deleteCharAt(datas.length() - 1);
                        recordData=datas.toString();
                    }
                    dataString[i++] =recordData;
                }
                dataList.add(dataString);
                ++x;
            }
        }
        parquetInfo.put("data",dataList);
        return parquetInfo;
    }

    private static String[] getFieldNames(final Schema.Field[] fields) {
        final String[] fieldNames = new String[fields.length];
        int i = 0;
        for (final Schema.Field field : fields) {
            fieldNames[i++] = field.name();
        }
        return fieldNames;
    }

}
