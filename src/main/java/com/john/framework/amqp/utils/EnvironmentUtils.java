package com.john.framework.amqp.utils;

import com.john.framework.amqp.testcase.TestCaseEnum;
import com.john.framework.amqp.testcase.TestContents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Objects;

/**
 * @Description TODO
 * @Author zqg
 * @Date 2023/5/17
 */

@Component
public class EnvironmentUtils {

    @Autowired
    private Environment environment;

    private static Environment staticEnvironment;

    @PostConstruct
    public void init(){
        staticEnvironment = environment;
    }

    public static int getTestCaseId(){
        return Integer.parseInt(Objects.requireNonNull(staticEnvironment.getProperty("testCaseId")));
    }

    public static int getSendRate(){
        return Integer.parseInt(Objects.requireNonNull(staticEnvironment.getProperty("sendRate")));
    }

    public static String getAppType(){
        return Objects.requireNonNull(staticEnvironment.getProperty("appType"));
    }


    public static String getPacketSize(){
        return staticEnvironment.getProperty("packetsize");
    }

    public static int getPacketSize(TestCaseEnum testCaseEnum){
        return Integer.parseInt(staticEnvironment.getProperty("packetsize",testCaseEnum.msgSize+""));
    }

    public static int getUniqueId(TestCaseEnum testCaseEnum){
        return Integer.parseInt(Objects.requireNonNull(staticEnvironment.getProperty("uniqueId")));
    }

    public static String getQueueName(TestCaseEnum testCaseEnum){
        int uniqueId = getUniqueId(testCaseEnum);
        return testCaseEnum.durable ?
                TestContents.DURABLE_QUEUE_PREFIX + uniqueId : TestContents.NONDURABLE_QUEUE_PREFIX + uniqueId;
    }

    public static String getSeqNoFileName(TestCaseEnum testCaseEnum){
        return getQueueName(testCaseEnum)+".txt";
    }

    public static int getTestTime(){
        return Integer.parseInt(staticEnvironment.getProperty("testTime",
                String.valueOf(TestContents.TEST_TIME_IN_SECONDS)));
    }

    public static int getWarmupTime(){
        return Integer.parseInt(staticEnvironment.getProperty("warmupTime",
                String.valueOf(TestContents.WARMUP_TIME_IN_SECONDS)));
    }

    public static int getCompressLen(){
        return Integer.parseInt(staticEnvironment.getProperty("compressLen", TestContents.MSG_SIZE_OF_2M+""));
    }

    public static int getSender(){
        return Integer.parseInt(staticEnvironment.getProperty("sender", "1"));
    }

    public static String getApiId(){
        return staticEnvironment.getProperty("apiId");
    }

    public static String getGroupId(){
        return staticEnvironment.getProperty("groupId");
    }
}
