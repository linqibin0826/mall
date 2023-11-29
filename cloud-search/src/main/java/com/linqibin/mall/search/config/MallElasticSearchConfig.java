package com.linqibin.mall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>Title: ElasticSearchConfig</p>
 * Description：1. 导入依赖
 * 2. 注入Bean对象
 * API : https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.4/java-rest-high-supported-apis.html
 * date：2020/6/8 14:50
 */
@Configuration
public class MallElasticSearchConfig {

	@Value("${ipAddr}")
	private String host;

	public static final RequestOptions COMMON_OPTIONS;

	/**
	 * 参考官方文档，发送每个ES请求都应加上这个东西
	 */
	static {
		RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
//		builder.addHeader("Authorization", "Bearer " + TOKEN);
//		builder.setHttpAsyncResponseConsumerFactory(
//				new HttpAsyncResponseConsumerFactory
//						.HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
		COMMON_OPTIONS = builder.build();
	}

	/**
	 * final String hostname, final int port, final String scheme
	 * @return
	 */
	@Bean
	public RestHighLevelClient esRestClient() {
        return new RestHighLevelClient(
				RestClient.builder(new HttpHost(host, 9200, "http")));
	}
}
