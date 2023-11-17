package com.linqibin.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linqibin.common.to.es.SkuHasStockVo;
import com.linqibin.common.utils.PageUtils;
import com.linqibin.mall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author hugh
 * @email linqibin0826@gmail.com
 * @date 2021-01-10 20:05:37
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    double addStock(Long skuId, Long wareId, Integer skuNum);

    /**
     * 查询sku是否还有库存
     * @param skuIds
     * @return
     */
    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);
}

