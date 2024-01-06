package com.linqibin.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.linqibin.mall.product.service.SkuSaleAttrValueService;
import com.linqibin.mall.product.vo.SkuItemVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linqibin.common.utils.PageUtils;
import com.linqibin.common.utils.Query;

import com.linqibin.mall.product.dao.SkuSaleAttrValueDao;
import com.linqibin.mall.product.entity.SkuSaleAttrValueEntity;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }


    /**
     * 找到指定spuId下所有的sku属性组合
     * @param spuId
     * @return
     */
    public List<SkuItemVo.SpuItemSaleAttrVo> listSpuSaleAttrsInfo(Long spuId) {
        return baseMapper.querySpuSaleAttrs(spuId);
    }

    @Override
    public List<String> saleAttrStringListBySkuId(Long skuId) {
        List<String> result = Lists.newArrayList();
        List<SkuSaleAttrValueEntity> valueEntities = baseMapper.selectList(new LambdaQueryWrapper<SkuSaleAttrValueEntity>().eq(SkuSaleAttrValueEntity::getSkuId, skuId));
        valueEntities.forEach(item -> {
            result.add(item.getAttrName() + ":" + item.getAttrValue());
        });
        return result;
    }

}
