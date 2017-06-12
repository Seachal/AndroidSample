package com.pinger.common.utils;

import android.content.Context;

/**
 * @author Pinger
 * @since 2017/4/7 0007 下午 2:15
 * 与Ui相关的操作都放在这里，并且封装全局唯一的Context
 */
public class UiUtil {

    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    /**
     * 返回一个全局唯一的Context
     */
    public synchronized static Context getContext() {
        return mContext;
    }
}
