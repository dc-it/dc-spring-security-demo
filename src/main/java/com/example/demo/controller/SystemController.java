package com.example.demo.controller;

import com.example.demo.common.api.Result;
import com.example.demo.dto.LoginFormDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 系统前端控制器
 *
 * @author duchao
 */
@RestController
@RequestMapping("/system")
public class SystemController {

    @PostMapping("/login")
    public Result<String> login(@RequestBody @Validated LoginFormDto loginFormDto) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginFormDto.getUsername(), loginFormDto.getPassword());
        SecurityContextHolder.getContext().setAuthentication(token);
        return Result.success("登录成功");
    }

    @GetMapping("/logout")
    public Result<String> logout() {
        SecurityContextHolder.clearContext();
        return Result.success("退出成功");
    }
}
