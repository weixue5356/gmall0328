package com.atguigu.gmall.manage.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.bean.BaseAttrInfo;
import com.atguigu.bean.BaseAttrValue;
import com.atguigu.gmall.manage.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.manage.mapper.BaseAttrValueMapper;
import com.atguigu.service.BaseAttrInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BaseAttrSereviceImpl implements BaseAttrInfoService {
    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    @Override
    public List<BaseAttrInfo> getAttrList(String catalog3Id) {
        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
        baseAttrInfo.setCatalog3Id(catalog3Id);
        return baseAttrInfoMapper.select(baseAttrInfo);
    }

    @Override
    public void saveAttr(BaseAttrInfo baseAttrInfo) {
        baseAttrInfoMapper.insertSelective(baseAttrInfo);
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        for (BaseAttrValue attrValue : attrValueList) {
            attrValue.setAttrId(baseAttrInfo.getId());
            baseAttrValueMapper.insert(attrValue);
        }

    }

    @Override
    public List<BaseAttrValue> getAttrValueList(String attrId) {
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(attrId);
        return baseAttrValueMapper.select(baseAttrValue);
    }

    @Override
    public void deleteAttrById(String id) {
        baseAttrInfoMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void updateAttrValueList(BaseAttrInfo baseAttrInfo) {
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        for (BaseAttrValue baseAttrValue : attrValueList) {
            baseAttrValue.setAttrId(baseAttrInfo.getId());
            baseAttrValueMapper.updateByPrimaryKey(baseAttrValue);
        }


    }

    @Override
    public List<BaseAttrInfo> getAttrListBycatalog3Id(String catalog3Id) {
        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();

        baseAttrInfo.setCatalog3Id(catalog3Id);

        List<BaseAttrInfo> baseAttrInfos = baseAttrInfoMapper.select(baseAttrInfo);


        for (BaseAttrInfo attrInfo : baseAttrInfos) {

            String attrId = attrInfo.getId();

            BaseAttrValue baseAttrValue = new BaseAttrValue();

            baseAttrValue.setAttrId(attrId);

            List<BaseAttrValue> baseAttrValues = baseAttrValueMapper.select(baseAttrValue);
            attrInfo.setAttrValueList(baseAttrValues);
        }

        return baseAttrInfos;

    }

    @Override
    public List<BaseAttrInfo> getAttrListByValueId(Set<String> valueIds) {
        if (valueIds != null && valueIds.size() > 0) {
            String join = StringUtils.join(valueIds, ",");

            List<BaseAttrInfo> baseAttrInfos = baseAttrValueMapper.selectAttrListByValueIds(join);

            return baseAttrInfos;
        } else {
            return null;
        }

    }


}
