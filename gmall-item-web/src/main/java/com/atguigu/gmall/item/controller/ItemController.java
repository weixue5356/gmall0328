package com.atguigu.gmall.item.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.bean.SkuAttrValue;
import com.atguigu.bean.SkuInfo;
import com.atguigu.bean.SkuSaleAttrValue;
import com.atguigu.bean.SpuSaleAttr;
import com.atguigu.service.SkuService;
import com.atguigu.service.SpuService;
import jdk.nashorn.internal.ir.ReturnNode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {

    @Reference
    private SkuService skuService;

    @Reference
    private SpuService spuService;



    @RequestMapping("/{skuId}.html")
     public  String getSkuInfo(@PathVariable("skuId") String skuId, ModelMap map){
          //查询skuInfo和skuImageList
        SkuInfo skuInfo = skuService.getSkuInfoById(skuId);
        map.put("skuInfo",skuInfo);



        String spuId = skuInfo.getSpuId();
        //List<SpuSaleAttr> spuSaleAttrs = spuService.getSpuSaleAttrByspuId(spuId);

        //当前sku对应的其他兄弟的销售属性哈希对应关系
        List<SkuInfo> skuInfoList =  skuService.getSkuSaleAttrValueListBySpu(spuId);
        Map<String, String> hashMap = new HashMap<String,String>();
        for (SkuInfo skuInfo1 : skuInfoList) {
            String v = skuInfo.getId();
            List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo1.getSkuSaleAttrValueList();
            String k = "";
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                 k = k + "|" +skuSaleAttrValue.getSaleAttrValueId();

                hashMap.put(k,v);
            }
        }
        String skuJson = JSON.toJSONString(hashMap);
        map.put("skuJson",skuJson);

        //spu销售属性列表
        Map<String, String> mapParam = new HashMap<>();
        mapParam.put("spuId",spuId);
        mapParam.put("skuId",skuId);
        // spu的sku和销售属性对应关系的hash表
       List<SpuSaleAttr> spuSaleAttrList = spuService.selectSpuSaleAttrListCheckBySku(mapParam);
        map.put("spuSaleAttrListCheckBySku",spuSaleAttrList);
        return "item";


        //获取选中的sku的销售属性值




    }



    @RequestMapping("/index")
    public String index(){
        return "demo";
    }
}
