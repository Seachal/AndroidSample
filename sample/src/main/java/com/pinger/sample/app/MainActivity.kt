package com.pinger.sample.app

import android.view.View
import com.fungo.baselib.base.basic.BaseActivity
import com.pinger.sample.R
import com.pinger.sample.screenshot.ScreenShotActivity
import com.pinger.sample.splash.SplashActivity

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

}