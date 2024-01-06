package com.linqibin.mall.product.exception;

import com.linqibin.common.exception.BizCodeEnum;
import com.linqibin.common.utils.R;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理
 * @author linqibin
 * @date   2023/12/28 23:13
 * @email  1214219989@qq.com
 */
@RestControllerAdvice(basePackages = "com.linqibin.mall.product.controller")
public class MallExceptionControllerAdvice {

    /**
     * 后端数据校验
     * @author hugh&you
     * @since 2021/1/21 23:07
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException exception) {
        BindingResult result = exception.getBindingResult();
        Map<String, String> map = new HashMap<>();
        result.getFieldErrors().forEach((item) -> {
            String field = item.getField();
            String msg = item.getDefaultMessage();
            map.put(field, msg);
        });
        return R.error(BizCodeEnum.VAILD_EXCEPTION.getCode(), BizCodeEnum.VAILD_EXCEPTION.getMsg())
                .put("data", map);
    }
}
