package com.atguigu.gmall.list.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.bean.UserAddress;
import com.atguigu.bean.UserInfo;
import com.atguigu.gmall.list.mapper.UserAddressMapper;
import com.atguigu.gmall.list.mapper.UserInfoMapper;
import com.atguigu.gmall.utils.RedisUtil;
import com.atguigu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserInfoMapper userInfoMapper;


    @Autowired
    private UserAddressMapper userAddressMapper;


    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<UserInfo> userInfoList() {
        return userInfoMapper.selectAll();
    }

    /**
     * 查询用户信息,同步缓存
     * @param userInfo
     * @return
     */
    @Override
    public UserInfo login(UserInfo userInfo) {

        UserInfo user = userInfoMapper.selectOne(userInfo);

        //同步缓存
        if (user != null) {
            Jedis jedis = redisUtil.getJedis();
            jedis.set("user:" + user.getId() + ":info", JSON.toJSONString(user));
            jedis.close();
        }

        return user;
    }




    /**
     *  根据用户id查询收货地址
     * @param userId
     * @return
     */
    @Override
    public List<UserAddress> getAddressList(String userId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        List<UserAddress> userAddressList = userAddressMapper.select(userAddress);
        return userAddressList;
    }
}
