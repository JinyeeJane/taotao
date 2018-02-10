package com.taotao.rest.jedis;

import java.util.HashSet;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

public class JedisTest {
	
	@Test
	public void testJedisSingle() {
		//创建jedis对象
		Jedis jedis=new Jedis("192.168.244.128",6379);
		//调用jedis方法
		jedis.set("key1", "jedis test");
		System.out.println(jedis.get("key1"));
		//关闭jedis
		jedis.close();
	}
	
	/**
	 * 使用连接池
	 */
	@Test
	public void testJedisPool() {
		//创建连接池
		JedisPool pool=new JedisPool("192.168.244.128",6379);
		//从连接池中获取Jedis对象
		Jedis jedis=pool.getResource();
		System.out.println("pool:"+jedis.get("key1"));
		//关闭资源
		jedis.close();
		pool.close();
	}
	
	/**
	 * 集群测试
	 */
	@Test
	public void testJedisCluster() {
		//配置集群中所有的redis
		HashSet<HostAndPort> nodes=new HashSet<>();
		nodes.add(new HostAndPort("192.168.244.128", 7001));
		nodes.add(new HostAndPort("192.168.244.128", 7002));
		nodes.add(new HostAndPort("192.168.244.128", 7003));
		nodes.add(new HostAndPort("192.168.244.128", 7004));
		nodes.add(new HostAndPort("192.168.244.128", 7005));
		nodes.add(new HostAndPort("192.168.244.128", 7006));
		//创建集群对象
		JedisCluster cluster=new JedisCluster(nodes);
		cluster.set("key1", "1000");
		System.out.println(cluster.get("key1"));
		//关闭资源
		cluster.close();
	}
	
	@Test
	public void testSpringJedisClister(){
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
		JedisCluster jedisCluster =  (JedisCluster) applicationContext.getBean("redisClient");
		String string = jedisCluster.get("key1");
		System.out.println(string);
		jedisCluster.close();
	}
	
	@Test
	public void testSpringJedisSingle() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
		JedisPool pool = (JedisPool) applicationContext.getBean("redisClient");
		Jedis jedis = pool.getResource();
		String string = jedis.get("key1");
		System.out.println(string);
		jedis.close();
		pool.close();
	}
}
