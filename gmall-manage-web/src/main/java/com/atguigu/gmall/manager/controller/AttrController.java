package com.atguigu.gmall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.bean.BaseAttrInfo;
import com.atguigu.bean.BaseAttrValue;
import com.atguigu.service.BaseAttrInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
public class AttrController {

   @Reference
    private BaseAttrInfoService baseAttrInfoService;




    //getAttrList
    @ResponseBody
    @RequestMapping("/getAttrList")
    public List<BaseAttrInfo> getAttrList(String catalog3Id){
        List<BaseAttrInfo> attributeInfoList= baseAttrInfoService.getAttrList(catalog3Id);
        return  attributeInfoList;
    }


    //saveAttr
    @ResponseBody
    @RequestMapping("/saveAttr")
    public String saveAttr(BaseAttrInfo baseAttrInfo){
        baseAttrInfoService.saveAttr(baseAttrInfo);
        return  "success";
    }

    //attrValueList

    @ResponseBody
    @RequestMapping("/getAttrValueList")
    public  List<BaseAttrValue> getAttrValueList(String attrId){
        List<BaseAttrValue> baseAttrValuelist =  baseAttrInfoService.getAttrValueList(attrId);
           return  baseAttrValuelist;
    }


    //updateAttrValueList
    @ResponseBody
    @RequestMapping("/updateAttrValueList")
    public String updateAttrValueList(BaseAttrInfo baseAttrInfo){
        baseAttrInfoService.updateAttrValueList(baseAttrInfo);
      return "success";
    }



    //deleteAttrById
    @ResponseBody
    @RequestMapping("/deleteAttrById")
    public void deleteAttrById(String id){
        baseAttrInfoService.deleteAttrById(id);

    }


    //getAttrListBycatalog3Id
    @ResponseBody
    @RequestMapping("/getAttrListBycatalog3Id")
    public List<BaseAttrInfo> getAttrListBycatalog3Id(String catalog3Id){
        List<BaseAttrInfo> baseAttrInfos = baseAttrInfoService.getAttrListBycatalog3Id(catalog3Id);
        return baseAttrInfos;


    }
}
