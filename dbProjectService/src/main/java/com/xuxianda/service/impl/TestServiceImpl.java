package com.xuxianda.service.impl;

import com.xuxianda.dao.MyUserMapper;
import com.xuxianda.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by XiandaXu on 2019/12/21.
 */
@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private MyUserMapper myUserMapper;

    @Override
    public List test1() {
        return myUserMapper.selectAll();
    }

}
