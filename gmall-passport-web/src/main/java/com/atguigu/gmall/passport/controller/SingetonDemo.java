package com.atguigu.gmall.passport.controller;

public class SingetonDemo {

    private SingetonDemo () {
        System.out.println(Thread.currentThread().getName() + "构造器被访问了！");
    }

    private  static SingetonDemo singetonDemo = null;

     private  static synchronized SingetonDemo getInstance() {
         if (singetonDemo == null) {
             singetonDemo = new SingetonDemo();
         }
         return  singetonDemo;
     }

    public static void main(String[] args) {
//        System.out.println(SingetonDemo.getInstance() == singetonDemo.getInstance());
//        System.out.println(SingetonDemo.getInstance() == singetonDemo.getInstance());
//        System.out.println(SingetonDemo.getInstance() == singetonDemo.getInstance());

        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                SingetonDemo.getInstance();
            },String.valueOf(i)).start();
        }

    }


}
