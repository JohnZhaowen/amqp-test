package com.john.framework.amqp;

import com.john.framework.amqp.amqp.IPubSub;
import com.john.framework.amqp.amqp.MyPub;
import com.john.framework.amqp.amqp.MyPubSub;
import com.john.framework.amqp.amqp.MySub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

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
        pressAnyKey("Press any key to stop..");
    }


    private static void pressAnyKey(String hint) {
        System.out.println(hint);
        scanner.nextLine();
    }

    @Bean
    public IPubSub pubSub(){
        if("sub".equalsIgnoreCase(environment.getProperty("appType"))){
            MySub mySub = new MySub();
            mySub.init();
            return mySub;
        }else if("pub".equalsIgnoreCase(environment.getProperty("appType"))){
            MyPub myPub = new MyPub();
            myPub.init();
            return myPub;
        }else if("pubsub".equalsIgnoreCase(environment.getProperty("appType"))){
            MyPubSub myPubSub = new MyPubSub();
            myPubSub.init();
            return myPubSub;
        }
        return null;
    }
}
