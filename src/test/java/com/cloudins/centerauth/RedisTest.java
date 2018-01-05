package com.cloudins.centerauth;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Test
    public void setRedisTemplateTest(){
//        redisTemplate.opsForValue().set("a",new String("mmm"));
        redisTemplate.opsForValue().set("a","mmm");
        Assert.assertEquals("mmm",redisTemplate.opsForValue().get("a"));

    }
}
