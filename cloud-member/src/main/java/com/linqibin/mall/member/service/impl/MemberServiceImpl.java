package com.linqibin.mall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.linqibin.mall.member.dao.MemberDao;
import com.linqibin.mall.member.dao.MemberOauthDao;
import com.linqibin.mall.member.domain.dto.MemberRegisterDTO;
import com.linqibin.mall.member.domain.dto.OauthLoginDTO;
import com.linqibin.mall.member.domain.dto.UserLoginDTO;
import com.linqibin.mall.member.domain.entity.MemberEntity;
import com.linqibin.mall.member.domain.entity.MemberLevelEntity;
import com.linqibin.mall.member.domain.entity.MemberOauthEntity;
import com.linqibin.mall.member.exception.EmailExistException;
import com.linqibin.mall.member.exception.LoginException;
import com.linqibin.mall.member.exception.UsernameExistException;
import com.linqibin.mall.member.service.MemberLevelService;
import com.linqibin.mall.member.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linqibin.common.utils.PageUtils;
import com.linqibin.common.utils.Query;
import org.springframework.transaction.annotation.Transactional;


/**
 * 会员信息服务
 *
 * @author linqibin
 * @date 2023/12/30 00:02
 * @email 1214219989@qq.com
 */
@Service("memberService")
@AllArgsConstructor
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    private final MemberLevelService memberLevelService;

    private final MemberOauthDao oauthDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(MemberRegisterDTO registerDTO) {
        MemberEntity domain = new MemberEntity();

        // 检查用户名和邮箱是否唯一
        checkUsernameUnique(registerDTO.getUsername());
        checkEmailUnique(registerDTO.getEmail());

        // 设置新用户的默认等级
        MemberLevelEntity defaultLevel = memberLevelService.getDefaultLevel();
        domain.setLevelId(defaultLevel.getId());
        domain.setUsername(registerDTO.getUsername());
        // 密码要进行加密存储
        String encoded = new BCryptPasswordEncoder().encode(registerDTO.getPassword());
        domain.setPassword(encoded);
        domain.setEmail(registerDTO.getEmail());
        domain.setNickname(registerDTO.getUsername());
        baseMapper.insert(domain);
    }

    @Override
    public MemberEntity login(UserLoginDTO userLoginDTO) {
        MemberEntity selected = baseMapper.selectOne(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getUsername, userLoginDTO.getUsername())
                .or().eq(MemberEntity::getEmail, userLoginDTO.getUsername()));
        if (selected == null) {
            throw new LoginException();
        } else {
            // 密码要进行加密存储
            if (!new BCryptPasswordEncoder().matches(userLoginDTO.getPassword(), selected.getPassword())) {
                throw new LoginException();
            }
        }
        return selected;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MemberEntity login(OauthLoginDTO oauthLoginDTO) {
        MemberOauthEntity selected = oauthDao.selectOne(new LambdaQueryWrapper<MemberOauthEntity>().eq(MemberOauthEntity::getUid, oauthLoginDTO.getUid())
                .eq(MemberOauthEntity::getType, oauthLoginDTO.getType()));
        long expired = System.currentTimeMillis() + oauthLoginDTO.getExpiredDate();
        LocalDateTime expiredTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(expired), ZoneId.systemDefault());
        MemberEntity result;
        if (selected == null) {
            // 无此用户， 需要注册, 用户其他信息不管
            result = new MemberEntity();
            result.setNickname(oauthLoginDTO.getNickname());
            save(result);

            MemberOauthEntity relation = new MemberOauthEntity();
            relation.setMemberId(result.getId());
            relation.setToken(oauthLoginDTO.getAccessToken());
            relation.setExpiredDate(expiredTime);
            relation.setType(oauthLoginDTO.getType());
            relation.setUid(oauthLoginDTO.getUid());


            oauthDao.insert(relation);
        } else {
            result = getById(selected.getMemberId());
            selected.setToken(oauthLoginDTO.getAccessToken());
            selected.setExpiredDate(expiredTime);
            oauthDao.updateById(selected);
        }
        return result;
    }

    private void checkEmailUnique(String email) {
        boolean exist = baseMapper.selectCount(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getEmail, email)) > 0;
        if (exist) {
            throw new EmailExistException();
        }
    }

    private void checkUsernameUnique(String username) {
        boolean exist = baseMapper.selectCount(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getUsername, username)) > 0;
        if (exist) {
            throw new UsernameExistException();
        }
    }

}
