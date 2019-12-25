package com.xuxianda.action;

import com.xuxianda.service.GoodsService;
import com.xuxianda.service.GoodsTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.*;

/**
 * Created by XiandaXu on 2019/12/22.
 */
@RestController
public class GoodsController {

    @Autowired
    private JedisCluster jedisCluster;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsTypeService goodsTypeService;

    @RequestMapping(value = "/visit", method = RequestMethod.GET)
    public Map visit(String goodsId) {
        String integer = jedisCluster.get(goodsId);
        if (integer == null) {
            jedisCluster.set(goodsId, "1");
        } else {
            Integer num = new Integer(integer);
            num++;
            jedisCluster.set(goodsId, num.toString());
        }
        return goodsService.getGoodsInfo(goodsId);
    }

    @RequestMapping(value = "/visit1", method = RequestMethod.GET)
    public Map visit1() {
        Map<Integer, Integer> kvMap = new HashMap();
        Map<Integer, Integer> thirdMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> secondMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> firstMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> topMap = new HashMap<Integer, Integer>();
        Map<String,Object> dataMap = new HashMap<>();
        Map<String,Object> resultMap = new HashMap<>();
        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();

        for (Map.Entry<String, JedisPool> entry : clusterNodes.entrySet()) {
            Jedis jedis = entry.getValue().getResource();
            // 判断非从节点(因为若主从复制，从节点会跟随主节点的变化而变化)
            if (!jedis.info("replication").contains("role:slave")) {
                Set<String> keys = jedis.keys("*");
                if (keys.size() > 0) {
                    for (String key : keys) {
                        kvMap.put(new Integer(key), new Integer(jedisCluster.get(key)));
                    }
                }
            }
        }

        kvMap.forEach((k, v) -> {
            Map goodsInfo = goodsService.getGoodsInfo(k + "");
            Integer goodsTypeId = new Integer(goodsInfo.get("goodsTypeId").toString());
            if (thirdMap.get(goodsTypeId) == null) {
                thirdMap.put(goodsTypeId, v);
            } else {
                Integer count = thirdMap.get(goodsTypeId);
                thirdMap.put(goodsTypeId, count+v);
            }
        });

        thirdMap.forEach((k, v) -> {
            Map queryMap = new HashMap();
            queryMap.put("id", k);
            Map map = goodsTypeService.getGoodsType(queryMap);
            Integer fatherId = new Integer(map.get("fatherId").toString());
            if (secondMap.get(fatherId) == null) {
                secondMap.put(fatherId, v);
            } else {
                Integer count = secondMap.get(fatherId);
                secondMap.put(fatherId, count + v);
            }
        });

        secondMap.forEach((k, v) -> {
            Map queryMap = new HashMap();
            queryMap.put("id", k);
            Map map = goodsTypeService.getGoodsType(queryMap);
            Integer fatherId = new Integer(map.get("fatherId").toString());
            if (firstMap.get(fatherId) == null) {
                firstMap.put(fatherId, v);
            } else {
                Integer count = firstMap.get(fatherId);
                firstMap.put(fatherId, count + v);
            }
        });

        firstMap.forEach((k, v) -> {
            Map queryMap = new HashMap();
            queryMap.put("id", k);
            Map map = goodsTypeService.getGoodsType(queryMap);
            Integer fatherId = new Integer(map.get("fatherId").toString());
            if (topMap.get(fatherId) == null) {
                topMap.put(fatherId, v);
            } else {
                Integer count = topMap.get(fatherId);
                topMap.put(fatherId, count + v);
            }
        });

        ArrayList<Map.Entry<Integer, Integer>> kvMapEntries = new ArrayList<>(kvMap.entrySet());
        Collections.sort(kvMapEntries, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        ArrayList<Map.Entry<Integer, Integer>> topMapEntries = new ArrayList<>(topMap.entrySet());
        Collections.sort(topMapEntries, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        ArrayList<Map.Entry<Integer, Integer>> firstMapEntries = new ArrayList<>(firstMap.entrySet());
        Collections.sort(firstMapEntries, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        ArrayList<Map.Entry<Integer, Integer>> secondMapEntries = new ArrayList<>(secondMap.entrySet());
        Collections.sort(secondMapEntries, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        ArrayList<Map.Entry<Integer, Integer>> thirdMapEntries = new ArrayList<>(thirdMap.entrySet());
        Collections.sort(thirdMapEntries, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        Map queryMap = new HashMap();
        if (topMapEntries.size() != 0) {
            queryMap.put("id",topMapEntries.get(0).getKey().toString());
            Map goodsType = goodsTypeService.getGoodsType(queryMap);
            dataMap.put("1",goodsType.get("type"));
            queryMap.put("id",firstMapEntries.get(0).getKey().toString());
            goodsType = goodsTypeService.getGoodsType(queryMap);
            dataMap.put("2",goodsType.get("type"));
            queryMap.put("id",secondMapEntries.get(0).getKey().toString());
            goodsType = goodsTypeService.getGoodsType(queryMap);
            dataMap.put("3",goodsType.get("type"));
            queryMap.put("id",thirdMapEntries.get(0).getKey().toString());
            goodsType = goodsTypeService.getGoodsType(queryMap);
            dataMap.put("4",goodsType.get("type"));

            resultMap.put("趋势","此用户最近浏览  "+dataMap.get("1")+">"+dataMap.get("2")+">"+dataMap.get("3")+">"+dataMap.get("4") +"    较多");

            queryMap.clear();
            queryMap.put("typeId",thirdMapEntries.get(0).getKey().toString());
            resultMap.put("猜你喜欢1",goodsService.getGoodsInfoByType(queryMap));

            Map goodsInfo = goodsService.getGoodsInfo(kvMapEntries.get(0).getKey().toString());
            queryMap.clear();

            queryMap.put("maxPrice",new Integer(goodsInfo.get("price").toString()) * 1.2);
            queryMap.put("minPrice",new Integer(goodsInfo.get("price").toString()) * 0.8);
            resultMap.put("猜你喜欢2",goodsService.getGoodsInfoByType(queryMap));

        }

        return resultMap;
    }

    @RequestMapping(value = "/visit2", method = RequestMethod.GET)
    public Map visit2() {
        Map<Integer, Integer> kvMap = new HashMap();
        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();

        for (Map.Entry<String, JedisPool> entry : clusterNodes.entrySet()) {
            Jedis jedis = entry.getValue().getResource();
            // 判断非从节点(因为若主从复制，从节点会跟随主节点的变化而变化)
            if (!jedis.info("replication").contains("role:slave")) {
                Set<String> keys = jedis.keys("*");
                if (keys.size() > 0) {
                    for (String key : keys) {
                        kvMap.put(new Integer(key), new Integer(jedisCluster.get(key)));
                    }
                }
            }
        }
        return kvMap;
    }

}
