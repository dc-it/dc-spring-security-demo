package com.example.demo.common.config;

import cn.hutool.core.util.StrUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码编码器
 *
 * @author duchao
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new PasswordEncoder() {

            @Override
            public String encode(CharSequence rawPassword) {
                return "%" + rawPassword + "%";
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                if (StrUtil.isNotBlank(rawPassword) && StrUtil.isNotBlank(encodedPassword)) {
                    return this.encode(rawPassword).equals(encodedPassword);
                }
                return false;
            }

            public String decode(String encodedPassword) {
                return encodedPassword.substring(1, encodedPassword.length() - 1);
            }
        };
    }

//    /**
//     * 密码编码器
//     *
//     * @return
//     */
//    @Bean(name = "delegatingPasswordEncoder1")
//    public PasswordEncoder delegatingPasswordEncoder1() {
//        PasswordEncoder delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
//        return delegatingPasswordEncoder;
//    }

//    /**
//     * 密码编码器
//     *
//     * @return
//     */
//    @Bean(name = "delegatingPasswordEncoder")
//    public PasswordEncoder passwordEncoder() {
//        String idForEncode = "bcrypt";
//        Map idToPasswordEncoder = new HashMap<>(4);
//        idToPasswordEncoder.put(idForEncode, new BCryptPasswordEncoder());
//        idToPasswordEncoder.put("noop", NoOpPasswordEncoder.getInstance());
//        idToPasswordEncoder.put("pbkdf2", new Pbkdf2PasswordEncoder());
//        idToPasswordEncoder.put("scrypt", new SCryptPasswordEncoder());
//        PasswordEncoder delegatingPasswordEncoder = new DelegatingPasswordEncoder(idForEncode, idToPasswordEncoder);
//        return delegatingPasswordEncoder;
//    }
}
