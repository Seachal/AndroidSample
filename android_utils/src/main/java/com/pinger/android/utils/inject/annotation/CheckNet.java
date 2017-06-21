package com.pinger.android.utils.inject.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Pinger
 * @since 2017/6/22 0:57
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckNet {
    // 网络错误时间的提示
    String value() default "网络异常，请检查您的网络";
}
