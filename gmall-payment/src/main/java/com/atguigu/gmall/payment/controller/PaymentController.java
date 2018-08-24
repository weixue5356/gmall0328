package com.atguigu.gmall.payment.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.bean.OrderInfo;
import com.atguigu.bean.PaymentInfo;
import com.atguigu.gmall.annotation.LoginRequire;
import com.atguigu.gmall.payment.config.AlipayConfig;
import com.atguigu.gmall.payment.service.PaymentService;
import com.atguigu.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PaymentController {

   @Reference
   private OrderService orderService;

   @Autowired
   private PaymentService paymentService;

   @Autowired
   private AlipayClient alipayClient;





   @LoginRequire(ifNeedSuccess = true)
   //alipay/callback/return
   @RequestMapping("/alipay/callback/return")
   public String callbackReturn(HttpServletRequest request, String orderId, ModelMap map) {

        Map<String, String> paramsMap = new HashMap<String,String>();
        boolean signVerified = true; //调用SDK验证签名
        try {
            signVerified = AlipaySignature.rsaCheckV1(paramsMap, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type);
        } catch (Exception e) {
            System.out.println("支付宝验证成功");
        }

        if(signVerified){
            // TODO 验签成功后，按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验，校验成功后在response中返回success并继续商户自身业务处理，校验失败返回failure
            String callbackContent = request.getQueryString();
            String outTradeNo = request.getParameter("out_trade_no");
            String tradeNo =request.getParameter("trade_no");
            String totalAamount = request.getParameter("total_amount");
         //幂等性判断
          boolean b  = paymentService.checkPaied(outTradeNo);

          if (!b) {
              //发送消息支付成功的消息给订单服务
              paymentService.sendPamentSuccessQueue(tradeNo,outTradeNo,callbackContent);
          }


        }else{
            // TODO 验签失败则记录异常日志，并在response中返回failure.
        }

       return "testSuccess";

   }


    //alipay/submit
    //mx/submit

    /**
     * 阿里支付
     * @param orderId
     * @param map
     * @return
     */
    @LoginRequire(ifNeedSuccess = true)
   @RequestMapping("/alipay/submit")
    @ResponseBody
    public String alipay (String orderId, ModelMap map) {
       OrderInfo orderInfo = orderService.getOrderById(orderId);

       //保存支付信息
       PaymentInfo paymentInfo = new PaymentInfo();

       paymentInfo.setCreateTime(new Date());
       paymentInfo.setOrderId(orderId);
       paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
       paymentInfo.setPaymentStatus("未支付");
       paymentInfo.setSubject(orderInfo.getOrderDetailList().get(0).getSkuName());
       paymentInfo.setTotalAmount(orderInfo.getTotalAmount());

       paymentService.savePament(paymentInfo);

       AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();//创建API对应的request
       alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
       alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);//在公共参数中设置回跳和通知地址

       HashMap<String, Object> stringStringHashMap = new HashMap<String,Object>();
       stringStringHashMap.put("out_trade_no",orderInfo.getOutTradeNo());
       stringStringHashMap.put("product_code","FAST_INSTANT_TRADE_PAY");
       stringStringHashMap.put("total_amount","0.01");
       stringStringHashMap.put("subject","测试手机主题");

       String json = JSON.toJSONString(stringStringHashMap);

       alipayRequest.setBizContent(json);//填充业务参数
       String form="";
       try {
           form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
       } catch (AlipayApiException e) {
           e.printStackTrace();
       }



       System.out.println("设置一个定时巡检订单"+paymentInfo.getOutTradeNo()+"的支付状态的延迟队列");
        paymentService.sendpaymentcheckqueue(paymentInfo.getOutTradeNo(),5);
        return form;

    }

    /**
     * 微信支付
     * @param orderId
     * @param map
     * @return
     */
    @LoginRequire(ifNeedSuccess = true)
    @ResponseBody
    @RequestMapping("/mx/submit")
    public String mx (String orderId, ModelMap map) {
        return "微信支付快扫我吧!哈哈哈!";

    }


    /**
     * 返回支付首页
     * @param orderId
     * @param map
     * @return
     */
    @LoginRequire(ifNeedSuccess = true)
    @RequestMapping("/index")
    public String index (String orderId, ModelMap map) {

       OrderInfo orderInfo = orderService.getOrderById(orderId);
        String outTradeNo = orderInfo.getOutTradeNo();
        map.put("orderId",orderId);
        map.put("outTradeNo",outTradeNo);
        map.put("totalAmount", orderInfo.getTotalAmount());

        return "index";
    }



}
