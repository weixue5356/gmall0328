package com.atguigu.service;

import com.atguigu.bean.OrderInfo;

public interface OrderService {
    String generateTradeCode(String userId);

    boolean checkTradeCodeChe(String tradeCode,String uerId);

    String saveOrder(OrderInfo orderInfo);

    OrderInfo getOrderById(String orderId);

    void updateOrderSatus(OrderInfo orderInfo);

    void sendOrderResultQueue(String outTradeNo);
}
