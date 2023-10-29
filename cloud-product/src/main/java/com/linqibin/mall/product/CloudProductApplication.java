package com.linqibin.mall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("com.linqibin.mall.product.dao")
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients(basePackages = "com.linqibin.mall.product.feign")
public class CloudProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudProductApplication.class, args);
	}

}
