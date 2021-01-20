package com.linqibin.mall.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@MapperScan("com.linqibin.mall.member.dao")
@SpringBootApplication
@EnableFeignClients(basePackages = "com.linqibin.mall.member.feign")
public class CloudMemberApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudMemberApplication.class, args);
	}

}
