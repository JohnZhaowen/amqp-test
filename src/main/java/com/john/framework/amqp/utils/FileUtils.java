package com.john.framework.amqp.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
    public static Long readSeqNo(String fileName) {
        File f = new File(fileName);
        if (!f.exists() || !f.isFile()) {
            LOG.warn("file:{},不存在！",fileName);
            return -1l;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                return Long.parseLong(line.trim());
            }
            return -1l;
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(f))) {
            writer.write(seq_no+"");
            writer.flush();
        } catch (IOException ex) {
            LOG.error("write file:{} error: ",fileName, ex);
            throw new RuntimeException("write file error:"+fileName);
        }
    }
}
