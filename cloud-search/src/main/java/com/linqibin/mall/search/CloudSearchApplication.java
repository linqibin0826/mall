package com.linqibin.mall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableFeignClients({"com.linqibin.mall.search.feign"})
@EnableRedisHttpSession
public class CloudSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudSearchApplication.class);
    }
}
