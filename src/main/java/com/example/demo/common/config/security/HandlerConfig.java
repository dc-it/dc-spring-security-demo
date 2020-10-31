package com.example.demo.common.config.security;

import cn.hutool.json.JSONUtil;
import com.example.demo.common.api.Result;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * 安全处理器配置
 * 走全局异常，这里对认证授权特定的异常没必要在此配置
 *
 * @author duchao
 */
@Configuration
public class HandlerConfig {

    /**
     * 认证处理器：未登录或登录过期
     *
     * @return
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authentication) -> {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().write(JSONUtil.toJsonStr(Result.failure("未登录或登录过期")));
            response.getWriter().flush();
        };
    }

//   全局异常加了该异常拦截
//    /**
//     * 访问拒绝处理器：无权访问
//     *
//     * @return
//     */
//    @Bean
//    public AccessDeniedHandler accessDeniedHandler() {
//        return (request, response, authentication) -> {
//            response.setCharacterEncoding("UTF-8");
//            response.setContentType("application/json");
//            response.getWriter().write(JSONUtil.toJsonStr(Result.failure("无权访问")));
//            response.getWriter().flush();
//        };
//    }

//    /**
//     * 认证成功处理器:开启登录使用，自定义登录则不使用
//     *
//     * @return
//     */
//    @Bean
//    public AuthenticationSuccessHandler authenticationSuccessHandler() {
//        return (request, response, authentication) -> {
//            response.setCharacterEncoding("UTF-8");
//            response.setContentType("application/json");
//            response.getWriter().write(JSONUtil.toJsonStr(Result.success("登录成功")));
//            response.getWriter().flush();
//        };
//    }
}
