package com.linqibin.mall.search.vo;

import lombok.Data;

@Data
public class BrandVo {

    /**
     * "brandId": 0,
     * "brandName": "string",
     */
    private Long brandId;

    private String brandName;

    /**
     * 品牌图片信息
     */
    private String brandImg;
}
