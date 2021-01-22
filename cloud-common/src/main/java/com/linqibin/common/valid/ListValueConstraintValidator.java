package com.linqibin.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Integer> {

    private Set<Integer> set = new HashSet<>();

    /**
     * 初始化方法, 会将注解里面的信息获取过来
     *
     * @author hugh&you
     * @since 2021/1/22 17:17
     */
    @Override
    public void initialize(ListValue constraintAnnotation) {
        int[] vals = constraintAnnotation.vals();
        if (vals.length > 0) {
            for (int val : vals) {
                set.add(val);
            }
        }
    }

    /**
     * 判断是否校验成功
     *
     * @param integer 需要校验的值
     * @author hugh&you
     * @since 2021/1/22 17:26
     */
    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        return set.contains(integer);
    }
}
