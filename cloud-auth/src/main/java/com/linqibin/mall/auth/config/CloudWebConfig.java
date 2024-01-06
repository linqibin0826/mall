package com.linqibin.mall.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * @author linqibin
 * @date   2024/1/4 19:25
 * @email  1214219989@qq.com
 */
@Configuration
public class CloudWebConfig implements WebMvcConfigurer {


    /**
     * 只有映射的空方法，可以修改为视图配置
     * <code>
     *     @GetMapping("/login.html")
     *     public String login() {
     *         return "login";
     *     }
     * </code>
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/reg.html").setViewName("reg");
    }
}



