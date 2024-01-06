package com.linqibin.mall.cart.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物项内容
 * @author linqibin
 * @date   2024/1/5 23:39
 * @email  1214219989@qq.com
 */
@Data
public class CartItemVo {

    private Long skuId;
    private String skuTile;
    private Boolean check = true;
    private String image;
    private List<String> skuAttr;
    private BigDecimal price;
    private Integer count;
    private BigDecimal totalPrice;

    public BigDecimal getTotalPrice() {
        return this.price.multiply(new BigDecimal("" + count));
    }
}
