package com.linqibin.mall.product.service.impl;

import com.linqibin.mall.product.service.SkuSaleAttrValueService;
import com.linqibin.mall.product.vo.ItemSaleAttrVo;
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

    @Override
    public List<ItemSaleAttrVo> getSaleAttrsBuSpuId(Long spuId) {

        SkuSaleAttrValueDao dao = this.baseMapper;
        return dao.getSaleAttrsBuSpuId(spuId);
    }
}
