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
     *
     * 功能测试4 + 性能测试
     */
    MKTDATA_TEST_3(
            3,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.PUB_1_SUB_1,
            TestContents.MSG_NON_DURABLE,
            TestContents.NON_SLOW_CONSUMER),

    /**
     * 功能测试3和4，性能测试
     */
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
            TestContents.SLOW_CONSUMER),

    /**
     * 功能测试1
     * 1.2个Producer
     * 2.1个主题A
     * 3.1个Broker集群，集群中两个节点
     * 4.1个Consumer，订阅主题A
     */
    MKTDATA_TEST_9(
            9,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.PUB_2_SUB_1,
            TestContents.MSG_NON_DURABLE,
            TestContents.NON_SLOW_CONSUMER),

    /**
     * 功能测试2
     * 1.1个Producer
     * 2.1个主题A
     * 3.1个Broker集群，集群中两个节点
     * 4.5个Consumer，订阅主题A
     * 5.5个Consumer的5个bindingKey各不一样
     */
    MKTDATA_TEST_10(
            10,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.PUB_1_SUB_5,
            TestContents.MSG_NON_DURABLE,
            TestContents.NON_SLOW_CONSUMER),
    /**
     * 1.5K包大小，测试1：1场景
     *
     * 功能测试3
     *
     * 1.1个Producer
     * 2.1个主题A
     * 3.1个Broker集群，集群中两个节点
     * 4.1个Consumer，订阅主题A
     */
    MKTDATA_TEST_11(
            11,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.PUB_1_SUB_1,
            TestContents.MSG_DURABLE,
            TestContents.NON_SLOW_CONSUMER),

    /**
     * 功能测试4
     *
     * 1.1个Producer
     * 2.1个主题A
     * 3.1个Broker集群，集群中两个节点
     * 4.1个Consumer，订阅主题A
     */
    MKTDATA_TEST_12(
            12,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.PUB_1_SUB_1,
            TestContents.MSG_DURABLE,
            TestContents.NON_SLOW_CONSUMER),

    /**
     * 功能测试9
     * 消息大小 1500， 1个pub2000个sub，消息非持久化，含有1个慢消费者
     */
    MKTDATA_TEST_13(
            13,
            TestContents.MSG_SIZE_OF_1500,
            TestContents.PUB_1_SUB_2000,
            TestContents.MSG_NON_DURABLE,
            TestContents.SLOW_CONSUMER),

    ;

    public final int testCaseId;

    public int msgSize;
    public int msgSendRate;
    public final int pubsubCount;
    public boolean durable;
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
        return "TestCaseEnum{" +
                "testCaseId=" + testCaseId +
                ", msgSize=" + msgSize +
                ", msgSendRate=" + msgSendRate +
                ", pubsubCount=" + pubsubCount +
                ", durable=" + durable +
                ", slowConsumer=" + slowConsumer +
                "} ";
    }

    public static void main(String[] args) {
        TestCaseEnum testCaseEnum = TestCaseEnum.getById(1);
        testCaseEnum.msgSendRate = 5000;

        System.out.println(testCaseEnum.msgSendRate);
    }
}
