package com.linqibin.mall.search.service;


import com.linqibin.common.to.es.SkuEsModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * <p>Title: ProductSaveService</p>
 * Description：
 * date：2020/6/8 21:15
 */
public interface ProductSaveService {

	Logger log = LoggerFactory.getLogger(ProductSaveService.class);

	/**
	 * 将sku信息发布到ES
	 * @param skuEsModels sku模型信息
	 * @return true：成功 false：失败
	 */
	boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
