package com.pinger.android.utils.inject.annotation;

import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Pinger
 * @since 2017/6/18 22:48
 */

@Target(METHOD)
@Retention(RUNTIME)
@ClickEvent(
        setter = "setOnClickListener",
        type = View.OnClickListener.class,
        callBackMethod = "onClick"
)
public @interface OnClick {
    int[] value();
}
