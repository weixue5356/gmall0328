package com.atguigu.gmall.interceptor;

import com.atguigu.gmall.annotation.LoginRequire;
import com.atguigu.gmall.util.CookieUtil;
import com.atguigu.gmall.util.HttpClientUtil;
import com.atguigu.gmall.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod method = (HandlerMethod)handler;
        LoginRequire methodAnnotation = method.getMethodAnnotation(LoginRequire.class);
        //无需携带token 游客页面
        if (methodAnnotation == null) {
            return true;
        }

        //没有token
        //新token为空 旧token不为空 已经登陆过
        //新token不为空 旧token为空 第一次登录
        //新token为空 旧token为空 没有token让客户去登录页面 已经判断了
        //新token不为空 旧token不为空 登录过期

        String token = "";
        String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
        String newToken = request.getParameter("newToken");

      if (StringUtils.isBlank(newToken) && StringUtils.isNotBlank(oldToken)) {
          //已经登录过

      }

        if (StringUtils.isNotBlank(newToken) && StringUtils.isBlank(oldToken)) {
            //第一次登录
            token = newToken;
        }
        if (StringUtils.isNotBlank(newToken) && StringUtils.isNotBlank(oldToken)) {
            //登录过期
            token = newToken;
        }


        //必须携带token 结算页面之后的页面
        //访问订单模块时没有token
        if (methodAnnotation.ifNeedSuccess() && StringUtils.isBlank(token)){

            StringBuffer returnUrl = request.getRequestURL();
       //https://passport.jd.com/new/login.aspx?ReturnUrl=https%3A%2F%2Fcart.jd.com%2Fcart.action
            response.sendRedirect("http://passport.gmall.com:8085/index?returnUrl=" + returnUrl);
            return false;
        }


        String success = "";

        //有token,需要验证,远程访问posstport验证token,用户已经登录过了
        if (StringUtils.isNotBlank(token)) {
            success = HttpClientUtil.doGet("http://passport.gmall.com:8085/verify?token=" + token + "&salt=" + getMyIp(request));
        }

        //订单页面
        if (!success.equals("success") && methodAnnotation.ifNeedSuccess()) {

            response.sendRedirect("http://passport.gmall.com:8085/index");
            return false;
        }



        //购物车页面
        if (!success.equals("success") && !methodAnnotation.ifNeedSuccess()) {
            return true;
        }

        if (success.equals("success")) {
            Map decode = JwtUtil.decode("atguigu0328", token, getMyIp(request));
            request.setAttribute("userId",decode.get("userId"));
            request.setAttribute("nickName",decode.get("nikeName"));

        }

        return true;
    }



    public String getMyIp(HttpServletRequest request) {

        String ip = "";
        ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isBlank(ip)) {
            ip = request.getRemoteAddr();
        } if (StringUtils.isBlank(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }

}
