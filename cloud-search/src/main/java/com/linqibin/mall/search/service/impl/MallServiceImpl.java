package com.linqibin.mall.search.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.linqibin.common.to.es.SkuEsModel;
import com.linqibin.mall.search.config.MallElasticSearchConfig;
import com.linqibin.mall.search.constant.EsConstant;
import com.linqibin.mall.search.feign.ProductFeignService;
import com.linqibin.mall.search.service.MallService;
import com.linqibin.mall.search.vo.AttrResponseVo;
import com.linqibin.mall.search.vo.BrandVo;
import com.linqibin.mall.search.vo.SearchParam;
import com.linqibin.mall.search.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.linqibin.common.constant.BaseConstants.STRING_BLANK;
import static com.linqibin.mall.search.constant.EsConstant.*;

/**
 * <p>Title: MallServiceImpl</p>
 * Description：
 * date：2020/6/12 23:06
 */
@Slf4j
@Service
public class MallServiceImpl implements MallService {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Resource
    private ProductFeignService productFeignService;

    @Override
    public SearchResult search(SearchParam Param) {

        SearchResult result = null;
        try {
            // 1.准备检索请求
            SearchRequest searchRequest = buildSearchRequest(Param);
            // 2.执行检索请求
            SearchResponse response = restHighLevelClient.search(searchRequest, MallElasticSearchConfig.COMMON_OPTIONS);
            // 3.分析响应数据
            result = buildSearchResult(response, Param);
        } catch (Exception e) {
            log.error("An exception occurred while performing the search... \n", e);
        }
        return result;
    }

    /**
     * 准备检索请求  [构建查询语句]
     */
    private SearchRequest buildSearchRequest(SearchParam param) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 构建查询语句
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();

        // 1.根据skuTitle全文检索
        String keyword = param.getKeyword();
        if (StrUtil.isNotBlank(keyword)) {
            queryBuilder.must(QueryBuilders.matchQuery("skuTitle", keyword));
            HighlightBuilder highlighter = new HighlightBuilder();
            highlighter.field("skuTitle");
            highlighter.preTags("<b style='color:red'>");
            highlighter.postTags("</b>");
            sourceBuilder.highlighter(highlighter);
        }

