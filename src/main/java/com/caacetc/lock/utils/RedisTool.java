package com.caacetc.lock.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;

public class RedisTool {
    private static final String LOCK_SUCCESS = "OK";
    /**
     * nxxx，这个参数我们填的是NX，意思是SET IF NOT EXIST，即当key不存在时，我们进行set操作；若key已经存在，则不做任何操作；
     */
    private static final String SET_IF_NOT_EXIST = "NX";
    /**
     * 这个参数我们传的是PX，意思是我们要给这个key加一个过期的设置，毫秒级别。
     */
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final Long RELEASE_SUCCESS = 1L;


    /**
     * set()加入了NX参数，可以保证如果已有key存在，则函数不会调用成功，也就是只有一个客户端能持有锁，
     * 满足互斥性。其次，由于我们对锁设置了过期时间，即使锁的持有者后续发生崩溃而没有解锁，
     * 锁也会因为到了过期时间而自动解锁（即key被删除），不会发生死锁。最后，因为我们将value赋值为requestId，
     * 代表加锁的客户端请求标识，那么在客户端在解锁的时候就可以进行校验是否是同一个客户端。
     * 由于我们只考虑Redis单机部署的场景，所以容错性我们暂不考虑。
     *
     *
     * 原则：
     * 互斥性。在任意时刻，只有一个客户端能持有锁。
     * 不会发生死锁。即使有一个客户端在持有锁的期间崩溃而没有主动解锁，也能保证后续其他客户端能加锁。
     * 具有容错性。只要大部分的Redis节点正常运行，客户端就可以加锁和解锁。
     * 解铃还须系铃人。加锁和解锁必须是同一个客户端，客户端自己不能把别人加的锁给解了。
     */

    /**
     * 尝试获取分布式锁
     * 1. 当前没有锁（key不存在），那么就进行加锁操作，并对锁设置个有效期，同时value表示加锁的客户端。
     * 2. 已有锁存在，不做任何操作。
     * @param jedis Redis客户端 我们使用key来当锁，因为key是唯一的。
     * @param lockKey 锁 我们传的是requestId，很多童鞋可能不明白，有key作为锁不就够了吗，
     *                为什么还要用到value？原因就是我们在上面讲到可靠性时，
     *                分布式锁要满足第四个条件解铃还须系铃人，通过给value赋值为requestId，
     *                我们就知道这把锁是哪个请求加的了，在解锁的时候就可以有依据。
     *                requestId可以使用UUID.randomUUID().toString()方法生成。
     * @param requestId 请求标识
     * @param expireTime 超期时间
     * @return 是否获取成功
     */
    public static boolean tryGetDistributedLock(Jedis jedis, String lockKey, String requestId, int expireTime) {

        //String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
        SetParams setParams = new SetParams();
        setParams.nx();
        setParams.px(expireTime);
        String result = jedis.set(lockKey, requestId, setParams);

        if (LOCK_SUCCESS.equals(result)) {
            return true;
        }
        return false;

    }
    /**
     * 释放分布式锁
     * @param jedis Redis客户端
     * @param lockKey 锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public static boolean releaseDistributedLock(Jedis jedis, String lockKey, String requestId) {

        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));

        if (RELEASE_SUCCESS.equals(result)) {
            return true;
        }
        return false;

    }

}
