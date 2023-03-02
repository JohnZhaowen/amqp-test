package com.john.framework.amqp.testcase;


/**
 * 测试场景的可调参数
 */
public interface TestContents {

    String BINDING_KEY = "pilot.default.JET.SZ.*.*.*.*.*.*";
    String EXCHAGE = "FAST";
    String NONDURABLE_QUEUE_PREFIX = "fast-shared-durable-";
    String DURABLE_QUEUE_PREFIX = "fast-shared-nondurable-";

    //msg size: bytes
    int MSG_SIZE_OF_256 = 256;
    int MSG_SIZE_OF_1500 = 1500;
    int MSG_SIZE_OF_2M = 2 * 1024 * 1024;

    //send rate: msg/s
    int MSG_SEND_RATE_OF_5K = 5000;
    int MSG_SEND_RATE_OF_3W = 30000;
    int MSG_SEND_RATE_OF_5W = 50000;

    int MSG_SEND_RATE_OF_10W = 100000;

    //test time, warn up time: s
    int TEST_TIME_IN_SECONDS = 5 * 60;
    int WARNUP_TIME_IN_SECONDS = 30;

    //durable: boolean
    boolean MSG_DURABLE = true;
    boolean MSG_NON_DURABLE = false;


    //pub,sub count
    int PUB_1_SUB_1 = 1;
    int PUB_1_SUB_10 = 10;
    int PUB_10_SUB_10 = 100;

    //slow consumer
    boolean SLOW_CONSUMER = true;
    boolean NON_SLOW_CONSUMER = false;

}
