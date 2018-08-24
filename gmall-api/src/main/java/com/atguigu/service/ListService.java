package com.atguigu.service;

import com.atguigu.bean.SkuLsInfo;
import com.atguigu.bean.SkuLsParam;

import java.util.List;

public interface ListService {
    public List<SkuLsInfo> search(SkuLsParam skuLsParam);

}
