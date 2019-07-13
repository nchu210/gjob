package com.gjob;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.gjob.base.redis.JedisCacheBase;
import com.gjob.base.redis.JedisFactory;
import com.gjob.base.redis.RedisProperties;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
@Component
public class GjobStart implements ApplicationRunner {
	@Autowired
    private RedisProperties redisProperties;
	@Override
	public void run(ApplicationArguments args) throws Exception {
		// TODO Auto-generated method stub
		//加载日志插件
		//加载数据库插件
		//加载redis
		intiRedis();
		//加载自定义插件
		test();
	}
	private void test() {
		// TODO Auto-generated method stub
		JedisCacheBase.set("A", "1");
		JedisCacheBase.set("B", "2");
		JedisCacheBase.set("C", "3");
		JedisCacheBase.set("D", "4");
		JedisCacheBase.set("E", "5");
		JedisCacheBase.set("F", "6");
		System.out.println(JedisCacheBase.get("E"));

	}
	/**
	 * 初始化Redis工厂
	 */
	@SuppressWarnings("static-access")
	private void intiRedis() {
		if(redisProperties.isCluster()) {
			Set<HostAndPort> clustersSet = new HashSet<HostAndPort>();
			String []a=redisProperties.getIp().split("\\|");
			for(int i=0;i<a.length;i++) {
		        System.out.println("redis集群: "+a[i]);
				clustersSet.add(new HostAndPort(a[i].split(":")[0], Integer.parseInt(a[i].split(":")[1])));
			}
			redisProperties.setClusters(clustersSet);
		}

		JedisFactory.getInstance().init(redisProperties);
        
	}

}
