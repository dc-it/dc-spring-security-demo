package com.example.demo.service;

import com.example.demo.dto.LoginFormDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 系统业务
 *
 * @author duchao
 */
@Service
public class SystemService {

    private final UserService userService;
    public final PasswordEncoder passwordEncoder;

    @Autowired
    public SystemService(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 登录
     *
     * @param loginFormDto 登录参数
     * @return 令牌
     */
    public void login(LoginFormDto loginFormDto) {
        UserDetails userDetails = userService.loadUserByUsername(loginFormDto.getUsername());
        if (userDetails == null) {
            throw new UsernameNotFoundException("账户不存在");
        }
        if (!passwordEncoder.matches(loginFormDto.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("密码不正确");
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginFormDto.getUsername(), loginFormDto.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    public void logout(){

    }
}
