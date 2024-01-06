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

	private boolean hasStock;

	/**
	 * sku的基本信息
	 */
	private SkuInfoEntity info;

	/**
	 * sku的图片信息
	 */
	private List<SkuImagesEntity> images;

	/**
	 * spu详情，同一类sku公用spu的详情
	 */
	private SpuInfoDescEntity desp;

	/**
	 * sku的销售属性组合
	 */
	private List<SpuItemSaleAttrVo> saleAttr;

	/**
	 * spu的属性分组信息
	 */
	private List<SpuItemAttrGroupVo> groupAttrs;

	/**
	 * spu的销售属性组合信息
	 */
	@Data
	public static class SpuItemSaleAttrVo {
		private Long attrId;
		private String attrName;
		private List<AttrValueWithSkuIdVo> attrValues;
	}

	/**
	 * 属性值及该属性值涉及的所有skuId
	 * 用于商品详情页，多个属性值确定一个sku。
	 */
	@Data
	public static class AttrValueWithSkuIdVo {

		private String attrValue;

		private String skuIds;
	}

	@Data
	public static class SpuItemAttrGroupVo {
		private String groupName;
		private List<SpuBaseAttrVo> attrs;
	}

	@Data
	public static class SpuBaseAttrVo {
		private String attrName;

		private String attrValue;
	}


}
