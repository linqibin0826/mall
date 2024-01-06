package com.linqibin.mall.auth.feign;


import com.linqibin.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("cloud-third-party")
public interface ThirdPartFeignClient {

    @PostMapping("/mail/sendCode")
    R sendCode(@RequestParam("mail") String mail, @RequestParam("code") String code);
}
