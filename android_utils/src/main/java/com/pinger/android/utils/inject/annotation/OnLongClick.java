package com.pinger.android.utils.inject.annotation;

import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Pinger
 * @since 2017/6/19 23:49
 * 长按事件注解
 */

@Target(METHOD)
@Retention(RUNTIME)
@ClickEvent(
        setter = "setOnLongClickListener",
        type = View.OnLongClickListener.class,
        callBackMethod = "onLongClick"
)
public @interface OnLongClick {
    int[] value();
}
