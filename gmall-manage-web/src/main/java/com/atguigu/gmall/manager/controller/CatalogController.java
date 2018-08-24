package com.atguigu.gmall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.bean.BaseCatalog1;
import com.atguigu.bean.BaseCatalog2;
import com.atguigu.bean.BaseCatalog3;
import com.atguigu.service.BaseCatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class CatalogController {

    @Reference
    private BaseCatalogService baseCatalogService;



    //getCatalog1
    @ResponseBody
    @RequestMapping("/getCatalog1")
    public List<BaseCatalog1> getCatalog1(){
        List<BaseCatalog1> baseCatalog1List = baseCatalogService.getCatalog1();
        return baseCatalog1List;
    }


    //getCatalog2
    @ResponseBody
    @RequestMapping("/getCatalog2")
    public List<BaseCatalog2> getCatalog2(String catalog1Id){
        List<BaseCatalog2> baseCatalog2List =  baseCatalogService.getCatalog2(catalog1Id);
             return  baseCatalog2List;
    }

    //getCatalog3
    @ResponseBody
    @RequestMapping("/getCatalog3")
    public List<BaseCatalog3> getCatalog3(String catalog2Id){
        List<BaseCatalog3> baseCatalog3List = baseCatalogService.getCatalog3(catalog2Id);
        return  baseCatalog3List;
    }

}
