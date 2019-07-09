package com.gjob.base.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.redis")
public class RedisProperties {
    private int expireSeconds;
    private List<String> clusterNodes = new ArrayList<String>();
    private String password;
    private int commandTimeout;
    private Map<String,Integer> pool = new HashMap<String, Integer>();
    public int getExpireSeconds() {
        return expireSeconds;
    }
    public void setExpireSeconds(int expireSeconds) {
        this.expireSeconds = expireSeconds;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public int getCommandTimeout() {
        return commandTimeout;
    }
    public void setCommandTimeout(int commandTimeout) {
        this.commandTimeout = commandTimeout;
    }
    public Map<String, Integer> getPool() {
        return pool;
    }
    public void setPool(Map<String, Integer> pool) {
        this.pool = pool;
    }
	public List<String> getClusterNodes() {
		return clusterNodes;
	}
	public void setClusterNodes(List<String> clusterNodes) {
		this.clusterNodes = clusterNodes;
	}
}
