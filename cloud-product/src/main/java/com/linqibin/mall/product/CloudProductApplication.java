package com.linqibin.mall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@MapperScan("com.linqibin.mall.product.dao")
@EnableDiscoveryClient
@SpringBootApplication
public class CloudProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudProductApplication.class, args);
	}

}
