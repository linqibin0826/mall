package com.linqibin.mall.auth.domain.vo;

import lombok.Data;

/**
 * Oauth登陆用户信息
 * @author linqibin
 * @date   2024/1/3 19:34
 * @email  1214219989@qq.com
 */
@Data
public class OauthLoginVo {

    private String accessToken;

    private String uid;

    private Long expiredDate;

    /**
     * Oauth登陆类型
     */
    private String type;

    /**
     * 头像地址
     */
    private String avatar;


    private String gender;

    private String nickname;
}
