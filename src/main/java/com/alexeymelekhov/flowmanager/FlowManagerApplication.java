package com.alexeymelekhov.flowmanager;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
@EnableFeignClients
public class FlowManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowManagerApplication.class, args);
    }

}
