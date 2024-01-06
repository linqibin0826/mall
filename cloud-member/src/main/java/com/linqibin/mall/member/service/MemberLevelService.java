package com.linqibin.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linqibin.common.utils.PageUtils;
import com.linqibin.mall.member.domain.entity.MemberLevelEntity;

import java.util.Map;

/**
 * 会员等级
 *
 * @author hugh
 * @email linqibin0826@gmail.com
 * @date 2021-01-10 20:03:40
 */
public interface MemberLevelService extends IService<MemberLevelEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取默认等级
     * @return 默认等级
     */
    MemberLevelEntity getDefaultLevel();
}

