package com.linqibin.mall.cart.domain.vo;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 整个购物车
 * 需要计算的属性， 必须重写get方法。
 * @author linqibin
 * @date   2024/1/5 23:39
 * @email  1214219989@qq.com
 */
@Data
public class CartVo {

    private List<CartItemVo> items;
    private Integer countNum;
    private Integer countType;
    private BigDecimal totalAmount;
    private BigDecimal reduce = new BigDecimal("0");

    public Integer getCountNum() {
        countNum = 0;
        if (CollectionUtils.isNotEmpty(items)) {
            items.forEach(item -> countNum += item.getCount());
        }
        return countNum;
    }

    public Integer getCountType() {
        countType = 0;
        if (CollectionUtils.isNotEmpty(items)) {
            items.forEach(item -> countType++);
        }
        return countType;
    }

    public BigDecimal getTotalAmount() {
        totalAmount = new BigDecimal("0");
        if (CollectionUtils.isNotEmpty(items)) {
            items.forEach(item -> totalAmount = totalAmount.add(item.getTotalPrice()));
        }
        // 减去优惠总价
        return totalAmount.subtract(getReduce());
    }
}
