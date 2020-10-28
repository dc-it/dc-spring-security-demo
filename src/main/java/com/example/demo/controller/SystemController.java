package com.example.demo.controller;

import com.example.demo.common.api.Result;
import com.example.demo.dto.LoginFormDto;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * 系统前端控制器
 *
 * @author duchao
 */
@RestController
@RequestMapping("/system")
public class SystemController {

    private final UserService userService;
    public final PasswordEncoder passwordEncoder;

    @Autowired
    public SystemController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody @Validated LoginFormDto loginFormDto, HttpServletResponse response) {

        UserDetails userDetails = userService.loadUserByUsername(loginFormDto.getUsername());
        if (userDetails == null) {
            throw new UsernameNotFoundException("账户不存在");
        }
        if(!passwordEncoder.matches(loginFormDto.getPassword(),userDetails.getPassword())){
            throw new BadCredentialsException("密码不正确");
        }

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginFormDto.getUsername(), loginFormDto.getPassword());
        SecurityContextHolder.getContext().setAuthentication(token);
        response.setHeader("token", token.getPrincipal().toString());
        return Result.success("登录成功");
    }

    @GetMapping("/logout")
    public Result<String> logout() {
        SecurityContextHolder.clearContext();
        return Result.success("退出成功");
    }
}
