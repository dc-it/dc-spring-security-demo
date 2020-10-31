package com.example.demo.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * redis工具
 *
 * @author duchao
 */
@Component
public class RedisUtil {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisUtil(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 设置值
     *
     * @param key   键
     * @param value 值
     */
    public void set(final String key, final String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 取值
     *
     * @param key 键
     * @return java.lang.String 值
     */
    public String get(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 某个key是否存在
     *
     * @param key 键
     * @return
     */
    public Boolean exist(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置过期
     *
     * @param key      键
     * @param duration
     */
    public void expire(final String key, final Duration duration) {
        redisTemplate.expire(key, duration);
    }

    /**
     * 设置过期
     *
     * @param key      键
     * @param value    值
     * @param duration
     */
    public void expire(final String key, final String value, final Duration duration) {
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, duration);
    }

    /**
     * 删除缓存
     *
     * @param key 键
     */
    public void delete(final String key) {
        redisTemplate.delete(key);
    }
}
