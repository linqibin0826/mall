package com.linqibin.mall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.linqibin.mall.product.entity.AttrAttrgroupRelationEntity;
import com.linqibin.mall.product.entity.AttrEntity;
import com.linqibin.mall.product.service.AttrService;
import com.linqibin.mall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.linqibin.mall.product.entity.AttrGroupEntity;
import com.linqibin.mall.product.service.AttrGroupService;
import com.linqibin.common.utils.PageUtils;
import com.linqibin.common.utils.R;



/**
 * 属性分组
 *
 * @author hugh
 * @email linqibin0826@gmail.com
 * @date 2021-01-08 22:12:57
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private AttrService attrService;

    /**
     * 查找指定属性分组Id下的所有基本属性
     */
    @GetMapping("{attrGroupId}/attr/relation")
    public R attrRelations(@PathVariable("attrGroupId") Long attrGroupId) {
        List<AttrEntity> data = attrGroupService.getRelationByGroupId(attrGroupId);
        return R.ok().put("data", data);
    }

    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable("catelogId") Long catelogId){
        // 1.查询当前分类下的所有属性分组
        List<AttrGroupWithAttrsVo> vos = attrGroupService.getAttrGroupWithAttrByCatelogId(catelogId);
        // 2.查询每个分组的所有信息
        return R.ok().put("data", vos);
    }

    /**
     * 查询指定属性分组Id下所有可关联基本属性
     */
    @GetMapping("{attrGroupId}/noattr/relation")
    public R attrNoRelations(@RequestParam Map<String, Object> params,
                             @PathVariable("attrGroupId") Long attrGroupId) {
        PageUtils page = attrService.getNoRelationsAttr(params, attrGroupId);
        return R.ok().put("page", page);
    }

    @PostMapping("/attr/relation")
    public R addRelation(@RequestBody List<AttrAttrgroupRelationEntity> relations) {
        attrGroupService.addRelation(relations);
        return R.ok();
    }



    /**
     * 列表
     */
    @RequestMapping("/list/{catalogId}")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable("catalogId") Long catalogId){
        PageUtils page = attrGroupService.queryPage(params, catalogId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
		// 分类级联选择器路径
        attrGroup.setCatalogPath(attrGroupService.findCatalogPath(attrGroup.getCatelogId()));
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    @PostMapping("/attr/relation/delete")
    public R deleteRelations(@RequestBody List<AttrAttrgroupRelationEntity> relations) {
        System.out.println(relations);
        attrGroupService.deleteRelations(relations);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
