package com.mypro.spider.model;

import org.elasticsearch.hadoop.mr.LinkedMapWritable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author fgzhong
 * @description: inject 读取字段
 * @since 2019/1/12
 */
public class FetchOutModel extends AbstractModel {

    private byte[] content;

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        super.write(dataOutput);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        super.readFields(dataInput);
    }

    @Override
    public LinkedMapWritable writeMap() {
        LinkedMapWritable map = super.writeMap();
        return map;
    }

    @Override
    public void readMap(LinkedMapWritable mapdata) {
        super.readMap(mapdata);
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        String superString = super.toString();
        StringBuffer repr = new StringBuffer();

        return superString + repr.toString();
    }
}
