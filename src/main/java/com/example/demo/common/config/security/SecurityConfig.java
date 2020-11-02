package com.example.demo.common.config.security;

import cn.hutool.core.util.ArrayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.util.StringUtils;

/**
 * 安全配置
 *
 * @author duchao
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final AnonAccessConfig anonAccessConfig;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final TokenFilter tokenFilter;
    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(PasswordEncoder passwordEncoder,
                          AnonAccessConfig anonAccessConfig,
                          AuthenticationEntryPoint authenticationEntryPoint,
                          TokenFilter tokenFilter,
                          @Qualifier("securityUserDetailsService") UserDetailsService userDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.anonAccessConfig = anonAccessConfig;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.tokenFilter = tokenFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * 认证管理器配置
     *
     * @param auth 认证管理构建器
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .passwordEncoder(passwordEncoder)
//                .withUser("user").password(passwordEncoder.encode("123456")).roles("USER");
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    /**
     * http请求安全配置
     * <p>
     * http.authorizeRequests().anyRequest().authenticated().and().formLogin().and().httpBasic();
     *
     * @param http httt请求安全类
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(ArrayUtil.toArray(anonAccessConfig.getAnonAccessList(), String.class)).permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //会话配置
                .and().csrf().disable() //非浏览器客服端的服务要禁用跨站请求伪装，csrf走cookie(a service that is used by non-browser clients, disable CSRF protection.)
                .cors().disable() //跨域
                .headers() //请求头配置，指导浏览器行为配置
                .cacheControl() //缓存控制
                .and().contentTypeOptions() //浏览器内容类型嗅探
                .and().httpStrictTransportSecurity() //浏览器请求https安全传输
                .and().frameOptions() //frame框架嵌入
                .and().xssProtection() //跨站脚本攻击
                .and().referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.ORIGIN_WHEN_CROSS_ORIGIN) // 访问来源
        ;
    }

//    @Bean
//    @Override
//    protected UserDetailsService userDetailsService() {
//        return username -> {
//            UserDetails userDetails = userService.loadUserByUsername(username);
//            if(userDetails==null){
//                throw new UsernameNotFoundException("账户不存在");
//            }
//            return userDetails;
//        };
//    }
}
