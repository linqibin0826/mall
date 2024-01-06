package com.linqibin.mall.cart.feign;

import com.linqibin.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Sku信息远程调用接口
 * @author linqibin
 * @date   2024/1/6 12:39
 * @email  1214219989@qq.com
 */
@FeignClient("cloud-product")
public interface ProductFeignClient {

    @RequestMapping("/product/skuinfo/info/{skuId}")
    R info(@PathVariable("skuId") Long skuId);

    /**
     * 根据skuId获取该商品的销售属性信息
     * @param skuId  商品id
     * @return  销售属性key：销售属性value 的集合
     */
    @GetMapping("/product/skusaleattrvalue/stringList/{skuId}")
    R getSkuSaleAttrValues(@PathVariable("skuId") Long skuId);

}
