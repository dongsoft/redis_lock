package com.caacetc.lock.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisConnect {

    /**
     * 获取jedis
     * @return Jedis
     */
    public static JedisPool  getJedis(){
        JedisPool pool = new JedisPool("192.168.47.128",6379);
        //Jedis jedis = pool.getResource();
        return pool;
    }

    public static void close(JedisPool pool ,Jedis jedis){
        if(jedis != null){
            jedis.close();
        }

        if(pool != null){
            pool.close();
        }
    }
}
