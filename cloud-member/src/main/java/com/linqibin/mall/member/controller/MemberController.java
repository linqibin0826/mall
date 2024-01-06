package com.linqibin.mall.member.controller;

import java.util.Arrays;
import java.util.Map;

// import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.linqibin.common.exception.BizCodeEnum;
import com.linqibin.mall.member.domain.dto.MemberRegisterDTO;
import com.linqibin.mall.member.domain.dto.OauthLoginDTO;
import com.linqibin.mall.member.domain.dto.UserLoginDTO;
import com.linqibin.mall.member.domain.entity.MemberEntity;
import com.linqibin.mall.member.exception.EmailExistException;
import com.linqibin.mall.member.exception.LoginException;
import com.linqibin.mall.member.exception.UsernameExistException;
import com.linqibin.mall.member.feign.CouponFeignClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import com.linqibin.mall.member.service.MemberService;
import com.linqibin.common.utils.PageUtils;
import com.linqibin.common.utils.R;



/**
 * 会员
 *
 * @author hugh
 * @email linqibin0826@gmail.com
 * @date 2021-01-10 20:03:41
 */
@RestController
@RequestMapping("member/member")
@AllArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    private final CouponFeignClient couponFeignClient;

    @PostMapping("/register")
    public R register(@RequestBody MemberRegisterDTO registerDTO) {

        try {
            memberService.register(registerDTO);
        } catch (UsernameExistException e) {
            return R.error(BizCodeEnum.USER_EXIST_EXCEPTION);
        } catch (EmailExistException e) {
            return R.error(BizCodeEnum.EMAIL_EXIST_EXCEPTION);
        }
        return R.ok();
    }

    @PostMapping("/login")
    public R login(@RequestBody UserLoginDTO userLoginDTO) {
        try {
            MemberEntity member = memberService.login(userLoginDTO);
            return R.ok().setData(member);
        } catch (LoginException e) {
            return R.error(BizCodeEnum.LOGINACTT_PASSWORD_ERROR);
        }
    }

    @PostMapping("/oauth/login")
    public R login(@RequestBody OauthLoginDTO oauthLoginDTO) {
        try {
            log.info("oauth login begin...");
            MemberEntity member = memberService.login(oauthLoginDTO);
            return R.ok().setData(member);
        } catch (Exception e) {
            log.error("oauth login exception:", e);
            return R.error(BizCodeEnum.SOCIALUSER_LOGIN_ERROR);
        }
    }

    @RequestMapping("/coupons")
    public R testFeign() {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("游美");
        R memberCoupons = couponFeignClient.memberCoupons();
        return R.ok().put("member", memberEntity).put("coupons", memberCoupons.get("coupon"));
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
