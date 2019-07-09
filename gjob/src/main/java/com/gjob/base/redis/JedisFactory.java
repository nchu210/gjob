/********************************************
 * 文件名称: JedisFactory.java
 *********************************************/

package com.gjob.base.redis;

import java.util.HashSet;
import java.util.Set;

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
	private boolean testOnBorrow;
	private long maxWaitMillis;
	private int maxIdle;
	private int port;
	private String ip;
	private JedisSentinelPool jedisSentinelPool = null;
	private JedisPool jedisPool = null;
	private Set<String> sentinels = new HashSet<String>();
	private boolean isSentinels = false;
	private String masterName;
	private String password;
	private boolean isCluster = false;
	private JedisCluster jedisCluster;
	private Set<HostAndPort> clusters = new HashSet<HostAndPort>();

	public Set<HostAndPort> getClusters() {
		return clusters;
	}

	public void setClusters(Set<HostAndPort> clusters) {
		this.clusters = clusters;
	}

	public JedisCluster getJedisCluster() {
		return jedisCluster;
	}

	public void setJedisCluster(JedisCluster jedisCluster) {
		this.jedisCluster = jedisCluster;
	}

	public boolean isCluster() {
		return isCluster;
	}

	public void setCluster(boolean isCluster) {
		this.isCluster = isCluster;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setMasterName(String masterName) {
		this.masterName = masterName;
	}

	public boolean isSentinels() {
		return isSentinels;
	}

	public void setSentinels(boolean isSentinels) {
		this.isSentinels = isSentinels;
	}

	public void setSentinels(Set<String> sentinels) {
		this.sentinels = sentinels;
	}

	static public JedisFactory getInstance() {
		return jrdisFactory;
	}

	public void init() {
		try {
			JedisPoolConfig config = new JedisPoolConfig();
			// 控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
			// 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
			config.setMaxTotal(maxTotal);
			// 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
			config.setMaxIdle(maxIdle);
			// 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
			config.setMaxWaitMillis(maxWaitMillis);
			// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
			config.setTestOnBorrow(testOnBorrow);
			// 支持单节点和Sentinels两种模式
			if (isSentinels) {
				if (password==null) {
					jedisSentinelPool = new JedisSentinelPool(masterName,
							sentinels, config, Protocol.DEFAULT_TIMEOUT,
							password);
				} else {
					jedisSentinelPool = new JedisSentinelPool(masterName,
							sentinels, config, Protocol.DEFAULT_TIMEOUT);
				}
				// qiulh-20170407 保存redis服务信息用于后面日志中读取
				HostAndPort hp = jedisSentinelPool.getCurrentHostMaster();
				setIp(hp.getHost());
				setPort(hp.getPort());
			} else if (isCluster) {
				//M201808210788 ZhaoZ 20180821
				if (password==null) {
					// new JedisCluster();
//					jedisCluster = new JedisCluster(clusters,
//							(int) maxWaitMillis, (int) maxWaitMillis, maxIdle,
//							password, config);
					jedisCluster = new JedisCluster(clusters,
							(int) maxWaitMillis, (int) maxWaitMillis, maxIdle,
							 config);
					
				} else {
					jedisCluster = new JedisCluster(clusters, config);
				}
			} else {
				if (password==null) {
					jedisPool = new JedisPool(config, ip, port,
							Protocol.DEFAULT_TIMEOUT, password);
				} else {
					jedisPool = new JedisPool(config, ip, port);
				}
			}
//			LogDefine.getLog().info(
//					"redis插件初始化成功");
		} catch (Exception e) {
//			LogDefine.getLog().error(
//					"redis插件初始化异常：" + e.getMessage());
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

	public void setMaxWaitMillis(long maxWaitMillis) {
		this.maxWaitMillis = maxWaitMillis;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public String getIp() {
		return ip;
	}

	public static void main(String[] args) {
		JedisPoolConfig config = new JedisPoolConfig();
		// 控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
		// 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
		config.setMaxTotal(50);
		// 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
		config.setMaxIdle(10);
		// 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
		config.setMaxWaitMillis(1000);
		// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
		config.setTestOnBorrow(true);

		Set<String> sentinels = new HashSet<String>();
		sentinels.add(new HostAndPort("172.28.1.148", 26000).toString());
		sentinels.add(new HostAndPort("172.28.1.148", 26001).toString());
		JedisSentinelPool jedisSentinelPool = new JedisSentinelPool("mymaster",
				sentinels, config, 1000);
		jedisSentinelPool.getResource();

	}
}
