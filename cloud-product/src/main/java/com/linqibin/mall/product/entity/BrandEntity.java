package com.linqibin.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.linqibin.common.valid.ListValue;
import com.linqibin.common.valid.addGroup;
import com.linqibin.common.valid.updateGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author hugh
 * @email linqibin0826@gmail.com
 * @date 2021-01-08 22:12:57
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@TableId
	@Null(message = "不可指定品牌ID", groups = {addGroup.class})
	@NotNull(message = "品牌ID不可以为空", groups = {updateGroup.class})
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名不可为空", groups = {addGroup.class, updateGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotBlank(groups = {addGroup.class})
	@URL(message = "logo必须是一个合法的url地址", groups = {addGroup.class, updateGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@ListValue(vals = {0, 1}, groups = {addGroup.class, updateGroup.class})
	@NotNull(groups = {addGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotBlank(groups = {addGroup.class})
	@Pattern(regexp = "^[a-zA-Z]{1}$", message = "检索首字母字段不合法", groups = {addGroup.class, updateGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(groups = {addGroup.class})
	@Min(value = 0, message = "排序字段必须是正整数", groups = {addGroup.class, updateGroup.class})
	private Integer sort;

}
