package com.atguigu.service;

import com.atguigu.bean.SkuInfo;

import java.math.BigDecimal;
import java.util.List;

public interface SkuService {
    List<SkuInfo> getSkuListBySpu(String spuId);

    void saveSku(SkuInfo skuInfo);

    SkuInfo getSkuInfoById(String skuId);

    List<SkuInfo> getSkuSaleAttrValueListBySpu(String spuId);

    List<SkuInfo> getSkuListByCatalog3Id(String catalog3Id);

    boolean checkPrice(String skuId, BigDecimal skuPrice);
}
