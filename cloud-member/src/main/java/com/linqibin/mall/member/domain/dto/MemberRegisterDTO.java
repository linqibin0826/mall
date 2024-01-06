package com.linqibin.mall.member.domain.dto;

import lombok.Data;

/**
 * 新用户注册数据传输对象
 * @author linqibin
 * @date   2023/12/29 23:52
 * @email  1214219989@qq.com
 */
@Data
public class MemberRegisterDTO {

    private String username;
    private String password;
    private String email;
}
