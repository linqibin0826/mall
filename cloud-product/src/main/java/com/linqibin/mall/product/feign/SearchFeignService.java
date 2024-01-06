package com.linqibin.mall.product.feign;

import com.linqibin.common.to.es.SkuEsModel;
import com.linqibin.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient("cloud-search")
public interface SearchFeignService {


    /**
     * 上架sku
     * @param esModels es模型
     */
    @PostMapping("/search/save/product")
    R productStatusUp(List<SkuEsModel> esModels);
}
