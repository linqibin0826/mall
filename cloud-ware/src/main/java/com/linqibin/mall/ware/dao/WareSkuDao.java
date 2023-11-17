package com.linqibin.mall.ware.dao;

import com.linqibin.common.to.es.SkuHasStockVo;
import com.linqibin.mall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 *
 * @author hugh
 * @email linqibin0826@gmail.com
 * @date 2021-01-10 20:05:37
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

    /**
     * 查询指定sku列表是否有库存
     * @param skuIds
     * @return
     */
    List<SkuHasStockVo> querySkuHashStock(List<Long> skuIds);
}
