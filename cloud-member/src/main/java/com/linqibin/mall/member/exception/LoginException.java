package com.linqibin.mall.member.exception;

/**
 * 用户不存在异常
 * @author linqibin
 * @date   2024/1/2 20:22
 * @email  1214219989@qq.com
 */
public class LoginException extends RuntimeException {

    public LoginException() {
        super("用户名或密码错误");
    }
}
