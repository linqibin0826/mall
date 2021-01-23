package com.linqibin.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linqibin.common.utils.PageUtils;
import com.linqibin.mall.product.entity.AttrGroupEntity;

import java.util.Map;

/**
 * 属性分组
 *
 * @author hugh
 * @email linqibin0826@gmail.com
 * @date 2021-01-08 22:12:57
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params, Long catalogId);

    /**
     * 查找目录的完整路径  e.g. [2, 34, 225]
     *
     * @param catalogId the catalog id
     * @return the long [ ]
     * @author hugh &you
     * @since 2021 /1/23 12:13
     */
    Long[] getCatalogPath(Long catalogId);
}

