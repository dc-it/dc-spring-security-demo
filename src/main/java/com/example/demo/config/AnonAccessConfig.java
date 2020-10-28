package com.example.demo.config;

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

    private List<String> anonAccessList;
}
