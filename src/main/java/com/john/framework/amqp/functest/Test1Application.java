package com.john.framework.amqp.functest;

import com.john.framework.amqp.amqp.IPubSub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Test1Application {

    private static final Logger logger = LoggerFactory.getLogger(Test1Application.class);


    public static void main(String[] args) {
        //加载动态库
        logger.info("os_name:{}, os_arch:{},java_library_path:{}", System.getProperty("os.name"), System.getProperty("os.arch"), System.getProperty("java.library.path"));
        System.loadLibrary("KSKingMQAPI");
        SpringApplication.run(Test1Application.class, args);


    }

    private IPubSub getPubSub() {

        return null;

    }


}
