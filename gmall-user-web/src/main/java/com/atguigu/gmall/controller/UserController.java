package com.atguigu.gmall.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.bean.UserInfo;
import com.atguigu.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Reference
    private UserService userService;

   @RequestMapping("/userInfoList")
    public List<UserInfo> userInfoList(){
       List<UserInfo> userInfos = userService.userInfoList();

       return userInfos;
    }

}
