package com.john.framework.amqp.functest;

import com.john.framework.amqp.amqp.AmqpMessage;
import com.john.framework.amqp.testcase.TestCaseEnum;
import org.springframework.core.env.Environment;

/**
 * @Description 用例9
 * @Author zqg
 * @Date 2023/5/17
 */
public class TestCase11ConsumerMsgListener extends AbstractFuncConsumerMsgListener{

    public TestCase11ConsumerMsgListener(TestCaseEnum testCaseEnum) {
        super(testCaseEnum);
    }

    @Override
    protected void onMsgExt(AmqpMessage message) {

    }
}
