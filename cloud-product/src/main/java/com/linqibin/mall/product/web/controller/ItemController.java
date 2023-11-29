package com.linqibin.mall.product.web.controller;

import com.linqibin.mall.product.entity.CategoryEntity;
import com.linqibin.mall.product.service.CategoryService;
import com.linqibin.mall.product.service.SkuInfoService;
import com.linqibin.mall.product.vo.Catelog2Vo;
import com.linqibin.mall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Controller
public class ItemController {

    @Autowired
    private SkuInfoService skuInfoService;

    @RequestMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {

        SkuItemVo vo = skuInfoService.item(skuId);

        model.addAttribute("item", vo);
        return "item";
    }

}
