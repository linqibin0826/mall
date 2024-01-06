package com.linqibin.common.constant;

/**
 * 购物车相关常量信息
 * @author linqibin
 * @date   2024/1/6 10:07
 * @email  1214219989@qq.com
 */
public interface CartConstants {

    /**
     * 临时用户cookie名称
     */
    String TEMP_USER_COOKIE_NAME = "user-key";

    Integer TEMP_USER_COOKIE_TIMEOUT = 60 * 60 * 24 * 30;

    /**
     * 用户购物车前缀
     */
    String CART_PREFIX = "cart:";
}
