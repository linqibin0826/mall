package com.linqibin.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linqibin.common.utils.PageUtils;
import com.linqibin.mall.product.entity.CategoryBrandRelationEntity;

import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author hugh
 * @email linqibin0826@gmail.com
 * @date 2021-01-08 22:12:57
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    void updateBrandName(Long brandId, String name);

    void updateCatalogName(Long catalogId, String name);
}

