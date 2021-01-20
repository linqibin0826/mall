package com.linqibin.mall.member.dao;

import com.linqibin.mall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author hugh
 * @email linqibin0826@gmail.com
 * @date 2021-01-10 20:03:41
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
