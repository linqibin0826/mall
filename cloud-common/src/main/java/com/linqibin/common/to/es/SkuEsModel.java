package com.linqibin.common.to.es;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>Title: SkuEsModel</p>
 * Description：
 * "mappings": {
 *     "properties": {
 *       "skuId":{
 *         "type": "long"
 *       },
 *       "spuId":{
 *         "type": "keyword"
 *       },
 *       "skuTitle":{
 *         "type": "text",
 *         "analyzer": "ik_smart"
 *       },
 *       "skuPrice":{
 *         "type": "keyword"
 *       },
 *       "skuImg":{
 *         "type": "keyword",
 *         "index": false,
 *         "doc_values": false
 *       },
 *       "saleCount":{
 *         "type": "long"
 *       },
 *       "hasStock":{
 *         "type": "boolean"
 *       },
 *       "hotScore":{
 *         "type": "long"
 *       },
 *       "brandId":{
 *         "type": "long"
 *       },
 *       "catalogId":{
 *         "type": "long"
 *       },
 *       "brandName":{
 *         "type":"keyword",
 *         "index": false,
 *         "doc_values": false
 *       },
 *       "brandImg":{
 *         "type": "keyword",
 *         "index": false,
 *         "doc_values": false
 *       },
 *       "catalogName":{
 *         "type": "keyword",
 *         "index": false,
 *         "doc_values": false
 *       },
 *       "attrs":{
 *         "type": "nested",
 *         "properties": {
 *           "attrId":{
 *             "type":"long"
 *           },
 *           "attrName":{
 *             "type":"keyword",
 *             "index":false,
 *             "doc_values": false
 *           },
 *           "attrValue":{
 *             "type":"keyword"
 *           }
 *         }
 *       }
 *     }
 *   }
 * date：2020/6/8 18:52
 */
@Data
@ToString
public class SkuEsModel implements Serializable {

	/**
	 * 商品skuId
	 */
	private Long skuId;

	/**
	 * 商品spuId
	 */
	private Long spuId;

	/**
	 * 商品sku标题
	 */
	private String skuTitle;

	/**
	 * 商品价格，为了在es中不出现精度问题，直接保存为string
	 */
	private BigDecimal skuPrice;

	/**
	 * sku图片信息
	 */
	private String skuImg;

	/**
	 * 销量
	 */
	private Long saleCount;

	/**
	 * 是否有库存
	 */
	private Boolean hasStock;

	/**
	 * 热度评分
	 */
	private BigDecimal hotScore;

	/**
	 * 分类名称
	 */
	private Long brandId;

	/**
	 * 分类id
	 */
	private Long catalogId;

	/**
	 * 品牌名称
	 */
	private String brandName;

	/**
	 * 品牌图片
	 */
	private String brandImg;

	/**
	 * 分类名称
	 */
	private String catalogName;

	/**
	 * 可检索属性列表
	 */
	private List<Attr> attrs;

	/**
	 * 可检索属性
	 */
	@Data
	@ToString
	public static class Attr {
		/**
		 * 属性id
		 */
		private Long attrId;
		/**
		 * 属性名称
		 */
		private String attrName;
		/**
		 * 属性值
		 */
		private String attrValue;
	}
}
