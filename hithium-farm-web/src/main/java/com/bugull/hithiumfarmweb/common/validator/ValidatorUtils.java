package com.bugull.hithiumfarmweb.common.validator;

import com.bugull.hithiumfarmweb.common.exception.ParamsValidateException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.Set;

public class ValidatorUtils {

    private static Validator validator;

    static {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }
    /**
     * 校验对象
     * @param object        待校验对象
     * @param groups        待校验的组
     * @throws ValidationException  校验不通过，则报RRException异常
     */
    public static void validateEntity(Object object, Class<?>... groups)
            throws ValidationException {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
        if (!constraintViolations.isEmpty()) {
            StringBuilder msg = new StringBuilder();
            for(ConstraintViolation<Object> constraint:  constraintViolations){
                msg.append(constraint.getMessage()).append("<br>");
            }
            /**
             * 参数校验异常
             */
            throw new ParamsValidateException(msg.toString());
        }
    }
}
