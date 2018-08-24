package com.atguigu.gmall.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.bean.CartInfo;
import com.atguigu.bean.SkuInfo;
import com.atguigu.gmall.annotation.LoginRequire;
import com.atguigu.gmall.util.CookieUtil;
import com.atguigu.service.CartService;
import com.atguigu.service.SkuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.Id;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {

@Reference
private SkuService skuService;


@Reference
private CartService cartService;



    //toTrade
    @LoginRequire(ifNeedSuccess = false)
    @RequestMapping("/carSuccess")
    public String carSuccess() {
        return "success";
    }


    //checkCart
    @LoginRequire(ifNeedSuccess = false)
    @RequestMapping("/checkCart")
    public String checkCart(CartInfo cartInfo,HttpServletRequest request,HttpServletResponse response,ModelMap map) {

        List<CartInfo> cartInfos = new ArrayList<>();
        //String userId = "2";
        String userId =(String)request.getAttribute("userId");
        //修改购物车的选中状态


        if (StringUtils.isBlank(userId)) {
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if (StringUtils.isNotBlank(cartListCookie)) {
                cartInfos = JSON.parseArray(cartListCookie, CartInfo.class);
                //浏览器的cookie缓存
                for (CartInfo info : cartInfos) {
                    if (info.getSkuId().equals(cartInfo.getSkuId())) {
                        //如果之前cookie中的skuId和现在加入的skuId相等就把cookie中的选中状态设为当前的选中状态
                        info.setIsChecked(cartInfo.getIsChecked());
                    }
                }

            }
            CookieUtil.setCookie(request,response,"cartListCookie",
                    JSON.toJSONString(cartInfos),60*60*22*7,true);

        } else {

            //更新db和缓存
            cartInfo.setUserId(userId);
            cartService.updateCartCheked(cartInfo);
            //从缓存中取数据
            cartInfos = cartService.getCartCache(userId);
        }
        map.put("cartList",cartInfos);
        map.put("totalPrice",getTotalPrice(cartInfos));
        return "cartListInner";
    }




     //cartList
     @LoginRequire(ifNeedSuccess = false)
     @RequestMapping("/cartList")
     public String cartList(HttpServletRequest request,ModelMap map) {
         List<CartInfo> cartInfos = new ArrayList<>();
         String userId =(String)request.getAttribute("userId");
        if (StringUtils.isBlank(userId)) {
            //从cookie中取数据
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            cartInfos = JSON.parseArray(cartListCookie, CartInfo.class);

        } else {
            //从缓存中取数据
            cartInfos = cartService.getCartCache(userId);
        }
         map.put("cartList",cartInfos);
         map.put("totalPrice",getTotalPrice(cartInfos));
         return "cartList";
     }


     //总价
    private BigDecimal getTotalPrice(List<CartInfo> cartInfos) {

        BigDecimal bigDecimal = new BigDecimal("0");
        for (CartInfo cartInfo : cartInfos) {
            if (cartInfo.getIsChecked().equals("1")) {
                bigDecimal = bigDecimal.add(cartInfo.getCartPrice());
            }
        }
        return bigDecimal;
    }




    //addToCart
    @LoginRequire(ifNeedSuccess = false)
    @RequestMapping("/addToCart")
    public String addToCart(HttpServletRequest request, HttpServletResponse response, CartInfo cartInfo) {
        String id = cartInfo.getSkuId();
        SkuInfo sku = skuService.getSkuInfoById(id);

        cartInfo.setCartPrice(sku.getPrice().multiply(new BigDecimal(cartInfo.getSkuNum())));
        cartInfo.setIsChecked("1");
        cartInfo.setImgUrl(sku.getSkuDefaultImg());
        cartInfo.setSkuPrice(sku.getPrice());
        cartInfo.setSkuName(sku.getSkuName());


        String userId =(String)request.getAttribute("userId");
        List<CartInfo> cartInfos = new ArrayList<CartInfo>();

        if (StringUtils.isBlank(userId)) {
            //用户未登录操作cookie
            String cartListCookieStr = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if (StringUtils.isBlank(cartListCookieStr)) {
                cartInfos.add(cartInfo);
            } else {
                cartInfos = JSON.parseArray(cartListCookieStr, CartInfo.class);
                boolean b = ifNewCart(cartInfos,cartInfo);
                if (b) {
                   //添加
                    cartInfos.add(cartInfo);
                } else {
                    //修改
                    for (CartInfo info : cartInfos) {
                        String skuId = info.getSkuId();
                        if (skuId.equals(cartInfo.getSkuId())) {
                            info.setSkuNum(info.getSkuNum() + cartInfo.getSkuNum());
                            info.setCartPrice(info.getSkuPrice().multiply(new BigDecimal(info.getSkuNum())));
                        }
                    }
                }
            }
                //操作完成后覆盖cookie
                CookieUtil.setCookie(request,response,"cartListCookie",JSON.toJSONString(cartInfos),60*60*24*7,true);



        } else  {
            //用户已登录操作db
            String skuId = cartInfo.getSkuId();
            cartInfo.setUserId(userId);
            CartInfo cartInfoDb =  cartService.ifCartExist(cartInfo);

           if (cartInfoDb != null) {
               //更新数据库
               cartInfoDb.setSkuNum(cartInfoDb.getSkuNum() + cartInfo.getSkuNum());
               cartInfoDb.setCartPrice(cartInfoDb.getSkuPrice().multiply(new BigDecimal(cartInfoDb.getSkuNum())));
               cartService.updateCart(cartInfoDb);
           } else {
               //插入数据库
               cartService.saveCart(cartInfo);// skuInfo:skuId:info cart:userId:info (hash)
           }

           //同步缓存
            cartService.syncCache(userId);
        }



        return  "redirect:carSuccess";
    }

    private boolean ifNewCart(List<CartInfo> cartInfos, CartInfo cartInfo) {

        boolean b = true;

        for (CartInfo info : cartInfos) {
            String skuId = info.getSkuId();
            if (skuId.equals(cartInfo.getSkuId())) {
              b = false;
            }
        }

        return  b;
    }




}
