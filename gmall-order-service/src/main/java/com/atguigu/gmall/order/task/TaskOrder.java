package com.atguigu.gmall.order.task;


import com.atguigu.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;

@Component
@EnableScheduling
public class TaskOrder {


    @Scheduled(cron = "0/10 * *  * * ?")
    public  void  deleteOrder () throws Exception{

        System.out.println("定时检查过期订单,进行逻辑删除,由orderService执行");

    }

}
