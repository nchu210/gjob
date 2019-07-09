package com.gjob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.gjob.base.redis.JedisFactory;
import com.gjob.base.redis.RedisProperties;
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
	}
/**
 * 初始化Redis工厂
 */
	private void intiRedis() {
		// TODO Auto-generated method stub

		JedisFactory jedisFactory=JedisFactory.getInstance();
		System.out.println(redisProperties.getPassword());
		//jedisFactory.setMaxTotal(maxTotal);
	}

}
