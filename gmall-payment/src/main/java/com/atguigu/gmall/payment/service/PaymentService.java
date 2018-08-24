package com.atguigu.gmall.payment.service;

import com.atguigu.bean.PaymentInfo;

import java.util.Map;

public interface PaymentService {
    void savePament(PaymentInfo paymentInfo);

    void updatePayment(PaymentInfo paymentInfo);

    void sendPamentSuccessQueue(String tradeNo, String outTradeNo, String callbackContent);

    void sendpaymentcheckqueue(String outTradeNo, int i);

    Map<String,String> checkPayment(String outTradeNo);

    boolean checkPaied(String outTradeNo);
}
