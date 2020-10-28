package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 密码编码器测试
 *
 * @author duchao
 */
@Slf4j
@SpringBootTest
class PasswordEncoderTests {

	private final static String RAW_PASSWORD="123456";

	@Autowired
	@Qualifier("delegatingPasswordEncoder")
	private PasswordEncoder passwordEncoder;

	/**
	 * 授权编码器DelegatingPasswordEncoder-明文加密、密码匹配
	 */
	@Test
	void testDelegatingPasswordEncoder() {
		if (passwordEncoder instanceof DelegatingPasswordEncoder) {

			DelegatingPasswordEncoder delegatingPasswordEncoder = (DelegatingPasswordEncoder) passwordEncoder;

			final String rawPassword1 = "duchao123";
			String encodedPassword = delegatingPasswordEncoder.encode(rawPassword1);
			log.info("\n明文密码：{}\n明文加密：{}", rawPassword1, encodedPassword);

			final String rawPassword2 = "duchao1234";
			boolean rawPassword1Matches = delegatingPasswordEncoder.matches(rawPassword1, encodedPassword);
			boolean rawPassword2Matches = delegatingPasswordEncoder.matches(rawPassword2, encodedPassword);
			log.info("\n明文{}和密文{}是否匹配：{}", rawPassword1, encodedPassword, rawPassword1Matches);
			log.info("\n明文{}和密文{}是否匹配：{}", rawPassword2, encodedPassword, rawPassword2Matches);

			boolean upgradeEncoding = delegatingPasswordEncoder.upgradeEncoding(rawPassword1);
			log.info("\n是否需要升级：{}", upgradeEncoding);
		}
	}

	/**
	 * BCryptPasswordEncoder-明文加密、密码匹配
	 */
	@Test
	void testBCryptPasswordEncoder() {
		// Create an encoder with strength 16
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);
		String encodedPassword = encoder.encode(RAW_PASSWORD);
		log.info("\n明文{}加密：{}",RAW_PASSWORD,encodedPassword);
		assertTrue(encoder.matches(RAW_PASSWORD, encodedPassword));
	}

	/**
	 * 测试使用注入IOC容器的密码编码器
	 */
	@Test
	void testWithAutowiredPasswordEncoder(){
		UserDetails userDetails = User.builder()
				.username("user")
				.password(passwordEncoder.encode("123456"))
				.roles("USER")
				.build();
		log.info("\n{}",userDetails.getPassword());
	}

	/**
	 * 测试使用默认密码编码器
	 */
	@Test
	void testWithDefaultPasswordEncoder(){
		User.UserBuilder users = User.withDefaultPasswordEncoder();
		UserDetails userDetails = users
				.username("user")
				.password("123456")
				.roles("USER")
				.build();
		log.info("\n{}",userDetails.getPassword());
	}

	/**
	 * 测试使用自定义密码编码器
	 */
	@Test
	void testWithCustomPasswordEncoder(){
		User.UserBuilder users = User.builder().passwordEncoder(rawPassword->new StringBuilder().append("%").append(rawPassword).append("%").toString());
		UserDetails userDetails = users
				.username("user")
				.password("123456")
				.roles("USER")
				.build();
		log.info("\n{}",userDetails.getPassword());
	}
}
