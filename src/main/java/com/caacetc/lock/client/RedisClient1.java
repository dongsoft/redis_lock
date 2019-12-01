package com.caacetc.lock.client;

import com.caacetc.lock.utils.RedisConnect;
import com.caacetc.lock.utils.RedisTool;
import com.caacetc.lock.utils.ToolsUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisClient1 {
    private static String localKey = "REDIS_LOCK";
    public static void main(String[] args) {
        JedisPool pool = RedisConnect.getJedis();
        Jedis jedis = pool.getResource();
        String uuid = ToolsUtil.getUuid();
        System.out.println("requestId : " + uuid);
        boolean result = RedisTool.tryGetDistributedLock(jedis, localKey, uuid, 100000);
        if(result){
            String requestId = jedis.get(localKey);
            System.out.println("获取锁成功 requestId : " + requestId);

        }else{
            System.out.println("获取锁失败");
        }


        // 模拟干活
        try {
            System.out.println("小弟正在干活，请稍等。。。");
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //活干完了，释放锁
        boolean b = RedisTool.releaseDistributedLock(jedis, localKey, uuid);
        if(b){
            System.out.println("释放锁成功");
        }else{
            System.out.println("释放锁失败");
        }

        // 释放redis连接资源
        RedisConnect.close(pool,jedis);
    }
}
