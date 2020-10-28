package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * 密码编码器
 *
 * @author duchao
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * 密码编码器
     *
     * @return
     */
    //@Bean(name = "delegatingPasswordEncoder1")
    public PasswordEncoder delegatingPasswordEncoder1() {
        PasswordEncoder delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return delegatingPasswordEncoder;
    }

    /**
     * 密码编码器
     *
     * @return
     */
    @Bean(name = "delegatingPasswordEncoder")
    public PasswordEncoder delegatingPasswordEncoder2() {
        String idForEncode = "bcrypt";
        Map idToPasswordEncoder = new HashMap<>(4);
        idToPasswordEncoder.put(idForEncode, new BCryptPasswordEncoder());
        idToPasswordEncoder.put("noop", NoOpPasswordEncoder.getInstance());
        idToPasswordEncoder.put("pbkdf2", new Pbkdf2PasswordEncoder());
        idToPasswordEncoder.put("scrypt", new SCryptPasswordEncoder());
        PasswordEncoder delegatingPasswordEncoder = new DelegatingPasswordEncoder(idForEncode, idToPasswordEncoder);
        return delegatingPasswordEncoder;
    }
}
