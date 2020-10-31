package com.example.demo.common.config.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 匿名访问配置
 *
 * @author duchao
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.security")
public class AnonAccessConfig {

    /**
     * 匿名访问权限列表
     */
    private List<String> anonAccessList;
}
