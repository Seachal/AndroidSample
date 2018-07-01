package com.pinger.sample.screenshot

import android.graphics.Bitmap
import android.view.View


/**
 * @author Pinger
 * @since 2018/7/1 下午5:27
 * 实现截屏和截图的工具类
 */
object ScreenShotUtils {


    /**
     * 对View进行量测，布局后截图
     *
     * @param view 截取的View
     * @return 生成的Bitmap
     */
    fun screenViewToBitmap(view: View): Bitmap {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        return view.drawingCache
    }


}