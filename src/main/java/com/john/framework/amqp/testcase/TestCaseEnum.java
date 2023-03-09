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
            TestContents.PUB_1_SUB_10,
            TestContents.MSG_NON_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    EXCELRANGE_TEST_2(
            2,
            TestContents.MSG_SIZE_OF_2M,
            TestContents.PUB_1_SUB_10,
            TestContents.MSG_NON_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    /**
     * 1.5K包大小，测试1：1场景
     */
    MKTDATA_TEST_3(
            3,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.PUB_1_SUB_1,
            TestContents.MSG_NON_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    MKTDATA_TEST_4(
            4,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.PUB_1_SUB_1,
            TestContents.MSG_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    /**
     * 1.5K包大小，测试1：10场景
     */
    MKTDATA_TEST_5(
            5,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.PUB_1_SUB_10,
            TestContents.MSG_NON_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    MKTDATA_TEST_6(
            6,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.PUB_1_SUB_10,
            TestContents.MSG_NON_DURABLE,
            TestContents.SLOW_CONSUMER),
    MKTDATA_TEST_7(
            7,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.PUB_1_SUB_10,
            TestContents.MSG_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    MKTDATA_TEST_8(
            8,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.PUB_1_SUB_10,
            TestContents.MSG_DURABLE,
            TestContents.SLOW_CONSUMER);

    public final int testCaseId;

    public final int msgSize;
    public int msgSendRate;
    public final int pubsubCount;
    public final boolean durable;
    public final boolean slowConsumer;

    TestCaseEnum(int testCaseId, int msgSize, int pubsubCount, boolean durable, boolean slowConsumer) {
        this.testCaseId = testCaseId;
        this.msgSize = msgSize;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TestCase: [\n");
        sb.append("testCaseId: ").append(testCaseId).append("\n")
                .append("msgSize: ").append(msgSize).append("bytes \n")
                .append("msgSendRate: ").append(msgSendRate).append("\n")
                .append("pubsubCount: ").append(pubsubCount).append("\n")
                .append("durable").append(durable).append("\n")
                .append("slowConsumer: ").append(slowConsumer).append("\n")
                .append("]\n");
        return sb.toString();

    }

    public static void main(String[] args) {
        TestCaseEnum testCaseEnum = TestCaseEnum.getById(1);
        testCaseEnum.msgSendRate = 5000;

        System.out.println(testCaseEnum.msgSendRate);
    }
}
