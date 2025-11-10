package com.claims;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ClaimsFraudDetectionApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClaimsFraudDetectionApplication.class, args);
    }
}