package com.linqibin.mall.member.exception;

/**
 * 用户名已存在异常
 * @author linqibin
 * @date   2023/12/30 01:14
 * @email  1214219989@qq.com
 */
public class UsernameExistException extends RuntimeException {

    public UsernameExistException() {
        super("用户名已存在");
    }
}
