package com.atguigu.gmall.manage.mapper;

import com.atguigu.bean.SkuInfo;
import com.atguigu.bean.SpuSaleAttr;
import com.atguigu.bean.SpuSaleAttrValue;
import org.apache.ibatis.annotations.Param;
import org.junit.runners.Parameterized;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SpuSaleAttrValueMapper extends Mapper<SpuSaleAttrValue> {
    public List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(Map<String,String> map);

    public List<SkuInfo> selectSkuSaleAttrValueListBySpu(@Param("spuId") String spuId);

}
