package com.linqibin.mall.search.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * Spring Session核心原理
 * 1） @EnableRedisHttpSession导入RedisHttpSessionConfiguration配置
 *      1. 给容器添加了一个组件
 *          SessionRepository -> [RedisOperationsSessionRepository] -> redis操作session。session的增删改查
 *      2. SessionRepositoryFilter -> Filter: session存储过滤器， 每个请求过来都必须经过该filter
 *          1️⃣. 创建的时候，就自动从容器中获取到SessionRepository
 *          2️⃣. 原始的request response都被包装。SessionRepositoryRequestWrapper
 *          3️⃣. 以后获取Session  request.getSession()  都是使用包装过的request对象
 *          4️⃣. wrappedRequest.getSession(); -> SessionRepository中获取的。
 *  装饰者模式。
 *  自动延期，redis中的数据也是有过期时间的。
 *
 * @author linqibin
 * @date   2024/1/4 20:23
 * @email  1214219989@qq.com
 */
@Configuration
public class SessionConfig {

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        // Session域名放大到父域名
        cookieSerializer.setDomainName("gulimall.com");
        cookieSerializer.setCookieName("MY-SESSION");
        return cookieSerializer;
    }

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

}
