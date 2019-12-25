package com.xuxianda.service;

import java.util.List;
import java.util.Map;

/**
 * Created by XiandaXu on 2019/12/22.
 */
public interface GoodsService {

    Map getGoodsInfo(String goodsId);

    List<Map> getGoodsInfoByType(Map queryMap);

}
