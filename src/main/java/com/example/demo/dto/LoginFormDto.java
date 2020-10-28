package com.example.demo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录
 *
 * @author duchao
 */
@Data
public class LoginFormDto {

    @NotBlank(message = "账户不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
