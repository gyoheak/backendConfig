package com.example.demo.service.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DbTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void RedisTest(){
        String key = "name";
        String value = "mgko";

        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value);
        Object retrievedValue = valueOperations.get(key);

        assertThat(retrievedValue).isEqualTo(value);

        redisTemplate.delete(key);
    }
}
