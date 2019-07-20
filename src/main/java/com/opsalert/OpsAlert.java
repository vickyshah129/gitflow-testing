package com.opsalert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
public class OpsAlert {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(OpsAlert.class, args);
    }
}