        // 2.过滤
        // 2.1 根据三级分类id
        Long catalog3Id = param.getCatalog3Id();
        if (Objects.nonNull(catalog3Id)) {
            queryBuilder.filter(QueryBuilders.termQuery("catalogId", catalog3Id));
        }
        // 2.2 根据品牌id(支持多选)
        List<Long> brandId = param.getBrandId();
        if (CollectionUtil.isNotEmpty(brandId)) {
            queryBuilder.filter(QueryBuilders.termsQuery("brandId", brandId));
        }
        // 2.3 根据价格区间(_2000: 0到2000元)
        String skuPrice = param.getSkuPrice();
        if (StrUtil.isNotBlank(skuPrice)) {
            String[] priceInterval = skuPrice.split(PRICE_SEPARATOR);
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            // 2000_  这种情况只会被分成一个字符串
            if (skuPrice.endsWith(PRICE_SEPARATOR)) {
                rangeQuery.gte(priceInterval[0]);
            } else if (skuPrice.startsWith(PRICE_SEPARATOR)) {
                rangeQuery.gte("0").lte(priceInterval[1]);
            } else {
                rangeQuery.gte(priceInterval[0]).lte(priceInterval[1]);
            }

            queryBuilder.filter(rangeQuery);
        }
        // 2.4 只显示有库存的
        Integer hasStock = param.getHasStock();
        if (HAS_STOCK_ONLY.equals(hasStock)) {
            queryBuilder.filter(QueryBuilders.termsQuery("hasStock", "true"));
        }
        // 2.5 根据属性值过滤(1_其他:安卓:苹果) 多个属性值用:分隔
        List<String> attrs = param.getAttrs();
        if (CollectionUtil.isNotEmpty(attrs)) {

            for (String attr : attrs) {
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                List<QueryBuilder> must = boolQuery.must();
                String[] attrItem = attr.split(ATTR_SEPARATOR);
                must.add(QueryBuilders.termQuery("attrs.attrId", attrItem[0]));
                String[] attrValues = attrItem[1].split(ATTR_VALUE_SEPARATOR);
                must.add(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
                queryBuilder.filter(QueryBuilders.nestedQuery("attrs", boolQuery, ScoreMode.None));
            }
        }
        // 条件查询完毕，将条件对象设置给query
        sourceBuilder.query(queryBuilder);
        // 2.6 分页、排序
        sourceBuilder.from(param.getPageNum());
        sourceBuilder.size(EsConstant.PRODUCT_PASIZE);
        String sort = param.getSort();
        if (StrUtil.isNotBlank(sort)) {
            String[] sortArray = sort.split(SORT_SEPARATOR);
            String sortRule = sortArray[1];
            sourceBuilder.sort(sortArray[0], ES_SORT_DESC.equals(sortRule) ? SortOrder.DESC : SortOrder.ASC);
        }
        // 2.7 聚合
        // 2.7.1 品牌聚合
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brandAgg");
        brandAgg.field("brandId").size(BRAND_AGG_SIZE);
        brandAgg.subAggregation(AggregationBuilders.terms("brandNameAgg").field("brandName").size(BRAND_AGG_SIZE));
        brandAgg.subAggregation(AggregationBuilders.terms("brandImgAgg").field("brandImg").size(BRAND_AGG_SIZE));
        sourceBuilder.aggregation(brandAgg);

        // 2.7.2 分类聚合
        TermsAggregationBuilder catalogAgg = AggregationBuilders.terms("catalogAgg").field("catalogId");
        catalogAgg.subAggregation(AggregationBuilders.terms("catalogNameAgg").field("catalogName"));
        sourceBuilder.aggregation(catalogAgg);

        // 2.7.3 属性聚合
        NestedAggregationBuilder nested = AggregationBuilders.nested("attrAgg", "attrs");
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attrIdAgg").field("attrs.attrId");
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue"));
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName"));
        nested.subAggregation(attrIdAgg);
        sourceBuilder.aggregation(nested);

        log.info("The request condition is constructed, and the Query DSL is as follows: \n{}", sourceBuilder);
        return new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
    }

