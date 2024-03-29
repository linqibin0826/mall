package com.linqibin.mall.ware.controller;

import com.linqibin.common.to.es.SkuHasStockVo;
import com.linqibin.common.utils.PageUtils;
import com.linqibin.common.utils.R;
import com.linqibin.mall.ware.entity.WareSkuEntity;
import com.linqibin.mall.ware.service.WareSkuService;
import com.linqibin.mall.ware.vo.WareSkuLockVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author firenay
 * @email 1046762075@qq.com
 * @date 2020-06-06 21:09:28
 */
@Slf4j
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {

    @Autowired
    private WareSkuService wareSkuService;


	/**
	 * 查询sku是否有库存
	 * 返回当前id stock量
	 */
	@PostMapping("/hasStock")
	public R getSkuHasStock(@RequestBody List<Long> SkuIds){
		List<SkuHasStockVo> vos = wareSkuService.getSkuHasStock(SkuIds);
		return R.ok().setData(vos);
	}

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);
        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }
}
