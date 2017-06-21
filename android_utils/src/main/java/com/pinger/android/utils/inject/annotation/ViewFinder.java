package com.pinger.android.utils.inject.annotation;

import android.app.Activity;
import android.view.View;

/**
 * @author Pinger
 * @since 2017/6/22 1:01
 * View的辅助类，负责根据id查找对应的控件对象
 */

public class ViewFinder {

    private Activity mActivity;
    private View mView;

    public ViewFinder(Activity activity) {
        this.mActivity = activity;
    }

    public ViewFinder(View view) {
        this.mView = view;
    }

    /**
     * 查找View
     */
    public View findViewById(int viewId) {
        return mActivity != null ? mActivity.findViewById(viewId) : mView.findViewById(viewId);
    }
}
