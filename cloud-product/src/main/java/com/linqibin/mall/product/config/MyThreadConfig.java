package com.linqibin.mall.product.config;

import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

/**
 * <p>Title: MyThreadConfig</p>
 * Description：配置线程池
 * date：2023/12/10 22:15
 */
// 开启这个属性配置
@EnableConfigurationProperties(ThreadPoolConfigProperties.class)
@Configuration
public class MyThreadConfig {

	@Bean
	public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties threadPoolConfigProperties){
		return new ThreadPoolExecutor(threadPoolConfigProperties.getCoreSize(), threadPoolConfigProperties.getMaxSize(),
				threadPoolConfigProperties.getKeepAliveTime(), TimeUnit.SECONDS, new LinkedBlockingQueue<>(10000),
				Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
	}
}
