package com.linqibin.mall.product.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.linqibin.common.to.SkuReductionTO;
import com.linqibin.common.to.SpuBoundTO;
import com.linqibin.common.to.es.SkuEsModel;
import com.linqibin.common.to.es.SkuHasStockVo;
import com.linqibin.common.utils.PageUtils;
import com.linqibin.common.utils.Query;
import com.linqibin.common.utils.R;
import com.linqibin.mall.product.dao.SpuInfoDao;
import com.linqibin.mall.product.entity.*;
import com.linqibin.mall.product.feign.CouponFeignService;
import com.linqibin.mall.product.feign.SearchFeignService;
import com.linqibin.mall.product.feign.WareFeignService;
import com.linqibin.mall.product.service.*;
import com.linqibin.mall.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.linqibin.common.utils.R.CODE_SUCCESS;
import static com.linqibin.mall.product.entity.SpuInfoEntity.STATUS_UP;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService attrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    /**
     * feign 远程调用优惠券服务
     */
    @Autowired
    private CouponFeignService couponFeignService;

    @Resource
    private WareFeignService wareFeignService;

    @Resource
    private BrandService brandService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }


    /**
     * 保存所有数据 [33kb左右]
     */
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {

        // 1.保存spu基本信息 pms_sku_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        BeanUtils.copyProperties(vo, spuInfoEntity);
        this.saveBatchSpuInfo(spuInfoEntity);
        // 2.保存spu的表述图片  pms_spu_info_desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        // String join 的方式将它们用逗号分隔
        spuInfoDescEntity.setDecript(String.join(",", decript));
        spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);
        // 3.保存spu的图片集  pms_sku_images

        // 先获取所有图片
        List<String> images = vo.getImages();
        // 保存图片的时候 并且保存这个是那个spu的图片
        spuImagesService.saveImages(spuInfoEntity.getId() ,images);
        // 4.保存spu的规格属性  pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            valueEntity.setAttrId(attr.getAttrId());
            // 可能页面没用传入属性名字 根据属性id查到所有属性 给名字赋值
            AttrEntity attrEntity = attrService.getById(attr.getAttrId());
            valueEntity.setAttrName(attrEntity.getAttrName());
            valueEntity.setAttrValue(attr.getAttrValues());
            valueEntity.setQuickShow(attr.getShowDesc());
            valueEntity.setSpuId(spuInfoEntity.getId());

            return valueEntity;
        }).collect(Collectors.toList());
        attrValueService.saveProductAttr(collect);
        // 5.保存当前spu对应所有sku信息
        Bounds bounds = vo.getBounds();
        SpuBoundTO spuBoundTO = new SpuBoundTO();
        BeanUtils.copyProperties(bounds, spuBoundTO);
        spuBoundTO.setSpuId(spuInfoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundTO);
        if(r.getCode() != 0){
            log.error("远程保存spu积分信息失败");
        }
        // 1).spu的积分信息 sms_spu_bounds
        List<Skus> skus = vo.getSkus();
        if(skus != null && skus.size() > 0){
            // 提前查找默认图片
            skus.forEach(item -> {
                String dufaultImg = "";
                for (Images img : item.getImages()) {
                    if(img.getDefaultImg() == 1){
                        dufaultImg = img.getImgUrl();
                    }
                }
                // 2).基本信息的保存 pms_sku_info
                // skuName 、price、skuTitle、skuSubtitle 这些属性需要手动保存
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item, skuInfoEntity);
                // 设置spu的品牌id
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(dufaultImg);
                skuInfoEntity.setSaleCount((long) (Math.random()*2888));
                skuInfoService.saveSkuInfo(skuInfoEntity);

                // 3).保存sku的图片信息  pms_sku_images
                // sku保存完毕 自增主键就出来了 收集所有图片
                Long skuId = skuInfoEntity.getSkuId();
                List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity ->
                        // 返回true就会保存 返回false就会过滤
                        !StringUtils.isEmpty(entity.getImgUrl())
                ).collect(Collectors.toList());
                skuImagesService.saveBatch(imagesEntities);

                // 4).sku的销售属性  pms_sku_sale_attr_value
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(a -> {
                    // 对拷页面传过来的三个属性
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                // 5.) sku的优惠、满减、会员价格等信息  [跨库]
                SkuReductionTO skuReductionTO = new SkuReductionTO();
                BeanUtils.copyProperties(item, skuReductionTO);
                skuReductionTO.setSkuId(skuId);
                if(skuReductionTO.getFullCount() > 0 || (skuReductionTO.getFullPrice().compareTo(new BigDecimal("0")) > 0)){
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTO);
                    if(r1.getCode() != 0){
                        log.error("远程保存sku优惠信息失败");
                    }
                }
            });
        }
    }


    /**
     * spu管理模糊查询
     */
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        // 根据 spu管理带来的条件进行叠加模糊查询
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and(w -> w.eq("id", key).or().like("spu_name",key));
        }

        String status = (String) params.get("status");
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("publish_status", status);
        }

        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id", brandId);
        }

        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalog_id", catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    @Override
    public void saveBatchSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    /**
     * 不一样的属性：skuPrice、skuImg、hasStock、hotScore、
     * 			brandName、brandImg、catalogName、attrs
     */
    @Override
    public void up(Long spuId) {
        // 1. 查出该spu对应的所有sku信息、品牌信息、分类信息
        List<SkuInfoEntity> skuList = skuInfoService.getSkusBySpuId(spuId);
        Assert.notEmpty(skuList, "未找到spuId为" + spuId + "的sku集合。");
        SpuInfoEntity spuInfo = this.getById(spuId);
        BrandEntity brandInfo = brandService.getById(spuInfo.getBrandId());
        CategoryEntity catalogInfo = categoryService.getById(spuInfo.getCatalogId());
        // 2. 查询库存信息。
        List<Long> skuIds = skuList.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        List<SkuHasStockVo> hasStockVos = wareFeignService.getSkuHasStock(skuIds).getData(new TypeReference<List<SkuHasStockVo>>(){});
        Map<Long, Boolean> stockMapBySkuId = hasStockVos.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));

        // 3. 查询可检索属性
        List<SkuEsModel.Attr> attrs = null;
        List<ProductAttrValueEntity> attrListForSpu = attrValueService.baseAttrListForSpu(spuId);
        if (CollectionUtil.isNotEmpty(attrListForSpu)) {
            List<Long> attrIds = attrListForSpu.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());
            List<Long> searchAbleIds = attrService.selectSearchAttrIds(attrIds);
            attrs = attrListForSpu.stream().filter(attr -> searchAbleIds.contains(attr.getAttrId()))
                    // 将属性实体映射成ES的属性VO
                    .map(attrEntity -> {
                        SkuEsModel.Attr attr = new SkuEsModel.Attr();
                        BeanUtils.copyProperties(attrEntity, attr);
                        return attr;
                    }).collect(Collectors.toList());
        }

        List<SkuEsModel> esModels = Lists.newArrayList();
        for (SkuInfoEntity skuInfo : skuList) {
            // 先进行属性复制，对于类型名字不同的属性，再进行单独处理
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(skuInfo, esModel);
            esModel.setSkuPrice(skuInfo.getPrice());
            esModel.setSkuImg(skuInfo.getSkuDefaultImg());
            // 远程库存服务查询的结果
            esModel.setHasStock(stockMapBySkuId.get(skuInfo.getSkuId()));
            // TODO
            esModel.setHotScore(BigDecimal.ZERO);
            esModel.setBrandName(brandInfo.getName());
            esModel.setBrandImg(brandInfo.getLogo());
            esModel.setCatalogName(catalogInfo.getName());
            esModel.setAttrs(attrs);
            esModels.add(esModel);
        }

        // 4. 调用搜索服务进行上架
        if (CODE_SUCCESS.equals(searchFeignService.productStatusUp(esModels).getCode())) {
            // 修改当前spu状态为已上架
            baseMapper.updateSpuStatus(spuId, STATUS_UP);
        } else {
            // 远程调用失败
            // TODO 重复调用？ 借口幂等性问题
        }
    }

}
