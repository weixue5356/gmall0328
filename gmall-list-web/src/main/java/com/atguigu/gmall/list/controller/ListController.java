package com.atguigu.gmall.list.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.bean.*;
import com.atguigu.service.BaseAttrInfoService;
import com.atguigu.service.ListService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
public class ListController {

    @Reference
    private ListService listService;


    @Reference
    private BaseAttrInfoService baseAttrInfoService;



    @RequestMapping("/index")
    public String index() {
        return "index";
    }

    @RequestMapping("/list.html")
    public String list(SkuLsParam skuLsParam, ModelMap map) {

        List<SkuLsInfo> skuLsInfo = listService.search(skuLsParam);

        //封装平台属性的列表,排除已经选中的属性
        List<BaseAttrInfo> baseAttrInfos = getAttrValueIds(skuLsInfo);

        ArrayList<Crumb> crumbs = new ArrayList<Crumb>();//面包屑

        String[] valueIds = skuLsParam.getValueId();

        if (valueIds != null && valueIds.length > 0 && baseAttrInfos != null) {

            for (String s : valueIds) {

                Iterator<BaseAttrInfo> iterator = baseAttrInfos.iterator();
                while (iterator.hasNext()) {
                    BaseAttrInfo baseAttrInfo = iterator.next();
                    List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
                    for (BaseAttrValue baseAttrValue : attrValueList) {

                            if(baseAttrValue.getId().equals(s)){
                                Crumb crumb = new Crumb();
                                //制作面包屑url
                                String urlParamForCrumb = getUrlParamForCrumb(skuLsParam,s);
                                //制作面包屑的name
                                String valueName = "";
                                valueName = baseAttrValue.getValueName();
                                //封装好面包屑
                                crumb.setUrlParam(urlParamForCrumb);
                                crumb.setValueName(valueName);
                                crumbs.add(crumb);
                                iterator.remove();
                        }
                    }
                }
            }
        }

        String urlParam = getUrlParam(skuLsParam);

        map.put("attrList", baseAttrInfos);
        map.put("skuLsInfoList", skuLsInfo);
        map.put("urlParam", urlParam);
        map.put("attrValueSelectedList", crumbs);


        return "list";
    }

    /**
     * 制作面包屑的url
     * @param skuLsParam
     * @param id
     * @return
     */
    private String getUrlParamForCrumb(SkuLsParam skuLsParam, String id) {
        String urlParam = "";

        String keyword = skuLsParam.getKeyword();
        String catalog3Id = skuLsParam.getCatalog3Id();
        String[] valueIds = skuLsParam.getValueId();

        if (StringUtils.isNotBlank(keyword)) {
            if (StringUtils.isBlank(urlParam)) {
                urlParam = "keyword" + keyword;
            } else {
                urlParam = urlParam + "&keyword=" + keyword;
            }
        }

        if (StringUtils.isNotBlank(catalog3Id)) {
            if (StringUtils.isBlank(urlParam)) {
                urlParam = "catalog3Id" + catalog3Id;
            } else {
                urlParam = urlParam + "&catalog3Id=" + catalog3Id;
            }
        }

        if (valueIds != null && valueIds.length > 0) {
            for (String valueId : valueIds) {
                if (!id.equals(valueId)) {
                    urlParam = urlParam + "&valueId=" + valueId;
                }

            }
        }

        return  urlParam;
    }

    /**
     * 获取普通的url
     * @param skuLsParam
     * @return
     */
    private String getUrlParam(SkuLsParam skuLsParam) {

        String urlParam = "";

        String[] valueId = skuLsParam.getValueId();
        String keyword = skuLsParam.getKeyword();
        String catalog3Id = skuLsParam.getCatalog3Id();

        if (StringUtils.isNotBlank(keyword)) {
            if (StringUtils.isBlank(urlParam)) {

                urlParam = "keyword=" + keyword;

            } else {
                urlParam = urlParam + "&keyword=" + keyword;
            }
        }

        if (StringUtils.isNotBlank(catalog3Id)) {
            if (StringUtils.isBlank(urlParam)) {

                urlParam = "catalog3Id=" + catalog3Id;

            } else {
                urlParam = urlParam + "&catalog3Id=" + catalog3Id;
            }
        }

        if (valueId != null && valueId.length > 0) {
            for (String s : valueId) {
                urlParam = urlParam + "&valueId=" + s;
            }
        }

        return urlParam;
    }


    /**
     * 获取valueIds
     * @param skuLsInfo
     * @return
     */
    private List<BaseAttrInfo> getAttrValueIds(List<SkuLsInfo> skuLsInfo) {
        Set<String> valueIds = new HashSet<>();

        for (SkuLsInfo lsInfo : skuLsInfo) {
            List<SkuLsAttrValue> skuAttrValueList = lsInfo.getSkuAttrValueList();
            for (SkuLsAttrValue skuLsAttrValue : skuAttrValueList) {
                String valueId = skuLsAttrValue.getValueId();
                valueIds.add(valueId);
            }
        }
        // 根据去重后的id集合检索，关联到的平台属性列表
        List<BaseAttrInfo> baseAttrInfos = baseAttrInfoService.getAttrListByValueId(valueIds);

        return baseAttrInfos;

    }
}