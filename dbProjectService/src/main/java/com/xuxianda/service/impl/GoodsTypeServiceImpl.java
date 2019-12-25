package com.xuxianda.service.impl;

import com.xuxianda.dao.GoodsTypeMapper;
import com.xuxianda.service.GoodsTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by XiandaXu on 2019/12/22.
 */
@Service
public class GoodsTypeServiceImpl implements GoodsTypeService{

    @Autowired
    private GoodsTypeMapper goodsTypeMapper;

    @Override
    public Map getGoodsType(Map queryMap) {
        return goodsTypeMapper.getGoodsType(queryMap);
    }
}
