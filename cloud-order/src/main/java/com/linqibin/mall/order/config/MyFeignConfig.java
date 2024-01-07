package com.linqibin.mall.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * feign远程调用时丢失请求头问题
 * 异步情况下还会丢失请求上下文
 *
 * @author linqibin
 * @date 2024/1/7 21:19
 * @email 1214219989@qq.com
 */
@Configuration
public class MyFeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // RequestContextHolder.setRequestAttributes() 切换线程时， 应该把上下文也带上。
        HttpServletRequest request = requestAttributes.getRequest();
        if (request != null) {
            String cookie = request.getHeader("Cookie");
            return template -> {
                template.header("Cookie", cookie);
            };
        }
        return null;
    }
}
