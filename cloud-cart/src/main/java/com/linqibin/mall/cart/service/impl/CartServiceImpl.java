package com.linqibin.mall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.linqibin.common.constant.CartConstants;
import com.linqibin.mall.cart.domain.dto.SkuInfoDTO;
import com.linqibin.mall.cart.domain.to.UserInfoTo;
import com.linqibin.mall.cart.domain.vo.CartItemVo;
import com.linqibin.mall.cart.domain.vo.CartVo;
import com.linqibin.mall.cart.feign.ProductFeignClient;
import com.linqibin.mall.cart.interceptor.CartInterceptor;
import com.linqibin.mall.cart.service.CartService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 *
 * @author linqibin
 * @date   2024/1/6 12:19
 * @email  1214219989@qq.com
 */
@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {

    private RedisTemplate<String, String> redisTemplate;

    private ProductFeignClient productFeignClient;


    private ThreadPoolExecutor myThreadPool;

    /**
     * 1.如果登录了， 往在线购物车里面添加。否则，添加往临时购物车里添加
     * 2.获取当前用户的购物车（boundHashOps），判断当前购物车里面，有没有这个商品， 如果有，只需要变更数量即可。
     *
     * @param skuId 商品skuId
     * @param num   数量
     * @return
     */
    @Override
    public CartItemVo addSkuToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, String, String> cart = getCurrentUserCart();
        String cartItemFromRedis = cart.get(skuId.toString());
        if (StringUtils.isNotBlank(cartItemFromRedis)) {
            CartItemVo itemVo = JSON.parseObject(cartItemFromRedis, CartItemVo.class);
            // 如果当前购物车已经有该商品，直接添加数量
            itemVo.setCount(itemVo.getCount() + num);
            cartItemFromRedis = JSON.toJSONString(itemVo);
            cart.put(skuId.toString(), cartItemFromRedis);
            return itemVo;
        } else {
            CartItemVo cartItem = new CartItemVo();
            // 调用远程服务查询信息，并保存到购物车。
            CompletableFuture<Void> task1 = CompletableFuture.runAsync(() -> {
                SkuInfoDTO data = productFeignClient.info(skuId).getData("skuInfo", new TypeReference<SkuInfoDTO>() {});
                cartItem.setSkuId(skuId);
                cartItem.setPrice(data.getPrice());
                cartItem.setImage(data.getSkuDefaultImg());
                cartItem.setCheck(true);
                cartItem.setSkuTile(data.getSkuTitle());
                cartItem.setCount(num);
            }, myThreadPool);

            CompletableFuture<Void> task2 = CompletableFuture.runAsync(() -> {
                List<String> saleAttrValues = productFeignClient.getSkuSaleAttrValues(skuId).getData(new TypeReference<List<String>>() {
                });
                cartItem.setSkuAttr(saleAttrValues);
            }, myThreadPool);

            CompletableFuture.allOf(task1, task2).get();
            // 序列化成json
            cartItemFromRedis = JSON.toJSONString(cartItem);
            cart.put(skuId.toString(), cartItemFromRedis);
            return cartItem;
        }
    }

    @Override
    public CartItemVo getSkuInfoFromCart(Long skuId) {
        BoundHashOperations<String, String, String> cart = getCurrentUserCart();
        String itemString = cart.get(skuId.toString());
        return JSON.parseObject(itemString, CartItemVo.class);
    }

    /**
     * 判断用户是否登录， 如果没登录， 获取临时购物车的所有购物项。
     * 如果登录，判断临时购物车中是否还有数据，有则合并,并且清空对应的临时购物车
     * @return
     */
    @Override
    public CartVo getCart() {
        UserInfoTo info = CartInterceptor.CURRENT_USER_INFO.get();
        // 登录没登录 都先获取临时购物车
        CartVo tempCart = getCartVoByCartKey(CartConstants.CART_PREFIX + info.getUserKey());
        if (info.getUserId() != null) {
            // 已登录用户购物车
            CartVo signedCart = getCartVoByCartKey(CartConstants.CART_PREFIX + info.getUserId());
            if (CollectionUtils.isNotEmpty(tempCart.getItems())) {
                // 合并临时购物车， 并删除临时购物车内容
                signedCart.getItems().addAll(tempCart.getItems());
                this.clearCartByKey(CartConstants.CART_PREFIX + info.getUserKey());
            }
            return signedCart;
        } else {
            return tempCart;
        }
    }

    @Override
    public boolean clearCartByKey(String cartKey) {
        return redisTemplate.delete(cartKey);
    }

    @Override
    public boolean updateCheckStatus(Long skuId, boolean status) {
        BoundHashOperations<String, String, String> cart = getCurrentUserCart();
        String itemString = cart.get(skuId.toString());
        CartItemVo itemVo = JSON.parseObject(itemString, CartItemVo.class);
        itemVo.setCheck(status);
        String updated = JSON.toJSONString(itemVo);
        // 更新
        cart.put(skuId.toString(), updated);
        return true;
    }

    @Override
    public void changeCount(Long skuId, Integer count) {
        BoundHashOperations<String, String, String> cart = getCurrentUserCart();
        String itemString = cart.get(skuId.toString());
        CartItemVo itemVo = JSON.parseObject(itemString, CartItemVo.class);
        itemVo.setCount(count);
        String updated = JSON.toJSONString(itemVo);
        cart.put(skuId.toString(), updated);
    }

    @Override
    public void deleteSkuFromCart(Long skuId) {
        BoundHashOperations<String, String, String> cart = getCurrentUserCart();
        cart.delete(skuId.toString());
    }

    /**
     * 根据用户获取其购物车VO对象
     * @param cartKey 用户的购物车key
     * @return 购物车VO
     */
    private CartVo getCartVoByCartKey(String cartKey) {
        CartVo cartVo = new CartVo();
        BoundHashOperations<String, String, String> unsignedCart = redisTemplate.boundHashOps(cartKey);
        List<String> cartItemList = unsignedCart.values();
        if (CollectionUtils.isNotEmpty(cartItemList)) {
            List<CartItemVo> itemVos = cartItemList.stream().map(item -> JSON.parseObject(item, CartItemVo.class)).collect(Collectors.toList());
            cartVo.setItems(itemVos);
        }
        return cartVo;
    }

    /**
     * 返回当前用户的购物车
     * @return
     */
    private BoundHashOperations<String, String, String> getCurrentUserCart() {
        UserInfoTo info = CartInterceptor.CURRENT_USER_INFO.get();
        String cartKey = CartConstants.CART_PREFIX;
        if (info.getUserId() != null) {
            // 已登录用户购物车
            cartKey += info.getUserId();
        } else {
            cartKey += info.getUserKey();
        }
        return redisTemplate.boundHashOps(cartKey);
    }
}
