package com.lv.demo.annotation;

import java.lang.annotation.*;

/**
 * @author lv
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VerifySign {

    boolean required() default true;

}
