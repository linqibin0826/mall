package com.linqibin.mall.auth.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登陆vo
 * @author linqibin
 * @date   2024/1/2 20:11
 * @email  1214219989@qq.com
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginVo {
   private String username;
   private String password;
}

