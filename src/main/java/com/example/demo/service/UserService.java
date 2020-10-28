package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户业务
 *
 * @author duchao
 */
@Slf4j
@Service
public class UserService {

    public static final Map<String, UserDetails> USER_MAP = new HashMap<>();
    public final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initUser(){
        USER_MAP.put("user1", User.withUsername("user1").password(passwordEncoder.encode("123456")).roles("USER").build());
        USER_MAP.put("user2", User.withUsername("user2").password(passwordEncoder.encode("123456")).roles("ADMIN").build());
        USER_MAP.put("user3", User.withUsername("user3").password(passwordEncoder.encode("123456")).roles("USER", "ADMIN").build());
    }

    /**
     * 获取系统用户
     *
     * @param username 用户名
     * @return
     */
    public UserDetails loadUserByUsername(String username) {
        if (USER_MAP.containsKey(username)) {
            return USER_MAP.get(username);
        }
        return null;
    }


}