    /**
     * 构建结果数据 指定catalogId 、brandId、attrs.attrId、嵌入式查询、倒序、0-6000、每页显示两个、高亮skuTitle、聚合分析
     */
    private SearchResult buildSearchResult(SearchResponse response, SearchParam searchParam) {
        log.info("The search was successful, and the response to this search is as follows:\n{}", response);
        SearchResult result = new SearchResult();
        result.setPageNum(searchParam.getPageNum());
        result.setPageSize(PRODUCT_PASIZE);

        SearchHits searchHits = response.getHits();
        int total = (int) searchHits.getTotalHits().value;
        result.setTotal(total);

        result.setTotalPages(total % PRODUCT_PASIZE == 0 ? total / PRODUCT_PASIZE : (total / PRODUCT_PASIZE) + 1);
        // 设置导航页
        ArrayList<Integer> pageNavs = new ArrayList<>();
        for (int i = 1;i <= result.getTotalPages(); i++){
            pageNavs.add(i);
        }
        result.setPageNavs(pageNavs);
        List<SkuEsModel> esModels = Arrays.stream(searchHits.getHits())
                .map(item -> {
                    SkuEsModel esModel = JSON.parseObject(item.getSourceAsString(), SkuEsModel.class);
                    if(!StringUtils.isEmpty(searchParam.getKeyword())){
                        // 1.1 获取标题的高亮属性
                        HighlightField skuTitle = item.getHighlightFields().get("skuTitle");
                        String highlightFields = skuTitle.getFragments()[0].string();
                        // 1.2 设置文本高亮
                        esModel.setSkuTitle(highlightFields);
                    }
                    return esModel;
                }).collect(Collectors.toList());

        result.setProducts(esModels);

        // 分析聚合结果
        Aggregations aggregations = response.getAggregations();
        // 1.品牌聚合结果
        Terms terms = aggregations.get("brandAgg");
        List<BrandVo> brandVoList = terms.getBuckets().stream().map(item -> {
            BrandVo brandVo = new BrandVo();
            brandVo.setBrandId(item.getKeyAsNumber().longValue());
            Terms brandNameTerms = item.getAggregations().get("brandNameAgg");
            brandVo.setBrandName(brandNameTerms.getBuckets().get(0).getKeyAsString());
            Terms brandImgAgg = item.getAggregations().get("brandImgAgg");
            brandVo.setBrandImg(brandImgAgg.getBuckets().get(0).getKeyAsString());
            return brandVo;
        }).collect(Collectors.toList());
        result.setBrands(brandVoList);

        // 2.分类信息
        Terms catalogAgg = aggregations.get("catalogAgg");
        List<SearchResult.CategoryVo> categoryVos = catalogAgg.getBuckets().stream().map(item -> {
            SearchResult.CategoryVo vo = new SearchResult.CategoryVo();
            vo.setCatalogId(item.getKeyAsString());
            Terms catalogNameAgg = item.getAggregations().get("catalogNameAgg");
            vo.setCatalogName(catalogNameAgg.getBuckets().get(0).getKeyAsString());
            return vo;
        }).collect(Collectors.toList());
        result.setCatalogs(categoryVos);

        // 3.属性信息
        Nested attrAgg = aggregations.get("attrAgg");
        Aggregations nestedAggregations = attrAgg.getAggregations();
        Terms attrIdAgg = nestedAggregations.get("attrIdAgg");
        List<SearchResult.AttrVo> attrVos = attrIdAgg.getBuckets().stream().map(item -> {
            SearchResult.AttrVo vo = new SearchResult.AttrVo();
            vo.setAttrId(item.getKeyAsNumber().longValue());
            Terms attrNameAgg = item.getAggregations().get("attrNameAgg");
            vo.setAttrName(attrNameAgg.getBuckets().get(0).getKeyAsString());
            Terms attrValueAgg = item.getAggregations().get("attrValueAgg");
            List<String> attrValue = attrValueAgg.getBuckets().stream().map(MultiBucketsAggregation.Bucket::getKeyAsString).collect(Collectors.toList());
            vo.setAttrValue(attrValue);
            return vo;
        }).collect(Collectors.toList());
        result.setAttrs(attrVos);

        // 4. 封装面包屑导航
        List<String> paramAttrs = searchParam.getAttrs();
        if (CollectionUtil.isNotEmpty(paramAttrs)) {
            // 1_骁龙845:骁龙855
            List<SearchResult.NavValueVo> navs = paramAttrs.stream().map(attr -> {
                SearchResult.NavValueVo nav = new SearchResult.NavValueVo();
                String[] attrArray = attr.split(ATTR_SEPARATOR);
                nav.setNavValue(attrArray[1].replaceAll(":", "、"));
                AttrResponseVo attrResponseVo = productFeignService.getAttrsInfo(Long.parseLong(attrArray[0])).getData("attr", new TypeReference<AttrResponseVo>() {});
                nav.setName(attrResponseVo.getAttrName());
                String encode = URLUtil.encode(attr);
                String replaced = searchParam.get_queryString().replaceAll("&attrs=" + encode, STRING_BLANK);
                nav.setLink("http://search.gulimall.com/list.html?" + replaced);
                return nav;
            }).collect(Collectors.toList());
            result.setNavs(navs);
        }
        // TODO 条件筛选联动， 当面包屑上存在属性、品牌、分类时，对应的就不应该显示在条件筛选框中了

        return result;
    }

    /**
     * 替换字符
     * key ：需要替换的key
     */
//	private String replaceQueryString(SearchParam Param, String value, String key) {
//		String encode = null;
//		try {
//			encode = URLEncoder.encode(value,"UTF-8");
//			// 浏览器对空格的编码和java的不一样
//			encode = encode.replace("+","%20");
//			encode = encode.replace("%28", "(").replace("%29",")");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		return Param.get_queryString().replace("&" + key + "=" + encode, "");
//	}
}
