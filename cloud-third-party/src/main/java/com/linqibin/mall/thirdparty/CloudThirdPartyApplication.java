package com.linqibin.mall.thirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class CloudThirdPartyApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudThirdPartyApplication.class, args);
	}

}
