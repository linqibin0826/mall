package com.linqibin.mall.order.dao;

import com.linqibin.mall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author hugh
 * @email linqibin0826@gmail.com
 * @date 2021-01-10 20:01:23
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
