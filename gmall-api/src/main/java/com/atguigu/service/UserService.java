package com.atguigu.service;

import com.atguigu.bean.UserAddress;
import com.atguigu.bean.UserInfo;

import java.util.List;

public interface UserService {
    List<UserInfo> userInfoList();

    UserInfo login(UserInfo userInfo);

    List<UserAddress> getAddressList(String userId);
}
