package com.xuxianda.dao;


import java.util.List;
import java.util.Map;

public interface GoodsMapper {

    Map getGoodsInfo(String goodsId);

    List<Map> getGoodsInfoByType(Map queryMap);

}