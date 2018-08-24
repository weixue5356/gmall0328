package com.atguigu.gmall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.bean.OrderDetail;
import com.atguigu.bean.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.utils.ActiveMQUtil;
import com.atguigu.gmall.utils.RedisUtil;
import com.atguigu.service.OrderService;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.util.List;
import java.util.UUID;


@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private  OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;


    @Autowired
    private  RedisUtil redisUtil;

    @Autowired
    private ActiveMQUtil activeMQUtill;




    @Override
    public String generateTradeCode(String userId) {

        Jedis jedis = redisUtil.getJedis();

        String k = "tradeCode:" + userId + ":info";

        String v = UUID.randomUUID().toString();

        jedis.setex(k,60*30,v);

        jedis.close();

        return v;
    }

    @Override
    public boolean checkTradeCodeChe(String tradeCode,String userId) {
        boolean b = false;
        String k = "tradeCode:" + userId + ":info";
        Jedis jedis = redisUtil.getJedis();

        String s = jedis.get(k);

        if (StringUtils.isNotBlank(s) && s.equals(tradeCode)) {
            b = true;
           jedis.del(k);
        }

        return b;
    }

    @Override
    public String saveOrder(OrderInfo orderInfo) {

        orderInfoMapper.insertSelective(orderInfo);
        String OrderId = orderInfo.getId();

        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();

        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setOrderId(OrderId);

            orderDetailMapper.insertSelective(orderDetail);
        }
        return OrderId;
    }

    @Override
    public OrderInfo getOrderById(String orderId) {

        OrderInfo orderInfo1 = new OrderInfo();
        orderInfo1.setId(orderId);
        OrderInfo orderInfo = orderInfoMapper.selectByPrimaryKey(orderInfo1);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(orderId);
        List<OrderDetail> orderDetails = orderDetailMapper.select(orderDetail);

        orderInfo.setOrderDetailList(orderDetails);

        return orderInfo;
    }

    @Override
    public void updateOrderSatus(OrderInfo orderInfo) {
        Example example = new Example(OrderInfo.class);
        example.createCriteria().andEqualTo("outTradeNo",orderInfo.getOutTradeNo());
        orderInfoMapper.updateByExampleSelective(orderInfo,example);

    }

    @Override
    public void sendOrderResultQueue(String outTradeNo) {


        Connection connection = activeMQUtill.getConnection();
        try {
            connection.start();


        Session session = connection.createSession(true,Session.SESSION_TRANSACTED);

        Queue queue = session.createQueue("ORDER_RESULT_QUEUE");

        MessageProducer producer = session.createProducer(queue);
            ActiveMQTextMessage textMessage = new ActiveMQTextMessage();
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setOutTradeNo(outTradeNo);
             orderInfo = orderInfoMapper.selectOne(orderInfo);
//               //获得订单消息数据
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderInfo.getId());
            List<OrderDetail> orderDetails = orderDetailMapper.select(orderDetail);
            orderInfo.setOrderDetailList(orderDetails);

            // 将消息数据转化为json字符串文本输出
            textMessage.setText(JSON.toJSONString(orderInfo));

        producer.setDeliveryMode(DeliveryMode.PERSISTENT);

        producer.send(textMessage);


            session.commit();
            connection.close();

        }catch (JMSException e) {
            e.printStackTrace();
        }
        System.out.println("订单支付成功,发送消息到库存系统");
    }
}
