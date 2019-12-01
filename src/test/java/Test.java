import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class Test {
    private static String localKey = "REDIS_LOCK";
    @org.junit.Test
    public void TestRedis(){
        Jedis jedis = new Jedis("192.168.47.128",6379);
        jedis.set("name","zhangdong");
        String name = jedis.get("name");
        System.out.println(name);
        jedis.close();
    }

    @org.junit.Test
    public void TestRedisPool(){
        JedisPool pool = new JedisPool("192.168.47.128",6379);
        Jedis jedis = pool.getResource();
        String name = jedis.get("name");
        System.out.println(name);
        String s = jedis.get(localKey);
        System.out.println(s);
    }
}
