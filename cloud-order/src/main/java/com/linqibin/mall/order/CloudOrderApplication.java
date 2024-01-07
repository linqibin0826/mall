package com.linqibin.mall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 *
 * @author linqibin
 * @date   2024/1/7 00:49
 * @email  1214219989@qq.com
 */
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.linqibin.mall.order.dao")
@EnableRedisHttpSession
public class CloudOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudOrderApplication.class, args);
	}

}
