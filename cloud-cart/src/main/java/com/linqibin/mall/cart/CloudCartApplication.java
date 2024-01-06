package com.linqibin.mall.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 1. 整合spring-session
 * 2. 浏览器有一个cookie：user-key，表示用户身份。一个月后过期。每次访问都会带上这个cookie。
 * 3. 使用拦截器来处理，判断用户是否登陆，看是临时用户还是登陆用户。并封装传递给controller
 * 4. 将UserInfoVo放到ThreadLocal中
 * 5. 配置拦截器拦截所有请求, 让浏览器保存cookie， user-key。指定过期时间
 * @author linqibin
 * @date   2024/1/5 22:52
 * @email  1214219989@qq.com
 */
@EnableRedisHttpSession
@SpringBootApplication
@EnableFeignClients
public class CloudCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudCartApplication.class, args);
    }
}
