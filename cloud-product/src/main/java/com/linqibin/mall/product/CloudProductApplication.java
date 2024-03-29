package com.linqibin.mall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients(basePackages = "com.linqibin.mall.product.feign")
public class CloudProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudProductApplication.class, args);
	}

}
