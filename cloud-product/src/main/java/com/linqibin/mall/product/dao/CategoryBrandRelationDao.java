package com.linqibin.mall.product.dao;

import com.linqibin.mall.product.entity.CategoryBrandRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 品牌分类关联
 * 
 * @author hugh
 * @email linqibin0826@gmail.com
 * @date 2021-01-08 22:12:57
 */
@Mapper
public interface CategoryBrandRelationDao extends BaseMapper<CategoryBrandRelationEntity> {

    /**
     * 级联更新,当Category表更新Name字段时,自动更新中间表的冗余数据
     *
     * @param catalogId the catalog id
     * @param name      the name
     * @author hugh &you
     * @since 2021 /1/24 16:14
     */
    void updateCatalogName(@Param("catalogId") Long catalogId, @Param("catalogName") String name);
}
