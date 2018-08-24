package com.atguigu.service;

import com.atguigu.bean.BaseAttrInfo;
import com.atguigu.bean.BaseAttrValue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface BaseAttrInfoService {


    List<BaseAttrInfo> getAttrList(String catalog3Id);

    void saveAttr(BaseAttrInfo baseAttrInfo);

    List<BaseAttrValue> getAttrValueList(String attrId);

    void deleteAttrById(String id);

    void updateAttrValueList(BaseAttrInfo baseAttrInfo);

    List<BaseAttrInfo> getAttrListBycatalog3Id(String catalog3Id);

    List<BaseAttrInfo> getAttrListByValueId(Set<String> valueIds);
}
