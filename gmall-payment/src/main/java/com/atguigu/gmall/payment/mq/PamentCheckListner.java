package com.atguigu.gmall.payment.mq;


import com.atguigu.gmall.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Map;

@Component
public class PamentCheckListner {

    @Autowired
    PaymentService paymentService;


    @JmsListener(containerFactory = "jmsQueueListener",destination = "PAYMENT_CHECK_QUEUE")
    public void consumerPaymentSuccess(MapMessage mapMessage) throws JMSException {
        int count = mapMessage.getInt("count");
        String outTradeNo = mapMessage.getString("outTradeNo");


        // 检查支付状态
        Map<String, String> stringStringMap = paymentService.checkPayment(outTradeNo);

        String status = stringStringMap.get("status");
        String callbackContent = stringStringMap.get("callbackContent");
        String tradeNo = stringStringMap.get("tradeNo");

        if (status.equals("TRADE_SUCCESS") || status.equals("TRADE_CLOSED")) {
            // 幂等性检查
            boolean b = paymentService.checkPaied(outTradeNo);
            // 发送支付成功的队列
            if(!b){
                paymentService.sendPamentSuccessQueue(tradeNo,outTradeNo,callbackContent);
            }

        } else {
            if (count > 0) {
                System.out.println("监听到延迟检查队列，执行延迟检查第" + (6 - count) + "次检查");
                paymentService.sendpaymentcheckqueue(outTradeNo, (count - 1));
            } else {
                System.out.println("监听到延迟检查队列次数耗尽，结束检查");
            }
        }

    }
}
