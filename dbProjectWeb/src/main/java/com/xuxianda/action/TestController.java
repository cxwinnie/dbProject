package com.xuxianda.action;

import com.xuxianda.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisCluster;

import java.util.Arrays;
import java.util.List;

/**
 * Created by XiandaXu on 2019/12/21.
 */
@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @Autowired
    private JedisCluster jedisCluster;

    @RequestMapping("/test1")
    public List test1(){
        return testService.test1();
    }

    @RequestMapping("/test2")
    public List test2(){
        jedisCluster.set("韩峰1","gay里gay气11");
        return Arrays.asList(jedisCluster.get("韩峰1"));
    }

}
