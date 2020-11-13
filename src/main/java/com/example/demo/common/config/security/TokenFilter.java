package com.example.demo.common.config.security;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.example.demo.common.util.JwtUtil;
import com.example.demo.common.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * token处理过滤器
 *
 * @author duchao
 */
@Slf4j
@Component
public class TokenFilter extends OncePerRequestFilter {

    private final AnonAccessConfig anonAccessConfig;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final WebUtil webUtil;
    private final Environment environment;

    @Autowired
    public TokenFilter(AnonAccessConfig anonAccessConfig,
                       @Qualifier("securityUserDetailsService") UserDetailsService userDetailsService,
                       JwtUtil jwtUtil,
                       WebUtil webUtil,
                       Environment environment) {
        this.anonAccessConfig = anonAccessConfig;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.webUtil = webUtil;
        this.environment = environment;
    }

    /**
     * @param request     请求
     * @param response    响应
     * @param filterChain 过滤器链
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("***********客户{}消费服务{}**********",webUtil.getClientIp(),environment.getProperty("spring.application.name"));

        //请求处理
        if (CollectionUtil.isEmpty(anonAccessConfig.getAnonAccessList()) || !anonAccessConfig.getAnonAccessList().contains(request.getRequestURI())) {

            String token = webUtil.getTokenFromRequestHeader();
            if (StrUtil.isBlank(token) || !jwtUtil.validToken(token)) {
                throw new AccountExpiredException("未登录或登录过期");
            }

            String account = jwtUtil.getAccountFromToken(token);

            if (StrUtil.isNotBlank(account) && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.info("用户{}通过认证，持有令牌：{}", account, token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(account);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            webUtil.setTokenToResponseHeader(jwtUtil.refreshTokenIfCanRefresh(token));
        }

        filterChain.doFilter(request, response);

        //响应处理
    }

}
