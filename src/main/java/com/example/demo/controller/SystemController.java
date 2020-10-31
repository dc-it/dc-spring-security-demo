package com.example.demo.controller;

import cn.hutool.core.util.StrUtil;
import com.example.demo.common.api.Result;
import com.example.demo.common.util.JwtUtil;
import com.example.demo.common.util.RedisUtil;
import com.example.demo.dto.LoginFormDto;
import com.example.demo.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${jwt.header}")
    private String header;
    @Value("${jwt.header-prefix}")
    private String headerPrefix;
    private final SystemService systemService;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Autowired
    public SystemController(SystemService systemService, JwtUtil jwtUtil, RedisUtil redisUtil) {
        this.systemService = systemService;
        this.jwtUtil = jwtUtil;
        this.redisUtil = redisUtil;
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody @Validated LoginFormDto loginFormDto, HttpServletResponse response) {
        systemService.login(loginFormDto);
        response.setHeader(header, headerPrefix + " " + jwtUtil.createToken(Map.of("account", loginFormDto.getUsername())));
        return Result.success("登录成功");
    }

    @GetMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(header);
        if (StrUtil.isBlank(authorizationHeader)) {
            throw new AccountExpiredException("未登录或登录过期");
        }
        String token = authorizationHeader.substring(headerPrefix.length() + 1);
        if (StrUtil.isBlank(token)) {
            throw new AccountExpiredException("未登录或登录过期");
        }
        redisUtil.delete(token);
        return Result.success("退出成功");
    }
}
