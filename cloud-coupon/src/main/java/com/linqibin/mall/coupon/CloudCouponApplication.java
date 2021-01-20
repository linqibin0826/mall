package com.linqibin.mall.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.linqibin.mall.coupon.dao")
public class CloudCouponApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudCouponApplication.class, args);
	}

}
