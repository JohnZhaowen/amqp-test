package com.john.framework.amqp.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * @Description TODO
 * @Author zqg
 * @Date 2023/5/17
 */
public class FileUtils {

    private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

    private FileUtils() {
    }

    //读文件
    //面对持久化情况 第一次如果读文件不存 从0序号开始收所有的信息
    public static Long readSeqNo(String fileName) {
        File f = new File(fileName);
        if (!f.exists() || !f.isFile()) {
            LOG.warn("file:{},不存在！",fileName);
            return 0l;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                return Long.parseLong(line.trim());
            }
            return 0l;
        } catch (IOException ex) {
            LOG.error("read file:{} error: ",fileName, ex);
            throw new RuntimeException("read file error:"+fileName);
        }
    }

    //读文件
    public static void writeSeqNo(String fileName,long seq_no) {
        File f = new File(fileName);
        if (!f.exists() || !f.isFile()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (FileOutputStream outStream = new FileOutputStream(f, false);
             OutputStreamWriter writer = new OutputStreamWriter(outStream, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(writer)) {
            bw.write(seq_no + "");
            bw.newLine();
        } catch (IOException ex) {
            LOG.error("write file:{} error: ",fileName, ex);
            throw new RuntimeException("write file error:"+fileName);
        }
    }

    public static void main(String[] args){
        for(int i=0;i<1000000;i++){
            writeSeqNo("100002.txt",i);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
