package com.linqibin.mall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.linqibin.mall.member.dao.MemberLevelDao;
import com.linqibin.mall.member.domain.entity.MemberLevelEntity;
import com.linqibin.mall.member.service.MemberLevelService;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linqibin.common.utils.PageUtils;
import com.linqibin.common.utils.Query;


@Service("memberLevelService")
public class MemberLevelServiceImpl extends ServiceImpl<MemberLevelDao, MemberLevelEntity> implements MemberLevelService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberLevelEntity> page = this.page(
                new Query<MemberLevelEntity>().getPage(params),
                new QueryWrapper<MemberLevelEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public MemberLevelEntity getDefaultLevel() {
        return baseMapper.selectOne(new LambdaQueryWrapper<MemberLevelEntity>()
                .eq(MemberLevelEntity::getDefaultStatus, MemberLevelEntity.DEFAULT_STATUS));
    }

}
