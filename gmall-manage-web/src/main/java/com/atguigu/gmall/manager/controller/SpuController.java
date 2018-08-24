package com.atguigu.gmall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.bean.BaseSaleAttr;
import com.atguigu.bean.SpuImage;
import com.atguigu.bean.SpuInfo;
import com.atguigu.bean.SpuSaleAttr;
import com.atguigu.gmall.manager.controller.utils.MyUploadUtils;
import com.atguigu.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class SpuController {

    @Reference
    private SpuService spuService;

    //spuList
    @ResponseBody
    @RequestMapping("/spuList")
    public List<SpuInfo> spuList(String catalog3Id){
        List<SpuInfo> spuInfoList = spuService.getSpuList(catalog3Id);
        return spuInfoList;
    }


    //baseSaleAttrList
    @ResponseBody
    @RequestMapping("/baseSaleAttrList")
    public List<BaseSaleAttr> baseSaleAttrList(){
        List<BaseSaleAttr> baseSaleAttrList = spuService.getBaseSaleAttrList();
        return baseSaleAttrList;
    }



    //saveSpu
    @ResponseBody
    @RequestMapping("/saveSpu")
    public String saveSpu(SpuInfo spuInfo){
        spuService.saveSpu(spuInfo);
        return "success";
    }


    //fileUpload
    @ResponseBody
    @RequestMapping("/fileUpload")
    public String fileUpload(@RequestParam("file") MultipartFile file){
       String uploadImg = MyUploadUtils.uploadImg(file);
         return uploadImg;
    }


    //getSpuSaleAttrByspuId
    @ResponseBody
    @RequestMapping("/getSpuSaleAttrByspuId")
    public List<SpuSaleAttr> getSpuSaleAttrByspuId(String spuId){
        List<SpuSaleAttr> spuSaleAttrList = spuService.getSpuSaleAttrByspuId(spuId);
        return spuSaleAttrList;
    }


    //getSpuImageListBySpuId
    @ResponseBody
    @RequestMapping("/getSpuImageListBySpuId")
    public List<SpuImage> getSpuImageListBySpuId(String spuId){
        List<SpuImage> spuImages = spuService.getSpuImageListBySpuId(spuId);
        return spuImages;
    }


}
