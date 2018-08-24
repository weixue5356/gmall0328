package com.atguigu.service;

import com.atguigu.bean.BaseSaleAttr;
import com.atguigu.bean.SpuImage;
import com.atguigu.bean.SpuInfo;
import com.atguigu.bean.SpuSaleAttr;
import com.sun.org.apache.xpath.internal.SourceTree;

import java.util.List;
import java.util.Map;

public interface SpuService {
    List<SpuInfo> getSpuList(String catalog3Id);

    List<BaseSaleAttr> getBaseSaleAttrList();

    void saveSpu(SpuInfo spuInfo);

    List<SpuSaleAttr> getSpuSaleAttrByspuId(String spuId);


    List<SpuImage> getSpuImageListBySpuId(String spuId);

    List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(Map<String, String> mapParam);
}
