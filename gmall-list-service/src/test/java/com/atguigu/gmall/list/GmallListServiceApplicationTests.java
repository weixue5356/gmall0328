package com.atguigu.gmall.list;
import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.bean.SkuInfo;
import com.atguigu.bean.SkuLsInfo;
import com.atguigu.service.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.lucene.search.highlight.QueryTermScorer;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallListServiceApplicationTests {
	@Autowired
	JestClient jestClient;

	@Reference
	private SkuService skuService;


	public static String getMyDsl(){
		//"filter":
		//"term":
		//"must":
		//"match":

		//创建一个dsl对象,先过滤在搜索
		SearchSourceBuilder dsl = new SearchSourceBuilder();
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

		//BoolQueryBuilder filter = boolQueryBuilder.filter();

		TermQueryBuilder t1 = new TermQueryBuilder("catalog3Id","61");
		boolQueryBuilder.filter(t1);
		TermQueryBuilder t2 = new TermQueryBuilder("skuAttrValueList.valueId","51");
		boolQueryBuilder.filter(t2);
		TermQueryBuilder t3 = new TermQueryBuilder("skuAttrValueList.valueId","54");
		boolQueryBuilder.filter(t3);
		//过滤条件
         String [] s = new String[2];
         s[0] = "51";
         s[1] = "54";

		TermsQueryBuilder t4 = new TermsQueryBuilder("skuAttrValueList.valueId",s);
		boolQueryBuilder.filter(t4);
		//搜索匹配
		MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName","小米");
		boolQueryBuilder.must(matchQueryBuilder);
		MatchQueryBuilder matchQueryBuilder1 = new MatchQueryBuilder("skuDesc", "小米");
		boolQueryBuilder.must(matchQueryBuilder1);


		dsl.query(boolQueryBuilder);
		dsl.from(0);
		dsl.size(100);
		System.out.println(dsl.toString());
		return  dsl.toString();


	}


	@Test
	public  void search2 () {
		List<SkuLsInfo> skuLsInfos = new ArrayList<SkuLsInfo>();
		Search search = new Search.Builder(getMyDsl()).addIndex("gmall").addType("SkuLsInfo").build();

		try {
			SearchResult execute = jestClient.execute(search);


			List<SearchResult.Hit<SkuLsInfo, Void>> hits = execute.getHits(SkuLsInfo.class);


			for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
				SkuLsInfo source = hit.source;
				skuLsInfos.add(source);
			}
			System.out.println(skuLsInfos.size());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}






	@Test
	public  void search () {
		List<SkuLsInfo> skuLsInfos = new ArrayList<SkuLsInfo>();
		Search search = new Search.Builder("{\n" +
				"  \"query\": {\n" +
				"    \"bool\": {\n" +
				"      \"filter\": [{\n" +
				"        \"term\": {\n" +
				"          \"catalog3Id\" : \"61\"\n" +
				"        }\n" +
				"      },\n" +
				"      {\n" +
				"        \"term\" : {\n" +
				"          \"skuAttrValueList.valueId\" : \"51\"\n" +
				"        }\n" +
				"      },\n" +
				"      {\n" +
				"        \"term\" : {\n" +
				"          \"skuAttrValueList.valueId\" : \"54\"\n" +
				"        }\n" +
				"      }\n" +
				"      ],\n" +
				"      \"must\": [\n" +
				"        {\n" +
				"          \"match\": {\n" +
				"            \"skuName\": \"小米\"\n" +
				"          }\n" +
				"        }\n" +
				"      ]\n" +
				"    \n" +
				"    }\n" +
				"  }\n" +
				"}").addIndex("gmall").addType("SkuLsInfo").build();

		try {
			SearchResult execute = jestClient.execute(search);


			List<SearchResult.Hit<SkuLsInfo, Void>> hits = execute.getHits(SkuLsInfo.class);


			for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
				SkuLsInfo source = hit.source;
				skuLsInfos.add(source);
			}
			System.out.println(skuLsInfos.size());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


       @Test
	public  void contextLoads(){

       	// 查询mysql中的sku信息
		List<SkuInfo> skuInfos = skuService.getSkuListByCatalog3Id("61");
		   // 转化es中的sku信息
		   ArrayList<SkuLsInfo> skuLsInfos = new ArrayList<>();

		   for (SkuInfo skuInfo : skuInfos) {
			   SkuLsInfo skuLsInfo = new SkuLsInfo();

			   try {
				   BeanUtils.copyProperties(skuLsInfo,skuInfo);
			   } catch (IllegalAccessException e) {
				   e.printStackTrace();
			   } catch (InvocationTargetException e) {
				   e.printStackTrace();
			   }
			   skuLsInfos.add(skuLsInfo);

		   }
		   //// 导入到es中
		   //System.out.println("11111111");
		   for (SkuLsInfo skuLsInfo : skuLsInfos) {
			   String id = skuLsInfo.getId();
			   Index build = new Index.Builder(skuLsInfo).index("gmall").type("SkuLsInfo").id(id).build();
			   System.out.println(build.toString());
			   try {
				   jestClient.execute(build);
			   } catch (IOException e) {
				   e.printStackTrace();
			   }

		   }


	   }

	}


