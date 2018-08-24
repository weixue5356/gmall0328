package com.atguigu.gmall.manager.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.bean.SkuInfo;
import com.atguigu.service.SkuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class SkuController {



    @Reference
    private SkuService skuService;



    @ResponseBody
    @RequestMapping("/getSkuListBySpu")
    public List<SkuInfo> getSkuListBySpu(String spuId) {
        List<SkuInfo> skuInfoList = skuService.getSkuListBySpu(spuId);
        return skuInfoList;
    }

    //saveSku
    @ResponseBody
    @RequestMapping("/saveSku")
    public String saveSku(SkuInfo skuInfo) {
        skuService.saveSku(skuInfo);
        return "success";
    }
}
