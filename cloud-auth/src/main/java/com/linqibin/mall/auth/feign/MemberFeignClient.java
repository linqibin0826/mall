package com.linqibin.mall.auth.feign;

import com.linqibin.common.utils.R;
import com.linqibin.mall.auth.domain.vo.OauthLoginVo;
import com.linqibin.mall.auth.domain.vo.RegisterVo;
import com.linqibin.mall.auth.domain.vo.UserLoginVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 用户信息feign客户端
 *
 * @author linqibin
 * @date 2023/12/30 00:20
 * @email 1214219989@qq.com
 */
@FeignClient("cloud-member")
public interface MemberFeignClient {

    /**
     * 注册
     *
     * @param registerVo 注册信息
     * @return code:0 成功
     */
    @PostMapping("/member/member/register")
    R register(@RequestBody RegisterVo registerVo);

    /**
     * 登录
     *
     * @param loginVo 登录信息
     * @return code:0 成功
     */
    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVo loginVo);

    /**
     * 社交账号登陆
     * @param oauthLoginVo 社交实体
     */
    @PostMapping("/member/member/oauth/login")
    R login(@RequestBody OauthLoginVo oauthLoginVo);
}
