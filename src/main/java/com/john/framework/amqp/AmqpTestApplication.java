package com.john.framework.amqp;

import com.john.framework.amqp.amqp.IPubSub;
import com.john.framework.amqp.amqp.SimplePub;
import com.john.framework.amqp.amqp.PubSubStatistics;
import com.john.framework.amqp.amqp.SimpleSub;
import com.john.framework.amqp.testcase.TestCaseEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.Objects;
import java.util.Scanner;

@SpringBootApplication
public class AmqpTestApplication {

    private static final Logger logger = LoggerFactory.getLogger(AmqpTestApplication.class);

    static final Scanner scanner = new Scanner(System.in);

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        //加载动态库
        logger.info("os_name:{}, os_arch:{},java_library_path:{}",
                System.getProperty("os.name"),
                System.getProperty("os.arch"),
                System.getProperty("java.library.path"));
        System.loadLibrary("KSKingMQAPI");
        SpringApplication.run(AmqpTestApplication.class, args);
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //pressAnyKey("Press any key to stop..");
    }


    private static void pressAnyKey(String hint) {
        System.out.println(hint);
        scanner.nextLine();
    }

    @Bean
    public IPubSub pubSub(){
        IPubSub pubSub;
        int testCaseId = Integer.parseInt(Objects.requireNonNull(environment.getProperty("testCaseId")));
        TestCaseEnum testCaseEnum = TestCaseEnum.getById(testCaseId);
        if("sub".equalsIgnoreCase(environment.getProperty("appType"))){
            pubSub = new SimpleSub();
        }else if("pub".equalsIgnoreCase(environment.getProperty("appType"))){
            int sendRate = Integer.parseInt(Objects.requireNonNull(environment.getProperty("sendRate")));
            testCaseEnum.msgSendRate = sendRate;
            pubSub = new SimplePub();
        }else if("pubsub".equalsIgnoreCase(environment.getProperty("appType"))){
            int sendRate = Integer.parseInt(Objects.requireNonNull(environment.getProperty("sendRate")));
            testCaseEnum.msgSendRate = sendRate;
            pubSub = new PubSubStatistics(testCaseEnum,environment);
        } else {
            return null;
        }
        pubSub.init();
        return pubSub;

    }
}
