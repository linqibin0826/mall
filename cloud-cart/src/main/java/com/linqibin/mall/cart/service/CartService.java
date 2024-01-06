package com.linqibin.mall.cart.service;

import com.linqibin.mall.cart.domain.vo.CartItemVo;
import com.linqibin.mall.cart.domain.vo.CartVo;

import java.util.concurrent.ExecutionException;

public interface CartService {

    /**
     * 将商品添加至购物车
     *
     * @param skuId 商品skuId
     * @param num   数量
     * @return
     */
    CartItemVo addSkuToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    /**
     * 从购物车中查找指定商品
     * @param skuId
     * @return
     */
    CartItemVo getSkuInfoFromCart(Long skuId);

    /**
     * 获取购物车页面信息
     * @return
     */
    CartVo getCart();

    /**
     * 根据购物车标识，清除购物车
     * @param cartKey
     * @return
     */
    boolean clearCartByKey(String cartKey);

    /**
     * 更新购物车中的商品是否选中
     *
     * @param skuId
     * @param status
     * @return
     */
    boolean updateCheckStatus(Long skuId, boolean status);

    /**
     * 更改购物车商品数量
     * @param skuId
     * @param count
     */
    void changeCount(Long skuId, Integer count);

    /**
     * 从购物车里面删除sku
     * @param skuId
     */
    void deleteSkuFromCart(Long skuId);
}
