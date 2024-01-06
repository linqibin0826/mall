package com.linqibin.mall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.linqibin.common.utils.R;
import com.linqibin.common.to.MemberRespVo;
import com.linqibin.mall.auth.domain.vo.OauthLoginVo;
import com.linqibin.mall.auth.feign.MemberFeignClient;
import com.xkcoding.justauth.AuthRequestFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.linqibin.common.constant.AuthConstant.LOGIN_USER;

/**
 * Oauth2.0 第三方账号登陆
 * @author linqibin
 * @date   2024/1/2 21:59
 * @email  1214219989@qq.com
 */
@AllArgsConstructor
@Controller
@RequestMapping("/oauth")
@Slf4j
public class Oauth2Controller {

    private final MemberFeignClient memberFeignClient;

    private final AuthRequestFactory factory;

    @GetMapping("/login/{type}")
    public void login(@PathVariable String type, HttpServletResponse response) throws IOException {
        AuthRequest authRequest = factory.get(type);
        response.sendRedirect(authRequest.authorize(AuthStateUtils.createState()));
    }

    @RequestMapping("/{type}/callback")
    public String login(@PathVariable("type") String type, AuthCallback callback, HttpSession session) {
        AuthRequest authRequest = factory.get(type);
        AuthResponse<AuthUser> response = authRequest.login(callback);
        // 判断此用户是否是已注册用户。
        OauthLoginVo vo = new OauthLoginVo();
        vo.setType(type.toUpperCase());
        AuthUser data = response.getData();
        vo.setAvatar(data.getAvatar());
        vo.setUid(data.getUuid());
        vo.setAccessToken(data.getToken().getAccessToken());
        vo.setExpiredDate((long) data.getToken().getExpireIn());
        vo.setNickname(data.getNickname());
        R login = memberFeignClient.login(vo);
        if (login.getCode() == 0) {
            MemberRespVo respVo = login.getData(new TypeReference<MemberRespVo>() {});
            session.setAttribute(LOGIN_USER, respVo);
            log.info("login success: {}", respVo);
        } else {
            return "redirect:http://auth.gulimall.com/login.html";
        }
        return "redirect:http://gulimall.com";
    }
}
