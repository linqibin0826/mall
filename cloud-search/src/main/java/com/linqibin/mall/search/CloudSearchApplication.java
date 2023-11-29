package com.linqibin.mall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableFeignClients({"com.linqibin.mall.search.feign"})
public class CloudSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudSearchApplication.class);
    }
}
