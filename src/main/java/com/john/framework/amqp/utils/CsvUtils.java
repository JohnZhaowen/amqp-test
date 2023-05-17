package com.john.framework.amqp.utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CsvUtils {

    private static final Logger LOG = LoggerFactory.getLogger(CsvUtils.class);

    public static void writeCsvWithMultiLines(String fileName, final Collection<String[]> datas) {

        if (datas == null || datas.isEmpty()) {
            return;
        }
        File f = new File(fileName);

        if (!f.exists() || !f.isFile()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("file create error: ", e);
            }
        }

        try (
                FileOutputStream fileOutputStream = new FileOutputStream(fileName, true);
                CSVWriter writer = new CSVWriter(new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8),
                        CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END);
        ) {
            writer.writeAll(datas);
            writer.flush();

        } catch (Exception e) {
            LOG.error("write csv file error: ", e);
        }
    }

    public static void writeCsvWithOneLine(String fileName, String[] line) {
        if (line == null) {
            return;
        }

        List<String[]> datas = new ArrayList<>();
        datas.add(line);

        writeCsvWithMultiLines(fileName, datas);
    }


}
