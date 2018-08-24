package com.atguigu.gmall.order.mq;


import com.atguigu.bean.OrderInfo;
import com.atguigu.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.remoting.jaxws.JaxWsSoapFaultException;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Calendar;

@Component
public class OrderListner {
@Autowired
    private OrderService orderService;

    @JmsListener(containerFactory = "jmsQueueListener",destination = "PAMENT_SUCCESS_QUEUE")
    public  void consumePaymentSuccess(MapMessage mapMessage) throws JMSException{
        String trackingNo = mapMessage.getString("trackingNo");
        String outTradeNo = mapMessage.getString("outTradeNo");

        System.out.println("监听到支付成功的消息trackingNo:" + trackingNo + "outTradeNo:" + outTradeNo);


         //修改订单状态,发送消息给库存服务
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOutTradeNo(outTradeNo);
        orderInfo.setTrackingNo(trackingNo);
        orderInfo.setProcessStatus("准备出库中");
        orderInfo.setOrderStatus("订单已支付");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE,2);
        orderInfo.setExpireTime(c.getTime());


        orderService.sendOrderResultQueue(orderInfo.getOutTradeNo());
        orderService.updateOrderSatus(orderInfo);



    }


}
