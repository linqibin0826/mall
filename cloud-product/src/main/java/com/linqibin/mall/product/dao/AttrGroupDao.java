package com.linqibin.mall.product.dao;

import com.linqibin.mall.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linqibin.mall.product.vo.SkuItemVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 *
 * @author hugh
 * @email linqibin0826@gmail.com
 * @date 2021-01-08 22:12:57
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    /**
     * 根据spuId查询spu的所有属性属性分组和组内信息
     * @param spuId
     * @return
     */
    List<SkuItemVo.SpuItemAttrGroupVo> queryAttrGroupsWithAttr(Long spuId);
}
