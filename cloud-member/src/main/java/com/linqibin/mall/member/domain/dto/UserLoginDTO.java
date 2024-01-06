package com.linqibin.mall.member.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登陆DTO
 *
 * @author linqibin
 * @date   2024/1/2 20:21
 * @email  1214219989@qq.com
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {

    private String username;
    private String password;
}
