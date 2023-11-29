package com.linqibin.mall.product.dao;

import com.linqibin.mall.product.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linqibin.mall.product.vo.ItemSaleAttrVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 *
 * @author hugh
 * @email linqibin0826@gmail.com
 * @date 2021-01-08 22:12:57
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<ItemSaleAttrVo> getSaleAttrsBuSpuId(@Param("spuId") Long spuId);
}
