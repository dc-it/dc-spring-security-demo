package com.example.demo.common.util;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;
import lombok.experimental.UtilityClass;

import java.text.ParseException;
import java.util.Date;

/**
 * jwt工具类
 *
 * @author duchao
 */
@UtilityClass
public class JwtUtil {

    /**
     * 创建秘钥
     */
    private static final byte[] SECRET = "6MNSobBRCHGIO0fS6MNSobBRCHGIO0fS".getBytes();

    public static void main(String[] args) throws Exception {
        //testSignedJwt();
        //testPlainJwt();
    }

    public static void testSignedJwt2() throws ParseException, JOSEException {
        System.out.println("****************签名JWT***************");
        System.out.println("生成token--->");
    }

    public static void testSignedJwt() throws ParseException, JOSEException {
        System.out.println("****************签名JWT***************");
        System.out.println("生成token--->");
        JWSHeader.Builder headerBuilder = new JWSHeader.Builder(JWSAlgorithm.HS256);
        headerBuilder.type(JOSEObjectType.JWT).customParam("desc","签名JWT");
        JWTClaimsSet.Builder payloadBuilder = new JWTClaimsSet.Builder();
        payloadBuilder.expirationTime(getExpirationTime());
        payloadBuilder.claim("user","小明");
        payloadBuilder.claim("id","1");
        SignedJWT signedJWT = new SignedJWT(headerBuilder.build(),payloadBuilder.build());
        System.out.println(signedJWT.getHeader());
        System.out.println(signedJWT.getPayload());
        signedJWT.sign(new MACSigner(SECRET));
        System.out.println(signedJWT.getSignature());
        String token = signedJWT.serialize();
        System.out.println(token);
        System.out.println("校验token--->");
        SignedJWT parseSignedJwt = SignedJWT.parse(token);
        System.out.println(parseSignedJwt.getHeader());
        System.out.println(parseSignedJwt.getPayload());
        System.out.println(parseSignedJwt.getSignature());
    }

    public static void testPlainJwt() throws ParseException {
        System.out.println("****************简单JWT***************");
        System.out.println("生成token--->");
        PlainHeader.Builder headerBuilder = new PlainHeader.Builder();
        headerBuilder.type(JOSEObjectType.JWT);
        headerBuilder.customParam("desc","简单JWT");
        JWTClaimsSet.Builder payloadBuilder = new JWTClaimsSet.Builder();
        payloadBuilder.expirationTime(getExpirationTime());
        payloadBuilder.claim("user","小明");
        payloadBuilder.claim("id","1");
        PlainJWT plainJWT = new PlainJWT(headerBuilder.build(),payloadBuilder.build());
        System.out.println(plainJWT.getHeader());
        System.out.println(plainJWT.getPayload());
        String token = plainJWT.serialize();
        System.out.println(token);
        System.out.println("校验token--->");
        PlainJWT parsePlainJwt = PlainJWT.parse(token);
        System.out.println(parsePlainJwt.getHeader());
        System.out.println(parsePlainJwt.getPayload());
    }

    /**
     * 2分钟后过期
     *
     * @return
     */
    public static Date getExpirationTime() {
        long millis = System.currentTimeMillis() + 2 * 60 * 1000;
        return new Date(millis);
    }
}
