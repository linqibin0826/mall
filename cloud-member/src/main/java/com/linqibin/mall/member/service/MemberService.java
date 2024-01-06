package com.linqibin.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linqibin.common.utils.PageUtils;
import com.linqibin.mall.member.domain.dto.MemberRegisterDTO;
import com.linqibin.mall.member.domain.dto.OauthLoginDTO;
import com.linqibin.mall.member.domain.dto.UserLoginDTO;
import com.linqibin.mall.member.domain.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author hugh
 * @email linqibin0826@gmail.com
 * @date 2021-01-10 20:03:41
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 注册
     * @param registerDTO 注册信息
     */
    void register(MemberRegisterDTO registerDTO);

    MemberEntity login(UserLoginDTO userLoginDTO);

    /**
     * 社交账号登录
     *
     * @param oauthLoginDTO 社交账号登录信息
     * @return
     */
    MemberEntity login(OauthLoginDTO oauthLoginDTO);
}

