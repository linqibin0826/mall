package com.linqibin.mall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.linqibin.mall.ware.dao")
public class CloudWareApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudWareApplication.class, args);
	}

}
