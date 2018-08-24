package com.atguigu.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.bean.CartInfo;
import com.atguigu.bean.UserInfo;
import com.atguigu.gmall.util.CookieUtil;
import com.atguigu.gmall.util.JwtUtil;
import com.atguigu.service.CartService;
import com.atguigu.service.UserService;
import io.jsonwebtoken.SignatureException;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.print.DocFlavor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PassPortController {


    @Reference
    private UserService userService;

    @Reference
    private CartService cartService;



     //http://passport.gmall.com:8085/index?returnUrl=http://cart.gmall.com:8084/toTrade
    @RequestMapping("/index")

    public String index(String returnUrl, ModelMap map) {

        map.put("returnUrl",returnUrl);

        return "index";
    }




    @ResponseBody
    @RequestMapping("/login")
    public String login(HttpServletResponse response, HttpServletRequest request, UserInfo userInfo) {


      UserInfo user =  userService.login(userInfo);

      if (user == null) {
          return "userName or passWord ERROR";
      } else  {
          //登录,颁发token 使用jwt技术
          //重定向到原始页面
          Map<String, String> stringStringHashMap = new HashMap<>();
          stringStringHashMap.put("userId",user.getId());
          stringStringHashMap.put("nikeName",user.getNickName());

          String token = JwtUtil.encode("atguigu0328", stringStringHashMap, getMyIp(request));

           //合并购物车数据
          String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
          List<CartInfo> cartInfoList = null;
          if (StringUtils.isNotBlank(cartListCookie)) {
             cartInfoList = JSON.parseArray(cartListCookie, CartInfo.class);
          }

           String useId = user.getId();
          cartService.combineCart(cartInfoList,useId);
             CookieUtil.setCookie(request,response,"cartListCookie","",0,true);
          //删除cookie中的购物车数据
          return token;
      }

    }





    @ResponseBody
    @RequestMapping("/verify")
    public String verify(String token,String salt) {

        //验证token
        Map<String,String> decode = null;
        try {
            decode = JwtUtil.decode("atguigu0328",token, salt);
        } catch (SignatureException e) {
            return "fail";
        }
        if (decode != null) {
           return "success";
       } else {
            return "fail";
       }

    }


    public String getMyIp(HttpServletRequest request) {

        String ip = "";
       ip = request.getHeader("x-forwarded-for");
          if (StringUtils.isBlank(ip)) {
               ip = request.getRemoteAddr();
          } if (StringUtils.isBlank(ip)) {
              ip = "127.0.0.1";
        }

        return ip;
    }
}
