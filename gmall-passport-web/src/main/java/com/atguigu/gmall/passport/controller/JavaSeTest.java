package com.atguigu.gmall.passport.controller;

import javax.xml.ws.soap.Addressing;
import java.math.BigDecimal;

public class JavaSeTest {


    static final int location = 10;

    public static void main(String[] args) {
        JavaSeTest b = new JavaSeTest();
        System.out.println("两个数字相加结果是：" + b.add(-7.5, 8.9));
        System.out.println("两个数字相减结果是：" + b.sub(-7.5, 8.9));
        System.out.println("两个数字相乘结果是：" + b.mul(-7.5, 8.9));
        System.out.println("两个数字相除，小数点后保留10位，结果是：" + b.div(-7.5, 8.9));
        System.out.println("两个数字相除，小数点后保留5位，结果是：" + b.div(-7.5, 8.9, 5));
    }



    public BigDecimal add(double value1, double value2){
        //      BigDecimal b1=new BigDecimal(Double.toString(value1)); //Double.toString(value1)返回的String类的value1,原因见下。
        //    BigDecimal b2=new BigDecimal(Double.toString(value2));
        BigDecimal b1=new BigDecimal(value1);
        BigDecimal b2=new BigDecimal(value2);
        return b1.add(b2);
    }
    public BigDecimal sub(double value1,double value2){
        BigDecimal b1=new BigDecimal(Double.toString(value1));
        BigDecimal b2=new BigDecimal(Double.toString(value2));
        return b1.subtract(b2);
    }
    public BigDecimal mul(double value1,double value2){
        BigDecimal b1=new BigDecimal(Double.toString(value1));
        BigDecimal b2=new BigDecimal(Double.toString(value2));
        return b1.multiply(b2);
    }
    public BigDecimal div(double value1,double value2){
        return div(value1,value2,location);
    }
    public BigDecimal div(double value1,double value2,int b){
        if(b<0){
            System.out.println("b必须大于等于0");
        }
        BigDecimal b1=new BigDecimal(Double.toString(value1));
        BigDecimal b2=new BigDecimal(Double.toString(value2));
        return b1.divide(b2,b,BigDecimal.ROUND_HALF_UP);
    }
}


