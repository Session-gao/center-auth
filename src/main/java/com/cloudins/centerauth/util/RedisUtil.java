package com.cloudins.centerauth.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis 资源池管理
 * Created by gpl on 12.18.
 */
public class RedisUtil {
    private static String ADDR = "127.0.0.1";
    private static int PORT = 6379;
    private static int MAX_ACTIVE = 1024;
    private static int MAX_IDLE = 200;
    private static int MAX_WAIT = 10000;
    private static int TIMEOUT = 10000;
    private static boolean TEST_ON_BORROW = true;
    private static JedisPool jedisPool = null;

    //初始化连接池
    static {
        try{
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWaitMillis(MAX_WAIT);
            config.setTestOnBorrow(TEST_ON_BORROW);
            jedisPool = new JedisPool(config,ADDR,PORT);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //获取jedis实例
    public synchronized static Jedis getJedis(){
        try {
            if(jedisPool != null){
                Jedis resource = jedisPool.getResource();
                return resource;
            }else{
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    //释放jedis资源
    public static void returnResource(final Jedis jedis){
        if(jedis != null){
            jedisPool.returnResource(jedis);
        }
    }

}
