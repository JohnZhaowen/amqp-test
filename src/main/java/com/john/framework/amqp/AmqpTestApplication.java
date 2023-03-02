package com.john.framework.amqp;

import com.john.framework.amqp.collectors.TestCaseRunner;
import com.john.framework.amqp.testcase.TestCaseEnum;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.Objects;

@SpringBootApplication
public class AmqpTestApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AmqpTestApplication.class, args);


        TestCaseRunner runner = context.getBean(TestCaseRunner.class);

        int pubsubCount = Integer.parseInt(Objects.requireNonNull(context.getEnvironment().getProperty("pubsubCount")));
        List<TestCaseEnum> cases = TestCaseEnum.getCasesByPubsubCount(pubsubCount);

        runner.runTestCases(cases);
    }

}
