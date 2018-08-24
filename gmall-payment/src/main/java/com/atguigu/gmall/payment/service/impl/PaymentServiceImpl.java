package com.atguigu.gmall.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.atguigu.bean.PaymentInfo;
import com.atguigu.gmall.payment.mapper.PaymentInfoMapper;
import com.atguigu.gmall.payment.service.PaymentService;
import com.atguigu.gmall.utils.ActiveMQUtil;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService{


    @Autowired
    private PaymentInfoMapper payMentInfoMapper;


    @Autowired
    private ActiveMQUtil activeMQUtil;


    @Autowired
    private AlipayClient alipayClient;


    @Override
    public void savePament(PaymentInfo paymentInfo) {
        payMentInfoMapper.insertSelective(paymentInfo);
    }

    @Override
    public void updatePayment(PaymentInfo paymentInfo) {
        Example example = new Example(PaymentInfo.class);
        example.createCriteria().andEqualTo("outTradeNo",paymentInfo.getOutTradeNo());
        payMentInfoMapper.updateByExampleSelective(paymentInfo,example);

    }


    /**
     * 发送支付成功的消息
     * @param tradeNo
     * @param outTradeNo
     * @param callbackContent
     */
    @Override
    public void sendPamentSuccessQueue(String tradeNo, String outTradeNo, String callbackContent) {
        // 修改支付信息
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPaymentStatus("已支付");
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setOutTradeNo(outTradeNo);
        paymentInfo.setCallbackContent(callbackContent);
        paymentInfo.setAlipayTradeNo(tradeNo);
        updatePayment(paymentInfo);

        try {
        //  建立mq的连接
        Connection connection = activeMQUtil.getConnection();
            connection.start();
            // 通过连接创建一次于mq的回话任务
            //第一个值表示是否使用事务，如果选择true，第二个值相当于选择0
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue queue = session.createQueue("PAMENT_SUCCESS_QUEUE");

            // 通过mq的回话任务将队列消息发送出去
            MessageProducer producer = session.createProducer(queue);

            ActiveMQMapMessage mqMapMessage = new ActiveMQMapMessage();
            mqMapMessage.setString("trackingNo",tradeNo);
            mqMapMessage.setString("outTradeNo",outTradeNo);
            //设置一个持久化的消息queue,把消息放松出去

            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(mqMapMessage);

            //提交任务
            session.commit();
            //关闭连接
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
        System.out.println("支付成功发送支付服务的消息队列!");

    }

    /**
     * 设置延迟检查的消息队列,由自身消费
     * @param outTradeNo
     * @param count
     */
    @Override
    public void sendpaymentcheckqueue(String outTradeNo, int count) {


        try {
            Connection connection = activeMQUtil.getConnection();
            connection.start();

            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue queue = session.createQueue("PAYMENT_CHECK_QUEUE");
            MessageProducer producer = session.createProducer(queue);

            MapMessage mqMapMessage = new ActiveMQMapMessage();
            mqMapMessage.setString("outTradeNo",outTradeNo);
            mqMapMessage.setInt("count",count);
            //设置延时时间

            mqMapMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY,1000*10);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(mqMapMessage);

            session.commit();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("发送第" + (6-count) + "次延时队列消息");

    }


    /**
     * 检查用户支付状态
     * @param outTradeNo
     * @return
     */
    @Override
    public Map<String, String> checkPayment(String outTradeNo) {

        HashMap<String, String> stringStringHashMap = new HashMap<>();

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        HashMap<String, Object> stringObjectHashMap = new HashMap<String, Object>();

        stringObjectHashMap.put("out_trade_no",outTradeNo);

        String json = JSON.toJSONString(stringObjectHashMap);
        request.setBizContent(json);
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if(response.isSuccess()){

            String tradeNo = response.getTradeNo();
            String status = response.getTradeStatus();
            String callbackContent = response.getBody();
            stringStringHashMap.put("tradeNo",tradeNo);
            stringStringHashMap.put("status",status);
            stringStringHashMap.put("callbackContent",callbackContent);

            System.out.println("调用成功");
        } else {
            System.out.println("用户未扫码");
        }
        return stringStringHashMap;
    }

    @Override
    public boolean checkPaied(String outTradeNo) {

        boolean b = false;
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(outTradeNo);
        PaymentInfo paymentInfo1 = payMentInfoMapper.selectOne(paymentInfo);

        if (paymentInfo1 != null && paymentInfo1.getPaymentStatus().equals("已支付")) {
            b = true;
        }
        return b;
    }
}
