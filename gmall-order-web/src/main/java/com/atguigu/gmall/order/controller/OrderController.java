package com.atguigu.gmall.order.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.bean.CartInfo;
import com.atguigu.bean.OrderDetail;
import com.atguigu.bean.OrderInfo;
import com.atguigu.bean.UserAddress;
import com.atguigu.bean.enums.PaymentWay;
import com.atguigu.gmall.annotation.LoginRequire;
import com.atguigu.service.CartService;
import com.atguigu.service.OrderService;
import com.atguigu.service.SkuService;
import com.atguigu.service.UserService;
import org.apache.tomcat.util.security.PrivilegedSetTccl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
public class OrderController {

   @Reference
    private UserService userService;

   @Reference
   private CartService cartService;

   @Reference
   private OrderService orderService;

   @Reference
   private SkuService skuService;



   //submitOrder
   @LoginRequire(ifNeedSuccess = true)
   @RequestMapping("/submitOrder")
   public String submitOrder(String tradeCode,HttpServletRequest request, ModelMap map) {
       String userId =(String)request.getAttribute("userId");
       //检查交易码,查询是否一致如果一致返回订单页面,如果不一致调回失败页面
       boolean bTradeCode = orderService.checkTradeCodeChe(tradeCode,userId);

    if(bTradeCode) {
      //执行订单提交业务

        //获取购物车中被选中的商品数据
        List<CartInfo> cartInfoList = cartService.getCartCacheByCheckd(userId);
        OrderInfo orderInfo = new OrderInfo();

        ArrayList<OrderDetail> orderDetails = new ArrayList<>();

        //生成订单信息
        //验价验库存
        for (CartInfo cartInfo : cartInfoList) {

            String skuId = cartInfo.getSkuId();
            BigDecimal skuPrice = cartInfo.getSkuPrice();
           boolean bprice = skuService.checkPrice(skuId,skuPrice);
           //验库存

           if (bprice) {
               OrderDetail orderDetail = new OrderDetail();
               orderDetail.setSkuId(cartInfo.getSkuId());
               orderDetail.setSkuNum(cartInfo.getSkuNum());
               orderDetail.setSkuName(cartInfo.getSkuName());
               orderDetail.setImgUrl(cartInfo.getImgUrl());
               orderDetail.setOrderPrice(cartInfo.getCartPrice());
               orderDetails.add(orderDetail);
           } else {
               map.put("errMsg","亲!订单中的商品库存有变,请重新去提交");
               return "tradeFail";
           }

        }

        orderInfo.setOrderDetailList(orderDetails);

        //封装订单信息
        String consignee = "测试收件人";
        orderInfo.setConsignee(consignee);

        String consigneeTel = "18570275518";
        orderInfo.setConsigneeTel(consigneeTel);
        orderInfo.setCreateTime(new Date());
        String address = "测试收件人地址";
        orderInfo.setDeliveryAddress(address);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE,1);
        orderInfo.setExpireTime(c.getTime());
        String comment = "测试手机评论描述";
        orderInfo.setOrderComment(comment);
        orderInfo.setOrderStatus("订单未支付");
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
        String format = sdf.format(new Date());
        String outTradeCode = "ATGUIGU" + format + System.currentTimeMillis();
        orderInfo.setOutTradeNo(outTradeCode);
        orderInfo.setPaymentWay(PaymentWay.ONLINE);
        orderInfo.setProcessStatus("未支付");
        orderInfo.setUserId(userId);
        orderInfo.setTotalAmount(getTotalPrice(cartInfoList));

        String orderId = orderService.saveOrder(orderInfo);

       //删除购物车数据 //同步缓存
        cartService.deleteCartById(cartInfoList);


        //对接支付接口
        return "redirect:http://payment.gmall.com:8087/index?orderId=" + orderId;


    } else {
        map.put("errMsg","亲!订单提交失败了啦,赶快去重新提交吧");
        return "tradeFail";
    }

   }




    /**
     * 去结算,需要查询用户信息
     * 将购物车对象转化为订单对象
     * @param request
     * @param map
     * @return
     */
    @LoginRequire(ifNeedSuccess = true)
    @RequestMapping("/toTrade")
    public String toTrade(HttpServletRequest request, ModelMap map) {
        String userId =(String)request.getAttribute("userId");
        //查询用户地址
        List<UserAddress> userAddressList =  userService.getAddressList(userId);
        //根据缓存查询购物车数据
        List<CartInfo>  cartInfoList = cartService.getCartCacheByCheckd(userId);

        ArrayList<OrderDetail> orderDetailList = new ArrayList<>();
           //把购物车数据全放进订单里面
        for (CartInfo cartInfo : cartInfoList) {

            OrderDetail orderDetail = new OrderDetail();

            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setOrderPrice(cartInfo.getCartPrice());
            orderDetail.setSkuName(cartInfo.getSkuName());
            orderDetail.setSkuId(cartInfo.getSkuId());

            orderDetailList.add(orderDetail);
        }
        //在页面结算之前设置一个tradeCode交易码,同时存放在Redis和结算页面
        String tradeCode = orderService.generateTradeCode(userId);

        map.put("tradeCode",tradeCode);
        map.put("userAddressList",userAddressList);
        map.put("orderDetailList",orderDetailList);
        map.put("totalAmount", getTotalPrice(cartInfoList));



        return "trade";
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




}
