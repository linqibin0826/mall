package com.linqibin.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linqibin.common.utils.PageUtils;
import com.linqibin.mall.product.entity.SpuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * spu图片
 *
 * @author hugh
 * @email linqibin0826@gmail.com
 * @date 2021-01-08 22:12:57
 */
public interface SpuImagesService extends IService<SpuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveImages(Long id, List<String> images);

}

