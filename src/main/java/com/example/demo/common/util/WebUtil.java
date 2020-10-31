package com.example.demo.common.util;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 请求工具类
 *
 * @author duchao
 */
@Component
public class WebUtil {

    @Value("${jwt.header}")
    private String header;

    @Value("${jwt.header-prefix}")
    private String headerPrefix;

    /**
     * 请求头中获取令牌
     *
     * @return
     */
    public String getTokenFromRequestHeader() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String authorizationHeader = request.getHeader(header);
        if (StrUtil.isBlank(authorizationHeader)) {
            return null;
        }
        return authorizationHeader.substring(headerPrefix.length() + 1);
    }

    /**
     * 响应头设置令牌
     *
     * @param token 令牌
     */
    public void setTokenToResponseHeader(final String token) {
        HttpServletResponse response = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
        response.setHeader(header, headerPrefix + " " + token);
    }

}
