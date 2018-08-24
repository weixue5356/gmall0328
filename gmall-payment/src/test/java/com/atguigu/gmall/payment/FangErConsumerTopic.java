package com.atguigu.gmall.payment;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class FangErConsumerTopic {

    public static void main(String[] args) {
        ConnectionFactory connect = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, "tcp://192.168.252.135:61616");
        try {
            Connection connection = connect.createConnection();
            connection.start();
            //第一个值表示是否使用事务，如果选择true，第二个值相当于选择0
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic testqueue = session.createTopic("KAIHUI");

            MessageConsumer consumer = session.createConsumer(testqueue);
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    if (message instanceof TextMessage) {
                        try {
                            String text = ((TextMessage) message).getText();
                            System.out.println(text + "芳儿不要工资");

                            //session.rollback();
                        } catch (JMSException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            ;
        }
    }


}
