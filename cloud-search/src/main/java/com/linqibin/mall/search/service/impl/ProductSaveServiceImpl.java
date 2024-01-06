package com.linqibin.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.linqibin.common.to.es.SkuEsModel;
import com.linqibin.mall.search.config.MallElasticSearchConfig;
import com.linqibin.mall.search.constant.EsConstant;
import com.linqibin.mall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Title: ProductSaveServiceImpl</p>
 * Description：
 * date：2020/6/8 21:16
 */
@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {

	@Resource
	private RestHighLevelClient client;

	/**
	 * 将数据保存到ES
	 * BulkRequest bulkRequest, RequestOptions options
	 */
	@Override
	public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
		// 1.给ES建立一个索引 product
		BulkRequest request = new BulkRequest();
		for (SkuEsModel esModel : skuEsModels) {
			IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
			String esJson = JSON.toJSONString(esModel);
			indexRequest.source(esJson, XContentType.JSON);
			request.add(indexRequest);
		}
		BulkResponse bulk = client.bulk(request, RequestOptions.DEFAULT);
        return !bulk.hasFailures();
    }
}
