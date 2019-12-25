package com.xuxianda.config;

import com.xuxianda.properties.RedisProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisClusterConnection;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;


/**
 * Created by Xianda Xu on 2018/2/28.
 */
@Configuration
public class JedisClusterConfig {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public JedisCluster getJedisCluster(){
        String[] serverArray = redisProperties.getNodes().split(",");
        HashSet<HostAndPort> nodes = new HashSet<HostAndPort>();
        for(String ipPort : serverArray){
            String[] ipAndPort = ipPort.split(":");
            nodes.add(new HostAndPort(ipAndPort[0].trim(),Integer.parseInt(ipAndPort[1].trim())));
        }
        return new JedisCluster(nodes);
    }

    @Bean
    public JedisClusterConnection getJedisClusterConnection(){
        return new JedisClusterConnection(getJedisCluster());
    }

}
