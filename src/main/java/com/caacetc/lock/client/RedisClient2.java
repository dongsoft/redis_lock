package com.caacetc.lock.client;

import com.caacetc.lock.utils.RedisConnect;
import com.caacetc.lock.utils.RedisTool;
import com.caacetc.lock.utils.ToolsUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisClient2 {
    private static String localKey = "REDIS_LOCK";
    public static void main(String[] args) {
        JedisPool pool = RedisConnect.getJedis();
        Jedis jedis = pool.getResource();
        String uuid = ToolsUtil.getUuid();
        System.out.println("requestId : " + uuid);

        boolean flag = true;
        while (flag){
            System.out.println("尝试获取锁..." );
            boolean lock = RedisTool.tryGetDistributedLock(jedis, localKey, uuid, 100000);
            if(lock){
                flag = false;
                System.out.println("本大爷终于获取到锁了！" );
            }else{
                System.out.println("烦死了，谁干活这么慢");
            }
        }

        // 让本大爷干干活先
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //活干完了，轮到client1了，释放锁
        boolean b = RedisTool.releaseDistributedLock(jedis, localKey, uuid);
        if(b){
            System.out.println("本大爷活干完了，释放锁成功");
        }else{
            System.out.println("释放锁失败");
        }

        // 释放redis连接资源
        RedisConnect.close(pool,jedis);
    }
}
