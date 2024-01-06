package com.linqibin.mall.member.dao;

import com.linqibin.mall.member.domain.entity.MemberLoginLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员登录记录
 *
 * @author hugh
 * @email linqibin0826@gmail.com
 * @date 2021-01-10 20:03:40
 */
@Mapper
public interface MemberLoginLogDao extends BaseMapper<MemberLoginLogEntity> {

}
