package com.gjob.base.redis;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;

@Component
@ConfigurationProperties(prefix = "spring.redis")
public class RedisProperties {
	public int getMaxTotal() {
		return maxTotal;
	}
	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}
	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}
	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}
	public long getMaxWaitMillis() {
		return maxWaitMillis;
	}
	public void setMaxWaitMillis(long maxWaitMillis) {
		this.maxWaitMillis = maxWaitMillis;
	}
	public int getMaxIdle() {
		return maxIdle;
	}
	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public JedisSentinelPool getJedisSentinelPool() {
		return jedisSentinelPool;
	}
	public void setJedisSentinelPool(JedisSentinelPool jedisSentinelPool) {
		this.jedisSentinelPool = jedisSentinelPool;
	}
	public JedisPool getJedisPool() {
		return jedisPool;
	}
	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}
	public Set<String> getSentinels() {
		return sentinels;
	}
	public void setSentinels(Set<String> sentinels) {
		this.sentinels = sentinels;
	}
	public boolean isSentinels() {
		return isSentinels;
	}
	public void setSentinels(boolean isSentinels) {
		this.isSentinels = isSentinels;
	}
	public String getMasterName() {
		return masterName;
	}
	public void setMasterName(String masterName) {
		this.masterName = masterName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isCluster() {
		return isCluster;
	}
	public void setIsCluster(boolean isCluster) {
		this.isCluster = isCluster;
	}
	public JedisCluster getJedisCluster() {
		return jedisCluster;
	}
	public void setJedisCluster(JedisCluster jedisCluster) {
		this.jedisCluster = jedisCluster;
	}
	public Set<HostAndPort> getClusters() {
		return clusters;
	}
	public void setClusters(Set<HostAndPort> clusters) {
		this.clusters = clusters;
	}
	private int maxTotal;
	private boolean testOnBorrow =true;
	private long maxWaitMillis=-1;
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
	public String getMaster() {
		return master;
	}
	public void setMaster(String master) {
		this.master = master;
	}
	private String master;
}
