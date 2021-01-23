package com.linqibin.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linqibin.common.utils.PageUtils;
import com.linqibin.common.utils.Query;
import com.linqibin.mall.product.dao.AttrGroupDao;
import com.linqibin.mall.product.entity.AttrGroupEntity;
import com.linqibin.mall.product.entity.CategoryEntity;
import com.linqibin.mall.product.service.AttrGroupService;
import com.linqibin.mall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private CategoryService categoryService;

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

    @Override
    public Long[] getCatalogPath(Long catalogId) {
        List<Long> longs = new ArrayList<>();
        CategoryEntity byId = categoryService.getById(catalogId);
        Long parentCid = byId.getParentCid();
        longs.add(catalogId);
        while (parentCid != 0) {
            longs.add(parentCid);
            parentCid = categoryService.getById(parentCid).getParentCid();
        }
        Collections.reverse(longs);
        return longs.toArray(new Long[longs.size()]);
    }
}