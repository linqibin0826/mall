package com.linqibin.mall.product.vo;

import com.linqibin.mall.product.entity.AttrEntity;
import lombok.Data;

/**
 *
 * @author hugh&you
 * @since 2021/1/24 19:21
 */
@Data
public class AttrVo extends AttrEntity  {

    /**
     * 属性组ID,
     */
    private Long attrGroupId;
}
