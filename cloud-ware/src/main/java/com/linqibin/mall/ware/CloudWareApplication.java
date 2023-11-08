package com.linqibin.mall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.linqibin.mall.ware.dao")
@EnableFeignClients(basePackages = {"com.linqibin.mall.ware.feign"})
public class CloudWareApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudWareApplication.class, args);
	}

}
