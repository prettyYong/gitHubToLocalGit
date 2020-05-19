package com.wy.redis;

import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;

public class RedisDemo {
    public static void main(String[] args) {
        //连接本地的redis服务
        Jedis jedis=new Jedis("localhost");
        System.out.println("jedis连接成功");
        //设置redis服务密码
        jedis.auth("redis");
        String ping = jedis.ping();
        System.out.println("ping="+ping);
        System.out.println("========string===========");
        jedis.set("str1","str的值");
        System.out.println(jedis.get("str1"));
        System.out.println("==========list=========");
        jedis.lpush("list1","wang");
        jedis.lpush("list1","and");
        jedis.lpush("list1","yong");
        List<String> stringList = jedis.lrange("list1", 0, 2);
        for(String str:stringList){
            System.out.println(str);
        }
        System.out.println("==========key=========");
        Set<String> keys = jedis.keys("*");
        for(String key:keys){
            System.out.println(key);
        }


    }
}
