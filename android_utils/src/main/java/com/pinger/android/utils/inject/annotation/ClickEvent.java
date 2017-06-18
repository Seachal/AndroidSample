package com.pinger.android.utils.inject.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Pinger
 * @since 2017/6/18 22:48
 */
@Target(ANNOTATION_TYPE)
@Retention(RUNTIME)
public @interface ClickEvent {

    /**
     * 事件设置方法的字符串
     */
    String setter();

    /**
     * 事件设置方法参数的对象
     */
    Class<?> type();

    /** 事件设置成功的回调方法字符串 */
    String callBackMethod();

}
