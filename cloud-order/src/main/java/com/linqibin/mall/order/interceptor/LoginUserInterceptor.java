package com.linqibin.mall.order.interceptor;

import com.linqibin.common.constant.AuthConstant;
import com.linqibin.common.to.MemberRespVo;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * 订单系统需要登录拦截器
 * @author linqibin
 * @date   2024/1/7 15:24
 * @email  1214219989@qq.com
 */
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberRespVo> LOGIN_USER = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        MemberRespVo respVo = (MemberRespVo) session.getAttribute(AuthConstant.LOGIN_USER);
        if (respVo != null) {
            LOGIN_USER.set(respVo);
            return true;
        } else {
            response.sendRedirect("http://auth.gulimall.com/login.html");
            return false;
        }
    }
}
