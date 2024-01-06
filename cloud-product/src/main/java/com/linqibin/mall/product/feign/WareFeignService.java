package com.linqibin.mall.product.feign;


import com.linqibin.common.to.es.SkuHasStockVo;
import com.linqibin.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 *
 * @author linqibin
 * @date    2023/12/1 23:45
 * @email  1214219989@qq.com
 */
@FeignClient("cloud-ware")
public interface WareFeignService {

    @PostMapping("/ware/waresku/hasStock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);
}
