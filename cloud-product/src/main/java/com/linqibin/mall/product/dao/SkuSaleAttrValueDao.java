package com.linqibin.mall.product.dao;

import com.linqibin.mall.product.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linqibin.mall.product.vo.SkuItemVo;
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

    /**
     * 根据spuId查询所有的销售属性、将销售属性的值用逗号拼接起来
     * @param spuId
     * @return
     */
    List<SkuItemVo.SpuItemSaleAttrVo> querySpuSaleAttrs(Long spuId);
}
