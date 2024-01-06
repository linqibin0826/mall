package com.linqibin.mall.search.vo;

import com.linqibin.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: SearchResponse</p>
 * Description：包含页面需要的所有信息
 * date：2023/12/02 23:29
 */
@Data
public class SearchResult {

	/**
	 * 检索到的sku商品信息
	 */
	private List<SkuEsModel> products;

	/**
	 * 当前页码
	 */
	private Integer pageNum;

	private Integer pageSize;

	private Integer total;

	private Integer totalPages;

	/**
	 * 可继续检索的品牌列表
	 */
	private List<BrandVo> brands;

	private List<CategoryVo> catalogs;

	private List<AttrVo> attrs;

	/**
	 * 导航页
	 */
	private List<Integer> pageNavs;

	private List<NavValueVo> navs;

	/**
	 * 面包屑导航功能
	 */
	@Data
	public static class NavValueVo {
		private String name;

		private String navValue;

		/**
		 * 移除此面包屑后该跳往的连接
		 */
		private String link;
	}

	@Data
	public static class AttrVo {
		private Long attrId;

		private String attrName;

		/**
		 * 一个属性有多个可选值
		 */
		private List<String> attrValue;
	}

	@Data
	public static class CategoryVo {

		private String catalogId;

		private String catalogName;
	}
}
