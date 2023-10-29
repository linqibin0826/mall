package com.linqibin.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linqibin.common.utils.PageUtils;
import com.linqibin.mall.ware.entity.PurchaseEntity;
import com.linqibin.mall.ware.vo.MergeVo;
import com.linqibin.mall.ware.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author hugh
 * @email linqibin0826@gmail.com
 * @date 2021-01-10 20:05:37
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceivePurchase(Map<String, Object> params);

    void mergePurchase(MergeVo mergeVo);


    void received(List<Long> ids);

    void done(PurchaseDoneVo doneVo);
}

