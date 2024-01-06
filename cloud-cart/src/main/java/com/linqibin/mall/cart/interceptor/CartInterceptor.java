package com.linqibin.mall.cart.interceptor;

import com.linqibin.common.constant.AuthConstant;
import com.linqibin.common.constant.CartConstants;
import com.linqibin.common.to.MemberRespVo;
import com.linqibin.mall.cart.domain.to.UserInfoTo;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Objects;
import java.util.UUID;

/**
 * 分发user-key拦截器
 * @author linqibin
 * @date   2024/1/6 10:03
 * @email  1214219989@qq.com
 */
public class CartInterceptor implements HandlerInterceptor {

    /**
     * 当前用户信息
     */
    public static ThreadLocal<UserInfoTo> CURRENT_USER_INFO = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfoTo data = new UserInfoTo();
        HttpSession session = request.getSession();
        MemberRespVo loginUser = (MemberRespVo) session.getAttribute(AuthConstant.LOGIN_USER);
        if (loginUser != null) {
            // 用户已登录状态
            data.setUserId(loginUser.getId());
        } else {
            // 从cookie中获取user-key
            Cookie[] cookies = request.getCookies();
            if (Objects.nonNull(cookies)) {
                for (Cookie cookie : cookies) {
                    if (CartConstants.TEMP_USER_COOKIE_NAME.equals(cookie.getName())) {
                        String userKey = cookie.getValue();
                        data.setUserKey(userKey);
                        data.setTempUser(true);
                    }
                }
            }
        }
        CURRENT_USER_INFO.set(data);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTo userInfoTo = CURRENT_USER_INFO.get();
        if (!userInfoTo.isTempUser()) {
            // 添加cookie
            Cookie cookie = new Cookie(CartConstants.TEMP_USER_COOKIE_NAME, UUID.randomUUID().toString());
            cookie.setDomain("gulimall.com");
            cookie.setMaxAge(CartConstants.TEMP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }
    }
}
