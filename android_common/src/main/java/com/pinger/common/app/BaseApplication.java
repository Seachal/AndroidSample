package com.pinger.common.app;

import android.app.Application;

import com.pinger.common.utils.UiUtil;

/**
 * @author Pinger
 * @since 2017/5/5 0005 下午 5:37
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        UiUtil.init(getApplicationContext());
    }
}
