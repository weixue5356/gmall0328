package com.atguigu.gware.mq;

import com.alibaba.fastjson.JSON;

import com.atguigu.gware.bean.OrderDetail;
import com.atguigu.gware.bean.OrderInfo;
import com.atguigu.gware.bean.WareOrderTaskDetail;
import com.atguigu.gware.util.ActiveMQUtil;
import com.atguigu.gware.bean.WareOrderTask;
import com.atguigu.gware.enums.TaskStatus;
import com.atguigu.gware.mapper.WareOrderTaskDetailMapper;
import com.atguigu.gware.mapper.WareOrderTaskMapper;
import com.atguigu.gware.mapper.WareSkuMapper;
import com.atguigu.gware.service.GwareService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.TextMessage;
import java.util.*;

/**
 * @param
 * @return
 */
@Component
public class WareConsumer {


    @Autowired
    WareOrderTaskMapper wareOrderTaskMapper;

    @Autowired
    WareOrderTaskDetailMapper wareOrderTaskDetailMapper;

    @Autowired
    WareSkuMapper wareSkuMapper;

    @Autowired
    ActiveMQUtil activeMQUtil;

    @Autowired
    JmsTemplate jmsTemplate;


    @Autowired
    GwareService gwareService;



    @JmsListener(destination = "ORDER_RESULT_QUEUE",containerFactory = "jmsQueueListener")
    public void receiveOrder(TextMessage textMessage) throws JMSException {
        String orderTaskJson = textMessage.getText();


        /***
         * 转化并保存订单对象
         */
        OrderInfo orderInfo = JSON.parseObject(orderTaskJson, OrderInfo.class);
        WareOrderTask wareOrderTask = new WareOrderTask();
        wareOrderTask.setConsignee(orderInfo.getConsignee());
        wareOrderTask.setConsigneeTel(orderInfo.getConsigneeTel());
        wareOrderTask.setCreateTime(new Date());
        wareOrderTask.setDeliveryAddress(orderInfo.getDeliveryAddress());
        wareOrderTask.setOrderId(orderInfo.getId());
        ArrayList<WareOrderTaskDetail> wareOrderTaskDetails = new ArrayList<>();
        List<OrderDetail> orderDetailList =
                orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            WareOrderTaskDetail wareOrderTaskDetail = new WareOrderTaskDetail();

            wareOrderTaskDetail.setSkuId(orderDetail.getSkuId());
            wareOrderTaskDetail.setSkuName(orderDetail.getSkuName());
            wareOrderTaskDetail.setSkuNum(orderDetail.getSkuNum());
            wareOrderTaskDetails.add(wareOrderTaskDetail);

        }
        wareOrderTask.setDetails(wareOrderTaskDetails);
        wareOrderTask.setTaskStatus(TaskStatus.PAID);
        gwareService.saveWareOrderTask(wareOrderTask);

        textMessage.acknowledge();

       //c拆单
        List<WareOrderTask> wareSubOrderTaskList = gwareService.checkOrderSplit(wareOrderTask);
        if (wareSubOrderTaskList != null && wareSubOrderTaskList.size() >= 2) {
            for (WareOrderTask orderTask : wareSubOrderTaskList) {
                gwareService.lockStock(orderTask);
            }
        } else {
            gwareService.lockStock(wareOrderTask);
        }


    }


}
