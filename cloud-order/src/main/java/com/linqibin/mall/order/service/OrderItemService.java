package com.linqibin.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linqibin.common.utils.PageUtils;
import com.linqibin.mall.order.entity.OrderItemEntity;

import java.util.Map;

/**
 * 订单项信息
 *
 * @author hugh
 * @email linqibin0826@gmail.com
 * @date 2021-01-10 20:01:23
 */
public interface OrderItemService extends IService<OrderItemEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

