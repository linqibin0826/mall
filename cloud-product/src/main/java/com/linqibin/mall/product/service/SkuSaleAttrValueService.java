package com.linqibin.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linqibin.common.utils.PageUtils;
import com.linqibin.mall.product.entity.SkuSaleAttrValueEntity;
import com.linqibin.mall.product.vo.SkuItemVo;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author hugh
 * @email linqibin0826@gmail.com
 * @date 2021-01-08 22:12:57
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuItemVo.SpuItemSaleAttrVo> listSpuSaleAttrsInfo(Long spuId);

    /**
     * 根据skuId获取指定销售属性字符串列表
     * @param skuId
     * @return
     */
    List<String> saleAttrStringListBySkuId(Long skuId);
}

