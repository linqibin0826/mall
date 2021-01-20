package com.linqibin.mall.coupon.dao;

import com.linqibin.mall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author hugh
 * @email linqibin0826@gmail.com
 * @date 2021-01-10 19:56:42
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
