package com.linqibin.mall.product.controller;

import java.util.Arrays;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.linqibin.common.valid.addGroup;
import com.linqibin.common.valid.updateGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.linqibin.mall.product.entity.BrandEntity;
import com.linqibin.mall.product.service.BrandService;
import com.linqibin.common.utils.PageUtils;
import com.linqibin.common.utils.R;

import javax.validation.Valid;

/**
 * 品牌
 *
 * @author hugh
 * @email linqibin0826@gmail.com
 * @date 2021-01-08 22:12:57
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = brandService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    public R info(@PathVariable("brandId") Long brandId) {
        BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@Validated(value = {addGroup.class}) @RequestBody BrandEntity brand) {
        brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@Validated(value = {updateGroup.class}) @RequestBody BrandEntity brand) {
        brandService.updateDetail(brand);
        return R.ok();
    }

    @PostMapping("/update/status")
    public R updateStatus(@RequestBody BrandEntity brand) {
        Long brandId = brand.getBrandId();
        Integer showStatus = brand.getShowStatus();
        boolean update = brandService.update(new UpdateWrapper<BrandEntity>().set(BrandEntity.BRAND_ID, brandId)
                .set(BrandEntity.SHOW_STATUS, showStatus));
        return update ? R.ok() : R.error();
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] brandIds) {
        brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
