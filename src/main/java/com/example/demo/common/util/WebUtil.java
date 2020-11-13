package com.example.demo.common.util;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
        HttpServletRequest request = getRequest();
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
        HttpServletResponse response = getResponse();
        response.setHeader(header, headerPrefix + " " + token);
    }

    /**
     * 获取客户端ip
     *
     * @return
     */
    public String getClientIp() {

        HttpServletRequest request = getRequest();
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inet.getHostAddress();
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) { //"***.***.***.***".length() = 15
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

    /**
     * 请求
     *
     * @return
     */
    private HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }

    /**
     * 响应
     *
     * @return
     */
    public HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
    }

}
