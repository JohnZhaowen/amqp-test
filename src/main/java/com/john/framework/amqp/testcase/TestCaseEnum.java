package com.john.framework.amqp.testcase;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 36个测试场景
 */
public enum TestCaseEnum {

    HEARTBEAT_TEST_1(
            1,
            TestContents.MSG_SIZE_OF_256,
            TestContents.MSG_SEND_RATE_OF_5K,
            TestContents.PUB_1_SUB_10,
            TestContents.MSG_NON_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    HEARTBEAT_TEST_2(
            2,
            TestContents.MSG_SIZE_OF_256,
            TestContents.MSG_SEND_RATE_OF_3W,
            TestContents.PUB_1_SUB_10,
            TestContents.MSG_NON_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    HEARTBEAT_TEST_3(
            3,
            TestContents.MSG_SIZE_OF_256,
            TestContents.MSG_SEND_RATE_OF_5W,
            TestContents.PUB_1_SUB_10,
            TestContents.MSG_NON_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    HEARTBEAT_TEST_4(
            4,
            TestContents.MSG_SIZE_OF_256,
            TestContents.MSG_SEND_RATE_OF_10W,
            TestContents.PUB_1_SUB_10,
            TestContents.MSG_NON_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    EXCELRANGE_TEST_5(
            5,
            TestContents.MSG_SIZE_OF_2M,
            TestContents.MSG_SEND_RATE_OF_5K,
            TestContents.PUB_1_SUB_1,
            TestContents.MSG_NON_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    EXCELRANGE_TEST_6(
            6,
            TestContents.MSG_SIZE_OF_2M,
            TestContents.MSG_SEND_RATE_OF_3W,
            TestContents.PUB_1_SUB_1,
            TestContents.MSG_NON_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    EXCELRANGE_TEST_7(
            7,
            TestContents.MSG_SIZE_OF_2M,
            TestContents.MSG_SEND_RATE_OF_5K,
            TestContents.PUB_1_SUB_10,
            TestContents.MSG_NON_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    EXCELRANGE_TEST_8(
            8,
            TestContents.MSG_SIZE_OF_2M,
            TestContents.MSG_SEND_RATE_OF_3W,
            TestContents.PUB_1_SUB_10,
            TestContents.MSG_NON_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    MKTDATA_TEST_9(
            9,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.MSG_SEND_RATE_OF_5K,
            TestContents.PUB_1_SUB_1,
            TestContents.MSG_NON_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    MKTDATA_TEST_10(
            10,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.MSG_SEND_RATE_OF_3W,
            TestContents.PUB_1_SUB_1,
            TestContents.MSG_NON_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    MKTDATA_TEST_11(
            11,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.MSG_SEND_RATE_OF_5W,
            TestContents.PUB_1_SUB_1,
            TestContents.MSG_NON_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    MKTDATA_TEST_12(
            12,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.MSG_SEND_RATE_OF_10W,
            TestContents.PUB_1_SUB_1,
            TestContents.MSG_NON_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    MKTDATA_TEST_13(
            13,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.MSG_SEND_RATE_OF_5K,
            TestContents.PUB_1_SUB_1,
            TestContents.MSG_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    MKTDATA_TEST_14(
            14,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.MSG_SEND_RATE_OF_3W,
            TestContents.PUB_1_SUB_1,
            TestContents.MSG_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    MKTDATA_TEST_15(
            15,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.MSG_SEND_RATE_OF_5W,
            TestContents.PUB_1_SUB_1,
            TestContents.MSG_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    MKTDATA_TEST_16(
            16,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.MSG_SEND_RATE_OF_10W,
            TestContents.PUB_1_SUB_1,
            TestContents.MSG_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    MKTDATA_TEST_17(
            17,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.MSG_SEND_RATE_OF_5K,
            TestContents.PUB_1_SUB_10,
            TestContents.MSG_NON_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    MKTDATA_TEST_18(
            18,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.MSG_SEND_RATE_OF_3W,
            TestContents.PUB_1_SUB_10,
            TestContents.MSG_NON_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    MKTDATA_TEST_19(
            19,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.MSG_SEND_RATE_OF_5W,
            TestContents.PUB_1_SUB_10,
            TestContents.MSG_NON_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    MKTDATA_TEST_20(
            20,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.MSG_SEND_RATE_OF_10W,
            TestContents.PUB_1_SUB_10,
            TestContents.MSG_NON_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    MKTDATA_TEST_21(
            21,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.MSG_SEND_RATE_OF_5K,
            TestContents.PUB_1_SUB_10,
            TestContents.MSG_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    MKTDATA_TEST_22(
            22,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.MSG_SEND_RATE_OF_3W,
            TestContents.PUB_1_SUB_10,
            TestContents.MSG_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    MKTDATA_TEST_23(
            23,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.MSG_SEND_RATE_OF_5W,
            TestContents.PUB_1_SUB_10,
            TestContents.MSG_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    MKTDATA_TEST_24(
            24,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.MSG_SEND_RATE_OF_10W,
            TestContents.PUB_1_SUB_10,
            TestContents.MSG_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    MKTDATA_TEST_25(
            25,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.MSG_SEND_RATE_OF_5K,
            TestContents.PUB_1_SUB_10,
            TestContents.MSG_NON_DURABLE,
            TestContents.SLOW_CONSUMER),
    MKTDATA_TEST_26(
            26,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.MSG_SEND_RATE_OF_3W,
            TestContents.PUB_1_SUB_10,
            TestContents.MSG_NON_DURABLE,
            TestContents.SLOW_CONSUMER),
    MKTDATA_TEST_27(
            27,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.MSG_SEND_RATE_OF_5W,
            TestContents.PUB_1_SUB_10,
            TestContents.MSG_NON_DURABLE,
            TestContents.SLOW_CONSUMER),
    MKTDATA_TEST_28(
            28,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.MSG_SEND_RATE_OF_10W,
            TestContents.PUB_1_SUB_10,
            TestContents.MSG_NON_DURABLE,
            TestContents.SLOW_CONSUMER),
    MKTDATA_TEST_29(
            29,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.MSG_SEND_RATE_OF_5K,
            TestContents.PUB_1_SUB_10,
            TestContents.MSG_DURABLE,
            TestContents.SLOW_CONSUMER),
    MKTDATA_TEST_30(
            30,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.MSG_SEND_RATE_OF_3W,
            TestContents.PUB_1_SUB_10,
            TestContents.MSG_DURABLE,
            TestContents.SLOW_CONSUMER),
    MKTDATA_TEST_31(
            31,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.MSG_SEND_RATE_OF_5W,
            TestContents.PUB_1_SUB_10,
            TestContents.MSG_DURABLE,
            TestContents.SLOW_CONSUMER),
    MKTDATA_TEST_32(
            32,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.MSG_SEND_RATE_OF_10W,
            TestContents.PUB_1_SUB_10,
            TestContents.MSG_DURABLE,
            TestContents.SLOW_CONSUMER),
    MKTDATA_TEST_33(
            33,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.MSG_SEND_RATE_OF_5K,
            TestContents.PUB_10_SUB_10,
            TestContents.MSG_NON_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    MKTDATA_TEST_34(
            34,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.MSG_SEND_RATE_OF_3W,
            TestContents.PUB_10_SUB_10,
            TestContents.MSG_NON_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    MKTDATA_TEST_35(
            35,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.MSG_SEND_RATE_OF_5W,
            TestContents.PUB_10_SUB_10,
            TestContents.MSG_NON_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    MKTDATA_TEST_36(
            36,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.MSG_SEND_RATE_OF_10W,
            TestContents.PUB_10_SUB_10,
            TestContents.MSG_NON_DURABLE,
            TestContents.NON_SLOW_CONSUMER);

    public final int testCaseId;

    public final int msgSize;
    public final int msgSendRate;
    public final int pubsubCount;
    public final boolean durable;
    public final boolean slowConsumer;

    TestCaseEnum(int testCaseId, int msgSize, int msgSendRate, int pubsubCount, boolean durable, boolean slowConsumer) {
        this.testCaseId = testCaseId;
        this.msgSize = msgSize;
        this.msgSendRate = msgSendRate;
        this.pubsubCount = pubsubCount;
        this.durable = durable;
        this.slowConsumer = slowConsumer;
    }

    public static TestCaseEnum getById(int testCaseId) {
        return Arrays.stream(TestCaseEnum.values()).
                filter(e -> e.testCaseId == testCaseId).findFirst().orElse(null);
    }

    public static List<TestCaseEnum> getByIds(List<Integer> testCaseIds) {
        return Arrays.stream(TestCaseEnum.values()).
                filter(e -> testCaseIds.contains(e.testCaseId)).
                collect(Collectors.toList());
    }

    public static List<TestCaseEnum> getDurableCases() {
        return Arrays.stream(TestCaseEnum.values()).
                filter(e -> e.durable).
                collect(Collectors.toList());
    }

    public static List<TestCaseEnum> getNonDurableCases() {
        return Arrays.stream(TestCaseEnum.values()).
                filter(e -> !e.durable).
                collect(Collectors.toList());
    }

    public static List<TestCaseEnum> getSlowConsumerCases() {
        return Arrays.stream(TestCaseEnum.values()).
                filter(e -> e.slowConsumer).
                collect(Collectors.toList());
    }

    public static List<TestCaseEnum> getNonSlowConsumerCases() {
        return Arrays.stream(TestCaseEnum.values()).
                filter(e -> !e.slowConsumer).
                collect(Collectors.toList());
    }

    public static List<TestCaseEnum> getDurableAndSlowConsumerCases() {
        return Arrays.stream(TestCaseEnum.values()).
                filter(e -> e.durable && e.slowConsumer).
                collect(Collectors.toList());
    }
}
