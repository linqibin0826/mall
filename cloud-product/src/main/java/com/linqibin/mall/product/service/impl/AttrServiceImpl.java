package com.linqibin.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linqibin.common.constant.ProductConstant;
import com.linqibin.common.utils.PageUtils;
import com.linqibin.common.utils.Query;
import com.linqibin.mall.product.dao.AttrAttrgroupRelationDao;
import com.linqibin.mall.product.dao.AttrDao;
import com.linqibin.mall.product.entity.AttrAttrgroupRelationEntity;
import com.linqibin.mall.product.entity.AttrEntity;
import com.linqibin.mall.product.entity.AttrGroupEntity;
import com.linqibin.mall.product.entity.CategoryEntity;
import com.linqibin.mall.product.service.AttrAttrgroupRelationService;
import com.linqibin.mall.product.service.AttrGroupService;
import com.linqibin.mall.product.service.AttrService;
import com.linqibin.mall.product.service.CategoryService;
import com.linqibin.mall.product.vo.AttrRespVo;
import com.linqibin.mall.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Resource
    private AttrAttrgroupRelationDao relationDao;


    @Autowired
    AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    AttrGroupService attrGroupService;

    @Autowired
    CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVo attrVo) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        // 1.保存基本数据到attr表中.
        this.save(attrEntity);
        // 2.保存关联关系
        if (attrVo.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && attrVo.getAttrGroupId() != null) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            relationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationService.save(relationEntity);
        }
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catalogId, String type) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("attr_type",
                "base".equalsIgnoreCase(type) ?
                        ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()
                        : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        String key = (String) params.get("key");

        // 拼接查询条件
        // 1). ID
        if (catalogId != 0) {
            queryWrapper.eq("catelog_id", catalogId);
        }
        // 2). 模糊查询
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(condition -> {
                condition.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );

        List<AttrEntity> records = page.getRecords();
        // 继续查询其余两个字段: ①属性所属的catalog的名称.(属于哪一类商品的) ②属性所属的属性组名称
        List<AttrRespVo> respVos = records.stream().map(attrEntity -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);
            // 1.查询分类名称.
            CategoryEntity categoryEntity = categoryService.getById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                String catalogName = categoryEntity.getName();
                attrRespVo.setCatalogName(catalogName);
            }
            // 2.查询属性组名称.
            //    1) 属性和属性组关系表中找出所属属性组ID
            if ("base".equalsIgnoreCase(type)) {
                AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
                if (relationEntity != null && relationEntity.getAttrGroupId() != null) {
                    //    2) 根据属性组ID获取属性组名称
                    Long attrGroupId = relationEntity.getAttrGroupId();
                    String attrGroupName = attrGroupService.getById(attrGroupId).getAttrGroupName();
                    attrRespVo.setGroupName(attrGroupName);
                }
            }

            return attrRespVo;
        }).collect(Collectors.toList());
        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(respVos);
        return pageUtils;
    }

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrEntity attrEntity = getById(attrId);
        AttrRespVo attrRespVo = new AttrRespVo();
        BeanUtils.copyProperties(attrEntity, attrRespVo);

        // 查找分类完整路径
        Long catalogId = attrEntity.getCatelogId();
        Long[] catalogPath = attrGroupService.findCatalogPath(catalogId);
        attrRespVo.setCatalogPath(catalogPath);

        // 查找分类名称
        CategoryEntity categoryEntity = categoryService.getById(catalogId);
        if (categoryEntity != null) {
            attrRespVo.setCatalogName(categoryEntity.getName());
        }

        // 判断是否时基本属性, 基本属性才有属性组, 销售属性并没有属性组
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            // 查找attrGroupId
            AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationService.getOne(
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
            if (relationEntity != null) {
                Long attrGroupId = relationEntity.getAttrGroupId();
                attrRespVo.setAttrGroupId(attrGroupId);
                AttrGroupEntity attrGroupEntity = attrGroupService.getById(attrGroupId);
                if (attrGroupEntity != null) {
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }


        return attrRespVo;
    }

    @Transactional
    @Override
    public void updateAttr(AttrVo attrVo) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        this.updateById(attrEntity);

        // 判断是否是修改基本属性, 如果是, 才需要维护中间表
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            // 更新relation表
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            relationEntity.setAttrId(attrVo.getAttrId());

            // 判断是否有记录, 没有则新增(在新建属性的时候有可能没有进行与属性组的关联操作)
            int count = attrAttrgroupRelationService.count(new QueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq("attr_id", attrVo.getAttrId()));
            if (count > 0) {
                attrAttrgroupRelationService.update(relationEntity,
                        new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrVo.getAttrId()));
            } else {
                // 新增
                attrAttrgroupRelationService.save(relationEntity);
            }
        }
    }

    /**
     * 根据分组id查找关联属性
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> entities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));
        List<Long> attrIds = entities.stream().map((attr) -> attr.getAttrId()).collect(Collectors.toList());
        // 根据这个属性查询到的id可能是空的
        if(attrIds == null || attrIds.size() == 0){
            return null;
        }
        return baseMapper.selectBatchIds(attrIds);
    }

    @Override
    public PageUtils getNoRelationsAttr(Map<String, Object> params, Long attrGroupId) {
        // 1.当前分组只能关联属于这个分类下的所有基本属性(销售属性不属于分组)
        AttrGroupEntity groupEntity = attrGroupService.getById(attrGroupId);
        Long catalogId = groupEntity.getCatelogId();
        // 2.当前分组只能关联那些没有被别的分组所引用的属性.(一个基本属性只能属于一个分组)
        // 2.1) 先查询当前这个分类下其他所有的分组
        List<AttrGroupEntity> groups = attrGroupService.list(new QueryWrapper<AttrGroupEntity>().eq("catalog_id", catalogId));
        List<Long> attrGroupIds = groups.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());
        // 2.2) 然后查询这些分组所关联过的所有属性.
        List<AttrAttrgroupRelationEntity> relationEntities = attrAttrgroupRelationService.list(
                new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", attrGroupIds));
        // 2.3) 映射出这些基本属性的ID
        List<Long> attrIds = relationEntities.stream().map(relation -> relation.getAttrId()).distinct().collect(Collectors.toList());
        // 2.4) 从当前分类的所有基本属性中移除这些属性
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>()
                .eq("catalog_id", catalogId)
                .eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (!CollectionUtils.isEmpty(attrIds)) {
            queryWrapper.notIn("attr_id", attrIds);
        }
        // 2.5) 是否带上模糊查询
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(condition -> condition.eq("attr_id", key).or().like("attr_name", key));
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        PageUtils pageUtils = new PageUtils(page);
        return pageUtils;
    }

}
