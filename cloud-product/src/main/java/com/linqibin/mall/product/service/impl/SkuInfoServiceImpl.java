package com.linqibin.mall.product.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.linqibin.common.to.es.SkuHasStockVo;
import com.linqibin.common.utils.PageUtils;
import com.linqibin.common.utils.Query;
import com.linqibin.mall.product.dao.SkuInfoDao;
import com.linqibin.mall.product.entity.SkuImagesEntity;
import com.linqibin.mall.product.entity.SkuInfoEntity;
import com.linqibin.mall.product.entity.SpuInfoDescEntity;
import com.linqibin.mall.product.feign.WareFeignService;
import com.linqibin.mall.product.service.*;
import com.linqibin.mall.product.vo.SkuItemVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import static java.awt.SystemColor.info;


@Service("skuInfoService")
@Slf4j
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {


    @Autowired
    private SkuImagesService imagesService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private WareFeignService wareFeignService;


    /**
     * 自定义线程串池
     */
    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    /**
     * SKU 区间模糊查询
     * key: 华为
     * catelogId: 225
     * brandId: 2
     * min: 2
     * max: 2
     */
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and(w -> w.eq("sku_id", key).or().like("sku_name", key));
        }
        // 三级id没选择不应该拼这个条件  没选应该查询所有
        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalog_id", catelogId);
        }
        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id", brandId);
        }
        String min = (String) params.get("min");
        if(!StringUtils.isEmpty(min)){
            // gt : 大于;  ge: 大于等于
            wrapper.ge("price", min);
        }
        String max = (String) params.get("max");
        if(!StringUtils.isEmpty(max)){
            try {
                BigDecimal bigDecimal = new BigDecimal(max);
                if(bigDecimal.compareTo(new BigDecimal("0")) == 1){
                    // le: 小于等于
                    wrapper.le("price", max);
                }
            } catch (Exception e) {
                System.out.println("com.firenay.mall.product.service.impl.SkuInfoServiceImpl：前端传来非数字字符");
            }
        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        return this.list(new LambdaQueryWrapper<SkuInfoEntity>()
                .eq(SkuInfoEntity::getSpuId, spuId));
    }

    /**
     * 查询页面详细内容
     */
    @Override
    public SkuItemVo item(Long skuId) {
        long start = System.currentTimeMillis();
        SkuItemVo resultVo = new SkuItemVo();

        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            // 1. sku的基本信息 pms_sku_info
            SkuInfoEntity info = getById(skuId);
            resultVo.setInfo(info);
            return info;
        });

        CompletableFuture<Void> saleAttrFuture = infoFuture.thenAcceptAsync((res) -> {
            // 3. spu的销售属性集合
            List<SkuItemVo.SpuItemSaleAttrVo> saleAttrs = skuSaleAttrValueService.listSpuSaleAttrsInfo(res.getSpuId());
            resultVo.setSaleAttr(saleAttrs);
        });

        CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((res) -> {
            // 4. spu的描述信息
            //4 获取spu介绍
            SpuInfoDescEntity spuInfo = spuInfoDescService.getById(res.getSpuId());
            resultVo.setDesp(spuInfo);
        });

        CompletableFuture<Void> groupAttrFuture = infoFuture.thenAcceptAsync((res) -> {
            // 5. 获取spu的规格属性
            List<SkuItemVo.SpuItemAttrGroupVo> groupAttrs = attrGroupService.listAttrGroupBySpuId(res.getSpuId());
            resultVo.setGroupAttrs(groupAttrs);
        });

        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
            // 2. sku的图片信息
            List<SkuImagesEntity> imagesBySkuId = imagesService.getImagesBySkuId(skuId);
            resultVo.setImages(imagesBySkuId);
        });
        // 6. 库存状态
        List<SkuHasStockVo> stockInfo = wareFeignService.getSkuHasStock(Lists.newArrayList(skuId)).getData(new TypeReference<List<SkuHasStockVo>>() {});
        if (CollectionUtil.isNotEmpty(stockInfo)) {
            resultVo.setHasStock(stockInfo.get(0).getHasStock());
        }
        CompletableFuture.allOf(saleAttrFuture, imageFuture, groupAttrFuture, descFuture).join();
        long end = System.currentTimeMillis();
        log.info("本次查询商品详情总耗时:{}", end - start + "ms");
        return resultVo;
    }



}
