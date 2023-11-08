package com.linqibin.mall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class CloudSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudSearchApplication.class);
    }
}
