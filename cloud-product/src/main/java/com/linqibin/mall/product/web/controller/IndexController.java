package com.linqibin.mall.product.web.controller;

import com.linqibin.mall.product.entity.CategoryEntity;
import com.linqibin.mall.product.service.CategoryService;
import com.linqibin.mall.product.vo.Catelog2Vo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Resource
    private CategoryService categoryService;

    @GetMapping({"/", "/index"})
    public String getIndexPage(Model model) {
        List<CategoryEntity> levelOneList = categoryService.queryLevelOneCategories();
        model.addAttribute("categorys", levelOneList);
        return "index";
    }

    @GetMapping("/index/catalog.json")
    @ResponseBody
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        return categoryService.getCatalogJson();
    }
}
