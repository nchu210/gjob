/********************************************
 * 文件名称: JedisFactory.java
 *********************************************/

package com.gjob.base.redis;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Protocol;

public class JedisFactory {
	private static JedisFactory jrdisFactory = new JedisFactory();
	private int maxTotal;
	private boolean testOnBorrow =true;
	private long maxWaitMillis=-1;
	private int maxIdle;
	private int port;
	private String ip;
	private JedisSentinelPool jedisSentinelPool = null;
	public JedisPool jedisPool = null;
	private Set<String> sentinels = new HashSet<String>();
	private boolean isSentinels = false;
	private String masterName;
	private String password;
	private boolean isCluster = false;
	private JedisCluster jedisCluster;
	private Set<HostAndPort> clusters = new HashSet<HostAndPort>();

	static public JedisFactory getInstance() {
		return jrdisFactory;
	}

	public void init(RedisProperties redisProperties) {
		isCluster=redisProperties.isCluster();
		isSentinels=redisProperties.isSentinels();
		try {
			JedisPoolConfig config = new JedisPoolConfig();
			// 控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
			// 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
			if (redisProperties.getMaxTotal()>0) config.setMaxTotal(redisProperties.getMaxTotal());
			// 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
			if(redisProperties.getMaxIdle()>0) config.setMaxIdle(redisProperties.getMaxIdle());
			// 表示当borrow(引用)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
			config.setMaxWaitMillis(redisProperties.getMaxWaitMillis());
			// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
			config.setTestOnBorrow(redisProperties.isTestOnBorrow());
			// 支持单节点和Sentinels两种模式
			if (redisProperties.isSentinels()) {
				if (redisProperties.getPassword()!=null) {
					jedisSentinelPool = new JedisSentinelPool(redisProperties.getMasterName(),
							redisProperties.getSentinels(), config, Protocol.DEFAULT_TIMEOUT,
							redisProperties.getPassword());
				} else {
					jedisSentinelPool = new JedisSentinelPool(redisProperties.getMasterName(),
							redisProperties.getSentinels(), config, Protocol.DEFAULT_TIMEOUT						
							);
				}
			} else if (redisProperties.isCluster()) {				
				if (redisProperties.getPassword()!=null) {
					// new JedisCluster();
//					jedisCluster = new JedisCluster(clusters,
//							(int) maxWaitMillis, (int) maxWaitMillis, maxIdle,
//							password, config);
					jedisCluster = new JedisCluster(redisProperties.getClusters(),config);
					
				} else {
					jedisCluster = new JedisCluster(redisProperties.getClusters(), config);
				}
			} else {
				if (redisProperties.getClusters()!=null) {
					jedisPool = new JedisPool(config, redisProperties.getIp(), redisProperties.getPort(),
							Protocol.DEFAULT_TIMEOUT, redisProperties.getPassword());
				} else {
					jedisPool = new JedisPool(config,redisProperties.getIp(), redisProperties.getPort());
				}
			}
//			LogDefine.getLog().info(
//					"redis插件初始化成功");
		} catch (Exception e) {
//			LogDefine.getLog().error(
//					"redis插件初始化异常：" + e.getMessage());
			System.out.println(e.getMessage());
		}

	
	}

	private JedisFactory() {
	}

	public Jedis getJedis() {
		if (isSentinels) {
			return jedisSentinelPool.getResource();
		} else {
			return jedisPool.getResource();
		}
	}

	public void returnResource(Jedis redis) {
		if (redis != null) {
			if (isSentinels) {
				jedisSentinelPool.returnResourceObject(redis);
			} else {
				jedisPool.returnResourceObject(redis);
			}
		}
	}
}
