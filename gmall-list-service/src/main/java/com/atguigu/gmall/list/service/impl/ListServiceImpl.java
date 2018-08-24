package com.atguigu.gmall.list.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.bean.SkuLsInfo;
import com.atguigu.bean.SkuLsParam;
import com.atguigu.service.ListService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ListServiceImpl implements ListService {


    @Autowired
    private JestClient jestClient;



    @Override
    public List<SkuLsInfo> search(SkuLsParam skuLsParam) {


        List<SkuLsInfo> skuLsInfos = new ArrayList<SkuLsInfo>();
        Search search = new Search.Builder(getMyDsl(skuLsParam))
                .addIndex("gmall").addType("SkuLsInfo").build();

        try {
            SearchResult execute = jestClient.execute(search);


            List<SearchResult.Hit<SkuLsInfo, Void>> hits = execute.getHits(SkuLsInfo.class);


            for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {

                SkuLsInfo source = hit.source;


                Map<String, List<String>> highlight = hit.highlight;

                if(highlight!=null && highlight.size() > 0){
                    List<String> skuName = highlight.get("skuName");
                    String s = skuName.get(0);
                    source.setSkuName(s);
                }
                skuLsInfos.add(source);
            }
           // System.out.println(skuLsInfos.size());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return skuLsInfos;

    }


    public  String getMyDsl(SkuLsParam skuLsParam){
        //"filter":
        //"term":
        //"must":
        //"match":
        //创建一个dsl对象,先过滤在搜索
        String catalog3Id = skuLsParam.getCatalog3Id();
        String keyword = skuLsParam.getKeyword();
        String[] valueId = skuLsParam.getValueId();

        SearchSourceBuilder dsl = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        //BoolQueryBuilder filter = boolQueryBuilder.filter();

        if (StringUtils.isNotBlank(catalog3Id)) {
            TermsQueryBuilder t = new TermsQueryBuilder("catalog3Id", catalog3Id);
            boolQueryBuilder.filter(t);
        }

        if (valueId != null && valueId.length > 0){

            for (int i = 0; i < valueId.length; i++) {

                TermsQueryBuilder t = new TermsQueryBuilder("skuAttrValueList.valueId", valueId[i]);
                boolQueryBuilder.filter(t);
            }

        }
//        //过滤条件
//        String [] s = new String[2];
//        s[0] = "51";
//        s[1] = "54";
//
//        TermsQueryBuilder t4 = new TermsQueryBuilder("skuAttrValueList.valueId",s);
 //       boolQueryBuilder.filter(t4);
        //搜索匹配

        if (StringUtils.isNotBlank(keyword)) {
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName",keyword);
            boolQueryBuilder.must(matchQueryBuilder);
        }

        dsl.query(boolQueryBuilder);
        dsl.size(100);
        dsl.from(0);

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("skuName");
        highlightBuilder.preTags("<span style='color:red;font-weight:bolder;'>");
        highlightBuilder.postTags("</span>");
        dsl.highlight(highlightBuilder);

        System.out.println(dsl.toString());
        return  dsl.toString();


    }
}
