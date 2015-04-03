package x.commons.lock.distributed;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.xml.DOMConfigurator;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisLockTestCommons {
	
	protected static JedisPool jedisPool;
	protected static RedisLock lock;

	protected static void _init(long autoReleaseTimeMillis) {
		DOMConfigurator.configure(RedisLockTestCommons.class.getResource("/log4j.xml").getPath());
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(5);
		config.setMaxIdle(5);
		config.setMinIdle(5);
		jedisPool = new JedisPool(config, "127.0.0.1", 2468);
		lock = new RedisLock(jedisPool, "testkey", autoReleaseTimeMillis);
		// 激活池中连接
		activatePool();
	}
	
	private static void activatePool() {
		for (int i = 0; i < 5; i++) {
			Jedis jedis = null;
			try {
				jedis = jedisPool.getResource();
				jedis.setex("__tmpkey" + i, 1, "tmpvalue" + i);
			} finally {
				IOUtils.closeQuietly(jedis);
			}
		}
	}
	
	protected static void _cleanup() {
		jedisPool.destroy();
		jedisPool = null;
		
		lock = null;
	}
}