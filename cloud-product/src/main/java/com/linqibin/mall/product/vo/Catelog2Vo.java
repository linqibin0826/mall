package com.linqibin.mall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 首页分类VO
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Catelog2Vo {

    private String id;

    private String name;

    /**
     * 一级分类id
     */
    private String catalog1Id;

    private List<Catalog3Vo> catalog3List;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Catalog3Vo {
        private String id;

        private String name;
    }
}
