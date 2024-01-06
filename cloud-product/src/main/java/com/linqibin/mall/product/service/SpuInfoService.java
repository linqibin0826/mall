package com.linqibin.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linqibin.common.utils.PageUtils;
import com.linqibin.mall.product.entity.SpuInfoEntity;
import com.linqibin.mall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author hugh
 * @email linqibin0826@gmail.com
 * @date 2021-01-08 22:12:57
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo spuSaveVo);


    /**
     * SPU模糊查询
     */
    PageUtils queryPageByCondition(Map<String, Object> params);

    void saveBatchSpuInfo(SpuInfoEntity spuInfoEntity);

    /**
     * 将指定spu下的所有sku上架（存储到es）
     * @param spuId        待上架商品id
     */
    void up(Long spuId);
}
