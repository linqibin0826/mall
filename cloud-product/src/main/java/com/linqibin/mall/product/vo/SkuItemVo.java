package com.linqibin.mall.product.vo;

import com.linqibin.mall.product.entity.SkuImagesEntity;
import com.linqibin.mall.product.entity.SkuInfoEntity;
import com.linqibin.mall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: SkuItemVo</p>
 * Description：
 * date：2020/6/24 13:33
 */
@Data
public class SkuItemVo {

	/**
	 * 基本信息
	 */
	SkuInfoEntity info;

	boolean hasStock = true;

	/**
	 * 图片信息
	 */
	List<SkuImagesEntity> images;

	/**
	 * 销售属性组合
	 */
	List<ItemSaleAttrVo> saleAttr;

	/**
	 * 介绍
	 */
	SpuInfoDescEntity desc;

	/**
	 * 参数规格信息
	 */
	List<SpuItemAttrGroup> groupAttrs;

}