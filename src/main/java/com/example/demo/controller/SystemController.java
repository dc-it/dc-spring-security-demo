package com.example.demo.controller;

import com.example.demo.common.api.Result;
import com.example.demo.common.util.JwtUtil;
import com.example.demo.dto.LoginFormDto;
import com.example.demo.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    public SystemController(SystemService systemService, JwtUtil jwtUtil) {
        this.systemService = systemService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody @Validated LoginFormDto loginFormDto, HttpServletResponse response) {
        systemService.login(loginFormDto);
        response.setHeader(header, headerPrefix + " " + jwtUtil.createToken(Map.of("account", loginFormDto.getUsername())));
        return Result.success("登录成功");
    }

    @GetMapping("/logout")
    public Result<String> logout() {
        SecurityContextHolder.clearContext();
        return Result.success("退出成功");
    }
}
