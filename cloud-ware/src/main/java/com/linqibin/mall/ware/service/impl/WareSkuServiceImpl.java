package com.linqibin.mall.ware.service.impl;

import com.linqibin.common.to.es.SkuHasStockVo;
import com.linqibin.common.utils.R;
import com.linqibin.mall.ware.feign.ProductFeignService;
import com.linqibin.mall.ware.service.WareOrderTaskDetailService;
import com.linqibin.mall.ware.service.WareOrderTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linqibin.common.utils.PageUtils;
import com.linqibin.common.utils.Query;

import com.linqibin.mall.ware.dao.WareSkuDao;
import com.linqibin.mall.ware.entity.WareSkuEntity;
import com.linqibin.mall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {


    @Resource
    private WareSkuDao wareSkuDao;

    @Resource
    private ProductFeignService productFeignService;

    /**
     * 商品库存的模糊查询
     * skuId: 1
     * wareId: 1
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        String id = (String) params.get("skuId");
        if(!StringUtils.isEmpty(id)){
            wrapper.eq("sku_id", id);
        }
        id = (String) params.get("wareId");
        if(!StringUtils.isEmpty(id)){
            wrapper.eq("ware_id", id);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }


    /**
     * 添加库存
     * wareId: 仓库id
     * return 返回商品价格
     */
    @Transactional
    @Override
    public double addStock(Long skuId, Long wareId, Integer skuNum) {
        // 1.如果还没有这个库存记录 那就是新增操作
        List<WareSkuEntity> entities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        double price = 0.0;
        // TODO 还可以用什么办法让异常出现以后不回滚？高级
        WareSkuEntity entity = new WareSkuEntity();
        try {
            R info = productFeignService.info(skuId);
            Map<String,Object> data = (Map<String, Object>) info.get("skuInfo");

            if(info.getCode() == 0){
                entity.setSkuName((String) data.get("skuName"));
                // 设置商品价格
                price = (Double) data.get("price");
            }
        }catch (Exception e){
            System.out.println("com.firenay.mall.ware.service.impl.WareSkuServiceImpl：远程调用出错");
        }
        // 新增操作
        if(entities == null || entities.size() == 0){
            entity.setSkuId(skuId);
            entity.setStock(skuNum);
            entity.setWareId(wareId);
            entity.setStockLocked(0);
            wareSkuDao.insert(entity);
        }else {
            wareSkuDao.addStock(skuId, wareId, skuNum);
        }
        return price;
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        List<SkuHasStockVo> stockVos = this.baseMapper.querySkuHashStock(skuIds);
        Map<Long, SkuHasStockVo> mapBySkuId = stockVos.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, v -> v));
        return skuIds.stream().map(skuId -> {
            SkuHasStockVo stockVo = mapBySkuId.get(skuId);
            if (stockVo != null) {
                return stockVo;
            } else {
                SkuHasStockVo vo = new SkuHasStockVo();
                vo.setSkuId(skuId);
                vo.setHasStock(false);
                return vo;
            }
        }).collect(Collectors.toList());
    }

}
