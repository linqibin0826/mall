package com.linqibin.mall.ware.service;

import com.google.common.collect.Lists;
import com.linqibin.common.to.es.SkuHasStockVo;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class WareSkuServiceTest {

    @Resource
    private WareSkuService wareSkuService;

    @Test
    public void getSkuHasStock() {
        List<SkuHasStockVo> hasStockVos = wareSkuService.getSkuHasStock(Lists.newArrayList(1L, 2L, 3L));
        Assert.assertTrue(CollectionUtils.isNotEmpty(hasStockVos));
    }
}
