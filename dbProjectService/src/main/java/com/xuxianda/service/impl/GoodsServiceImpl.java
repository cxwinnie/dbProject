package com.xuxianda.service.impl;

import com.xuxianda.dao.GoodsMapper;
import com.xuxianda.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by XiandaXu on 2019/12/22.
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public Map getGoodsInfo(String goodsId) {
        return goodsMapper.getGoodsInfo(goodsId);
    }

    public List<Map> getGoodsInfoByType(Map queryMap){
        return goodsMapper.getGoodsInfoByType(queryMap);
    }

}
