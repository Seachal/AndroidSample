package com.pinger.sample.app

import android.view.View
import com.fungo.baselib.base.basic.BaseActivity
import com.pinger.sample.R
import com.pinger.sample.alphabar.AlphaBarActivity
import com.pinger.sample.flowlayout.FlowLayoutActivity
import com.pinger.sample.pointnine.PointNineActivity
import com.pinger.sample.screenrotate.ScreenRotateActivity
import com.pinger.sample.screenshot.ScreenShotActivity
import com.pinger.sample.slidelayout.SlideLayoutActivity
import com.pinger.sample.splash.SplashActivity
import com.pinger.sample.toast.ToastActivity

/**
 * @author Pinger
 * @since 2018/7/1 下午4:56
 *
 */
class MainActivity : BaseActivity() {

    override val layoutResID: Int
        get() = R.layout.activity_main

    fun onSplash(view: View) {
        startActivity(SplashActivity::class.java)
    }

    fun onScreenShot(view: View) {
        startActivity(ScreenShotActivity::class.java)
    }


    fun onShowToast(view: View) {
        startActivity(ToastActivity::class.java)
    }

    fun onPointNine(view: View) {
        startActivity(PointNineActivity::class.java)
    }

    fun onFlowLayout(view: View) {
        startActivity(FlowLayoutActivity::class.java)
    }

    fun onScreenRotate(view: View) {
        startActivity(ScreenRotateActivity::class.java)
    }

    fun onSlideLayout(view: View) {
        startActivity(SlideLayoutActivity::class.java)
    }

    fun onAlphaBar(view: View) {
        startActivity(AlphaBarActivity::class.java)
    }

}