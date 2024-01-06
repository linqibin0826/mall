package com.linqibin.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linqibin.common.utils.PageUtils;
import com.linqibin.common.utils.Query;
import com.linqibin.mall.product.dao.AttrGroupDao;
import com.linqibin.mall.product.entity.AttrAttrgroupRelationEntity;
import com.linqibin.mall.product.entity.AttrEntity;
import com.linqibin.mall.product.entity.AttrGroupEntity;
import com.linqibin.mall.product.entity.CategoryEntity;
import com.linqibin.mall.product.service.AttrAttrgroupRelationService;
import com.linqibin.mall.product.service.AttrGroupService;
import com.linqibin.mall.product.service.AttrService;
import com.linqibin.mall.product.service.CategoryService;
import com.linqibin.mall.product.vo.AttrGroupWithAttrsVo;
import com.linqibin.mall.product.vo.SkuItemVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrAttrgroupRelationService relationService;

    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catalogId) {
        String key = (String) params.get("key");
        // 模糊查询: select * from pms_attr_group where catalog_id = ? and (attr_group_id=key or attr_group_name like %key%)
        QueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<>();
        // 判断是否带了模糊查询
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(Condition -> {
                Condition.eq("attr_group_id", key).or().like("attr_group_name", key);
            });
        }

        // 如果catalogId是0, 则查询所有.
        if (catalogId != 0) {
            queryWrapper.eq("catalog_id", catalogId);
        }

        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                queryWrapper);
        return new PageUtils(page);

    }

    /**
     * 根据分类id 查出所有的分组以及这些组里边的属性
     */
    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrByCatelogId(Long catelogId) {

        // 1.查询这个品牌id下所有分组
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));

        // 2.查询所有属性
        List<AttrGroupWithAttrsVo> collect = attrGroupEntities.stream().map(group ->{
            // 先对拷分组数据
            AttrGroupWithAttrsVo attrVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(group, attrVo);
            // 按照分组id查询所有关联属性并封装到vo
            List<AttrEntity> attrs = attrService.getRelationAttr(attrVo.getAttrGroupId());
            attrVo.setAttrs(attrs);
            return attrVo;
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public Long[] findCatalogPath(Long catalogId) {
        List<Long> longs = new ArrayList<>();
        CategoryEntity byId = categoryService.getById(catalogId);
        Long parentCid = byId.getParentCid();
        longs.add(catalogId);
        while (parentCid != 0) {
            longs.add(parentCid);
            parentCid = categoryService.getById(parentCid).getParentCid();
        }
        Collections.reverse(longs);
        return longs.toArray(new Long[0]);
    }

    @Override
    public List<AttrEntity> getRelationByGroupId(Long attrGroupId) {
        List<AttrAttrgroupRelationEntity> relationEntities = relationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrGroupId));

        List<AttrEntity> attrEntities = null;
        List<Long> attrIds = relationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        attrEntities = (List<AttrEntity>) attrService.listByIds(attrIds);

        return attrEntities;
    }

    @Override
    public void deleteRelations(List<AttrAttrgroupRelationEntity> relations) {
        relationService.deleteBatchRelations(relations);
    }

    @Override
    public List<SkuItemVo.SpuItemAttrGroupVo> listAttrGroupBySpuId(Long spuId) {
        return baseMapper.queryAttrGroupsWithAttr(spuId);
    }

}
