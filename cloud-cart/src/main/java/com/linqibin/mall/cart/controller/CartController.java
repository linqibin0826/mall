package com.linqibin.mall.cart.controller;

import com.linqibin.mall.cart.domain.vo.CartItemVo;
import com.linqibin.mall.cart.domain.vo.CartVo;
import com.linqibin.mall.cart.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author linqibin
 * @date 2024/1/6 00:36
 * @email 1214219989@qq.com
 */
@Controller
@RequestMapping
@AllArgsConstructor
public class CartController {

    private CartService cartService;

    @GetMapping("/cart.html")
    public String cartListPage(Model model) {
        CartVo cart = cartService.getCart();
        model.addAttribute("cart", cart);
        return "cartList";
    }

    @GetMapping("/updateChecked/{skuId}")
    public String updateChecked(@PathVariable("skuId") Long skuId, @RequestParam("status") boolean status) {
        cartService.updateCheckStatus(skuId, status);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("/changeCount/{skuId}")
    public String changeCount(@PathVariable("skuId") Long skuId, @RequestParam("count") Integer count) {
        if (count > 0) {
            cartService.changeCount(skuId, count);
        }
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @RequestMapping("/delete/{skuId}")
    public String deleteFromCart(@PathVariable("skuId") Long skuId) {
        cartService.deleteSkuFromCart(skuId);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num) throws Exception {
        cartService.addSkuToCart(skuId, num);
        return "redirect:http://cart.gulimall.com/addToCartSuccess.html?skuId=" + skuId;
    }

    /**
     * 把商品添加到购物车后，重定向到此接口， 防止刷新页面一只添加购物车
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId, Model model) {
        CartItemVo item = cartService.getSkuInfoFromCart(skuId);
        model.addAttribute("item", item);
        return "success";
    }
}
