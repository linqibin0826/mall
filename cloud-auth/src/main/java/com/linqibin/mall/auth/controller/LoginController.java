package com.linqibin.mall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.linqibin.common.constant.AuthConstant;
import com.linqibin.common.constant.RedisConstants;
import com.linqibin.common.exception.BizCodeEnum;
import com.linqibin.common.to.MemberRespVo;
import com.linqibin.common.utils.R;
import com.linqibin.mall.auth.domain.vo.RegisterVo;
import com.linqibin.mall.auth.domain.vo.UserLoginVo;
import com.linqibin.mall.auth.feign.MemberFeignClient;
import com.linqibin.mall.auth.feign.ThirdPartFeignClient;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.linqibin.common.constant.AuthConstant.LOGIN_USER;
import static com.linqibin.common.constant.PunctuationConstants.UNDERLINE;

/**
 * 1. Session不能跨不同域名共享
 * 2. 即使同一域名，可能存在多个服务实例
 *
 * 1️⃣.Session共享问题解决
 * - Session复制 数据冗余量大， 占用带宽高 不可取。
 * - hash一致————负载均衡，使固定用户总是被分配到同一台服务器上。  缺点： 如果水平拓展了。 rehash后session会重新分布。会有一部分用户路由不到正确的session
 * - 统一存储， redis  缺点： 增加了一次网络调用， 增加了系统的复杂性。
 * 2️⃣.子域之间session共享问题   auth.gulimall.com   order.gulimall.com
 * - 在发JSESSIONID时，即使是子域发的，也能让父使用  response
 * 登陆注册Controller
 * @author linqibin
 * @date   2023/12/28 22:56
 * @email  1214219989@qq.com
 */
@Controller
@AllArgsConstructor
public class LoginController {

    private final ThirdPartFeignClient thirdPartFeignClient;

    private final MemberFeignClient memberFeignClient;

    private final StringRedisTemplate redisTemplate;



    /**
     * 发送邮件验证码
     *
     * @param mail 邮件地址
     * @return 返回结果
     */
    @GetMapping("/mail/sendCode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String mail) {
        // 从Redis中获取上次发送的验证码时间
        String cache = redisTemplate.opsForValue().get(RedisConstants.REGISTER_CODE_CACHE_PREFIX + mail);
        if (StringUtils.isNotBlank(cache)) {
            String[] cacheArray = cache.split(UNDERLINE);
            long preCodeTimestamp = Long.parseLong(cacheArray[1]);
            // 判断两次发送验证码的时间是否小于60秒
            if (System.currentTimeMillis() - preCodeTimestamp <  60000) {
                return R.error(BizCodeEnum.CODE_EXCEPTION);
            }
        }


        // 生成随机验证码
        String code = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase(Locale.ENGLISH);
        // 调用第三方发送验证码方法
        thirdPartFeignClient.sendCode(mail, code);
        // 将验证码存入Redis
        redisTemplate.opsForValue().set(RedisConstants.REGISTER_CODE_CACHE_PREFIX + mail, code + "_" + System.currentTimeMillis(),
                5, TimeUnit.MINUTES);
        return R.ok();
    }


    /**
     * 注册
     * 重定向携带数据，利用session原理， 将数据放在session中，只要跳到下一个页面取出数据，session里面的数据就会删除
     * 还需要解决分布式下session的问题
     * @param registerVo 注册信息
     * @param result 注册校验结果
     * @param redirectAttributes 跳转属性
     * @return 登录页面路径
     */
    @PostMapping("/register")
    public String register(@Valid RegisterVo registerVo, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            HashMap<String, Object> map = new HashMap<>(4);
            result.getFieldErrors().forEach(error -> {
                map.put(error.getField(), error.getDefaultMessage());
            });
            // 添加一闪而过的错误信息（利用session原理，在errors保存在session中了）
            redirectAttributes.addFlashAttribute("errors", map);
            // 为了防止前端页面刷新后，错误信息还存在，所以将错误信息存入session，此处应用重定向,而且必须写完整路径，否则重定向会跳到ip+端口
            return "redirect:http://auth.gulimall.com/reg.html";
        }
        // 校验验证码并删除
        String cacheCode = redisTemplate.opsForValue().get(RedisConstants.REGISTER_CODE_CACHE_PREFIX + registerVo.getEmail());
        if (StringUtils.isBlank(cacheCode) || !cacheCode.split(UNDERLINE)[0].equals(registerVo.getCode())) {

            redirectAttributes.addFlashAttribute("errors", new HashMap<String, String>(1) {{put("code", "验证码错误");}});
            return "redirect:http://auth.gulimall.com/reg.html";
        }

        redisTemplate.delete(RedisConstants.REGISTER_CODE_CACHE_PREFIX + registerVo.getEmail());

        // 真正调用远程用户服务 注册用户
        R status = memberFeignClient.register(registerVo);
        return handleRegistrationStatus(redirectAttributes, status);
    }

    @GetMapping("/login.html")
    public String login(HttpSession session) {
        if (session.getAttribute(LOGIN_USER) != null) {
            return "http://gulimall.com";
        } else {
            return "login";
        }
    }

    @PostMapping("/login")
    public String login(@RequestParam(value = "username") String username,
                        @RequestParam(value = "password")String password,
                        RedirectAttributes redirectAttributes, HttpSession session) {
        R login = memberFeignClient.login(new UserLoginVo(username, password));
        if (login.getCode() == 0) {
            MemberRespVo data = login.getData(new TypeReference<MemberRespVo>() {});
            session.setAttribute(LOGIN_USER, data);
            return "redirect:http://gulimall.com";
        } else {
            redirectAttributes.addFlashAttribute("errors", new HashMap<String, String>(1) {{put("msg", "用户名或密码错误");}});
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }

    private static String handleRegistrationStatus(RedirectAttributes redirectAttributes, R status) {
        if (status.getCode() == 0) {
            // 成功
            return "redirect:http://auth.gulimall.com/login.html";
        } else {
            Map<String, String> map = new HashMap<>(1);
            if (Objects.equals(status.getCode(), BizCodeEnum.USER_EXIST_EXCEPTION.getCode())) {
                map.put("username", "用户名已存在");
            } else if (Objects.equals(status.getCode(), BizCodeEnum.EMAIL_EXIST_EXCEPTION.getCode())) {
                map.put("email", "邮箱已存在");
            } else {
                map.put("msg", "注册失败");
            }
            redirectAttributes.addFlashAttribute("errors", map);
            return "redirect:http://auth.gulimall.com/reg.html";
        }
    }

}
