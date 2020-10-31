package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 密码编码器测试
 *
 * @author duchao
 */
@Slf4j
@SpringBootTest
class JasyptTests {

    @Autowired
    private StringEncryptor stringEncryptor;

    @Test
    void testEncrypt() {
        String rawPassword="abcdefg";
        log.info("原始密码：{},加密后：{}",rawPassword,stringEncryptor.encrypt(rawPassword));
    }
}
