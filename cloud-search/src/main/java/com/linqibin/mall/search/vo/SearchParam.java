package com.linqibin.mall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * <p>Title: SearchParam</p>
 * Description：封装页面所有可能传递过来的关键字
 * 		catalog3Id=225&keyword=华为&sort=saleCount_asc&hasStock=0/1&brandId=25&brandId=30
 * 模糊匹配，过滤（按照属性，分类，品牌，价格区间，库存），排序，分页，高亮。聚合。
 * date：2023/12/02 22:56
 */
@Data
public class SearchParam {

	/**
	 * 分页属性
	 */
	private Integer pageNum = 1;

	/**
	 * 关键词，搜索框，全文检索
	 */
	private String keyword;

	/**
	 * 三级分类id
	 */
	private Long catalog3Id;

	/**
	 * sort=saleCount_desc
	 * sort=price_desc
	 * sort=hotScore_desc
	 * 三选一
	 */
	private String sort;

	/**
	 * 好多的过滤条件  是否有库存
	 */
	private Integer hasStock;

	/**
	 * 按照价格区间过滤
	 */
	private String skuPrice;

	/**
	 * 按照品牌id过滤
	 */
	private List<Long> brandId;

	/**
	 * 按照商品属性多选过滤
	 * attrs=1_其他:安卓   属性的多个值使用冒号分割
	 */
	private List<String> attrs;

	/**
	 * 原生查询字符串——用于制作面包屑导航功能
	 */
	private String _queryString;

}
