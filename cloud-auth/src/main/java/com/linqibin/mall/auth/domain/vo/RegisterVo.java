package com.linqibin.mall.auth.domain.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 注册页面实体类
 */
@Data
public class RegisterVo {

    @NotBlank(message = "用户名不可为空")
    @Length(min = 6, max = 12, message = "用户名应在6-12个字符")
    private String username;

    @NotBlank(message = "密码不可为空")
    @Length(min = 8, max = 16, message = "密码应在8-16个字符")
    private String password;

    @NotBlank(message = "邮箱不可为空")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "验证码不可为空")
    @Length(min = 6, max = 6, message = "验证码应为6位")
    private String code;
}
