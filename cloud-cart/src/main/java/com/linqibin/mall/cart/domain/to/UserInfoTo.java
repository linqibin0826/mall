package com.linqibin.mall.cart.domain.to;

import lombok.Data;

/**
 * 用户信息传输对象， 用户在不同层传输登录用户或临时用户信息
 * @author linqibin
 * @date   2024/1/6 10:17
 * @email  1214219989@qq.com
 */
@Data
public class UserInfoTo {

    private Long userId;
    /**
     * 临时用户信息
     */
    private String userKey;

    private boolean tempUser;
}
