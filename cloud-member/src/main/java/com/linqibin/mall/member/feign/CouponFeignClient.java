package com.linqibin.mall.member.feign;

import com.linqibin.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author hugh&you
 * @since 2021/1/10 22:02
 */
@FeignClient("mall-coupon")  // 远程服务的application.name
public interface CouponFeignClient {

    /**
     * 远程调用接口, 必须写全路径才能识别
     * @return
     */
    @RequestMapping("/coupon/coupon/member/list")
    R memberCoupons();
}
