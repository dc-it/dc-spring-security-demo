package com.example.demo.controller;

import cn.hutool.core.util.StrUtil;
import com.example.demo.common.api.Result;
import com.example.demo.common.util.JwtUtil;
import com.example.demo.common.util.WebUtil;
import com.example.demo.dto.LoginFormDto;
import com.example.demo.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 系统前端控制器
 *
 * @author duchao
 */
@RestController
@RequestMapping("/system")
public class SystemController {

    private final SystemService systemService;
    private final JwtUtil jwtUtil;
    private final WebUtil webUtil;

    @Autowired
    public SystemController(SystemService systemService, JwtUtil jwtUtil, WebUtil webUtil) {
        this.systemService = systemService;
        this.jwtUtil = jwtUtil;
        this.webUtil = webUtil;
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody @Validated LoginFormDto loginFormDto, HttpServletResponse response) {
        systemService.login(loginFormDto);
        webUtil.setTokenToResponseHeader(jwtUtil.createToken(Map.of("account", loginFormDto.getUsername())));
        return Result.success("登录成功");
    }

    @GetMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        String token = webUtil.getTokenFromRequestHeader();
        if (StrUtil.isBlank(token)) {
            throw new AccountExpiredException("未登录或登录过期");
        }
        jwtUtil.removeFromWhiteList(token);
        return Result.success("退出成功");
    }
}
