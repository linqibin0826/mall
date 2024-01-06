package com.linqibin.mall.search.constant;

/**
 * <p>Title: EsConstant</p>
 * Description：
 * date：2020/6/8 21:19
 */
public class EsConstant {

	/**
	 * sku数据在ES中的索引
	 */
	public static final String PRODUCT_INDEX = "product";

	/**
	 * 分页的大小
	 */
	public static final int PRODUCT_PASIZE = 20;

	/**
	 * 品牌聚合 聚合数量
	 */
	public static final Integer BRAND_AGG_SIZE = 20;

	/**
	 * 价格区间分隔符
	 */
	public static final String PRICE_SEPARATOR = "_";

	/**
	 * 属性id与属性值分隔符
	 */
	public static final String ATTR_SEPARATOR = "_";

	/**
	 * 多个属性值之间的分隔符
	 */
	public static final String ATTR_VALUE_SEPARATOR = ":";

	/**
	 * 排序分隔符
	 */
	public static final String SORT_SEPARATOR = "_";

	public static final String ES_SORT_DESC = "desc";
	public static final String ES_SORT_ASC = "asc";

	/**
	 * 只显示有库存
	 */
	public static final Integer HAS_STOCK_ONLY = 1;
}
