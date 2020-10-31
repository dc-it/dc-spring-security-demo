package com.example.demo.common.util;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.example.demo.common.exception.BaseException;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

/**
 * jwt工具类
 * <p>
 * 注意：引入redis缓存登录令牌白名单，解决jwt退出问题。
 * 但是，这违背了JWT这种Session会话机制不用把会话存服务器端的策略。
 *
 * @author duchao
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.expiration-time}")
    private long expirationTime;

    @Value("${jwt.refresh-time}")
    private long refreshTime;

    @Value("${jwt.type}")
    private Integer type;

    private final RedisUtil redisUtil;

    @Autowired
    public JwtUtil(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    /**
     * 创建令牌
     *
     * @param claims 有效载荷，用户数据
     * @return java.lang.String 令牌
     */
    public String createToken(final Map<String, Object> claims) {
        try {
            Assert.notEmpty(claims, "令牌参数不能为空");

            //创建令牌
            String token = null;
            switch (type) {
                case 1:
                    token = createPlainToken(claims);
                    break;
                case 2:
                    token = createSignedToken(claims);
                    break;
                case 3:
                    token = createEncryptedToken(claims);
                    break;
                default:
            }

            //缓存令牌白名单，有效期：令牌过期时间+令牌过期允许刷新时间
            redisUtil.expire(token, "", Duration.ofMillis(getExpirationTime().getTime() + refreshTime));

            return token;
        } catch (Exception e) {
            log.debug("创建令牌异常：{}", e.getMessage());
            e.printStackTrace();
            throw new BaseException("创建令牌异常");
        }
    }

    /**
     * 验证令牌是否有效
     * <p>
     * 有效规则：
     * 1、未到刷新截至时间
     * 2、redis缓存白名单存在
     *
     * @param token 令牌
     * @return
     */
    public boolean validToken(final String token) {
        JWTClaimsSet claims = this.getClaims(token);
        return new Date().before(new Date(claims.getExpirationTime().getTime() + refreshTime)) && redisUtil.exist(token);
    }

    /**
     * 是否可以刷新令牌
     *
     * @param token 令牌
     * @return
     */
    public boolean isCanRefreshToken(final String token) {
        JWTClaimsSet claims = this.getClaims(token);
        Date currentDate = new Date();
        return claims.getExpirationTime().before(currentDate) && currentDate.before(new Date(claims.getExpirationTime().getTime() + refreshTime));
    }

    /**
     * 刷新token，续期
     *
     * @param token 令牌
     * @return
     */
    public String refreshTokenIfCanRefresh(String token) {

        JWTClaimsSet claims = this.getClaims(token);
        if (new Date().before(claims.getExpirationTime())) {
            return token;
        }

        //刷新令牌，令牌过期并且在允许刷新时间范围内
        String newToken = null;
        if (new Date().before(new Date(claims.getExpirationTime().getTime() + refreshTime))) {
            newToken = createToken(claims.getClaims());
            log.info("用户{}登录过期，刷新令牌：{}", claims.getClaim("account"), newToken);

            //删除旧令牌
            redisUtil.delete(token);
        }

        return newToken;
    }

    /**
     * 移出白名单、主动失效
     * <p>
     * 注意：jwt这种session机制本身没有主动失效这么一说，只有到期过期
     *
     * @param token 令牌
     */
    public void removeFromWhiteList(final String token) {
        if (redisUtil.exist(token)) {
            redisUtil.delete(token);
        }
    }

    /**
     * 获取账户
     *
     * @param token 令牌
     * @return
     */
    public String getAccountFromToken(final String token) {
        Object account = this.getClaims(token).getClaim("account");
        return ObjectUtil.isNotEmpty(account) ? account.toString() : null;
    }

    /**
     * 获取有效载荷
     *
     * @param token 令牌
     * @return net.minidev.json.JSONObject 有效载荷
     */
    private JWTClaimsSet getClaims(final String token) {
        try {
            Assert.notEmpty(token, "令牌不能为空");

            JWTClaimsSet jwtClaimsSet = null;
            switch (type) {
                case 1:
                    jwtClaimsSet = PlainJWT.parse(token).getJWTClaimsSet();
                    break;
                case 2:
                    jwtClaimsSet = SignedJWT.parse(token).getJWTClaimsSet();
                    break;
                case 3:
                    EncryptedJWT parseEncryptedJwt = EncryptedJWT.parse(token);
                    //解密，不加密有效载荷获取不到，有效载荷被加密
                    parseEncryptedJwt.decrypt(new RSADecrypter(new RSAKeyGenerator(RSAKeyGenerator.MIN_KEY_SIZE_BITS).generate()));
                    jwtClaimsSet = parseEncryptedJwt.getJWTClaimsSet();
                    break;
                default:
            }
            return jwtClaimsSet;
        } catch (Exception e) {
            log.debug("令牌解析异常：{}", e.getMessage());
            e.printStackTrace();
            throw new BaseException("令牌解析异常");
        }
    }

    /**
     * 加密JWT（最安全，payload进行了加密）
     *
     * @param claims 有效载荷声明
     * @return
     */
    private String createEncryptedToken(final Map<String, Object> claims) throws JOSEException {
        JWEHeader.Builder headerBuilder = new JWEHeader.Builder(JWEAlgorithm.A256GCMKW, EncryptionMethod.A128CBC_HS256);
        JWTClaimsSet.Builder payloadBuilder = new JWTClaimsSet.Builder();
        claims.forEach(payloadBuilder::claim);
        payloadBuilder.issueTime(new Date());
        payloadBuilder.expirationTime(getExpirationTime());
        EncryptedJWT encryptedJwt = new EncryptedJWT(headerBuilder.build(), payloadBuilder.build());
        encryptedJwt.encrypt(new RSAEncrypter(new RSAKeyGenerator(RSAKeyGenerator.MIN_KEY_SIZE_BITS).generate()));
        return encryptedJwt.serialize();
    }

    /**
     * 签名JWT（不会被修改，但是header和payload只是进行Base64编码，有信息泄露风险）
     *
     * @param claims 有效载荷声明
     * @return
     */
    private String createSignedToken(final Map<String, Object> claims) throws JOSEException {
        JWSHeader.Builder headerBuilder = new JWSHeader.Builder(JWSAlgorithm.RS256);
        JWTClaimsSet.Builder payloadBuilder = new JWTClaimsSet.Builder();
        claims.forEach(payloadBuilder::claim);
        payloadBuilder.issueTime(new Date());
        payloadBuilder.expirationTime(getExpirationTime());
        SignedJWT signedJwt = new SignedJWT(headerBuilder.build(), payloadBuilder.build());
        signedJwt.sign(new RSASSASigner(new RSAKeyGenerator(RSAKeyGenerator.MIN_KEY_SIZE_BITS).generate())); //new MACSigner(secret.getBytes())
        return signedJwt.serialize();
    }

    /**
     * 创建普通JWT（明文，不安全）
     *
     * @param claims 有效载荷声明
     * @return
     */
    private String createPlainToken(final Map<String, Object> claims) {
        PlainHeader.Builder headerBuilder = new PlainHeader.Builder();
        headerBuilder.type(JOSEObjectType.JWT);
        JWTClaimsSet.Builder payloadBuilder = new JWTClaimsSet.Builder();
        claims.forEach(payloadBuilder::claim);
        payloadBuilder.issueTime(new Date());
        payloadBuilder.expirationTime(getExpirationTime());
        PlainJWT plainJwt = new PlainJWT(headerBuilder.build(), payloadBuilder.build());
        return plainJwt.serialize();
    }

    /**
     * 2分钟后过期
     *
     * @return
     */
    private Date getExpirationTime() {
        long millis = System.currentTimeMillis() + expirationTime;
        return new Date(millis);
    }
}
