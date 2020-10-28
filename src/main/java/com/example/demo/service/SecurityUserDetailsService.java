package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * 认证需要用户加载
 *
 * @author duchao
 */
@Component("securityUserDetailsService")
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public SecurityUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = userService.loadUserByUsername(username);
        if(userDetails==null){
            throw new UsernameNotFoundException("账户不存在");
        }
        return userDetails;
    }
}
