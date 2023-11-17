package com.linqibin.mall.product.feign;

import com.linqibin.common.to.es.SkuEsModel;
import com.linqibin.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>Title: SearchFeignService</p>
 * Description：远程上架商品
 * date：2020/6/8 21:42
 */
@FeignClient("cloud-search")
public interface SearchFeignService {

	@PostMapping("/search/save/product")
	R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);
}
