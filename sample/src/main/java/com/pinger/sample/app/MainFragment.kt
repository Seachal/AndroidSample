package com.pinger.sample.app

import android.content.Intent
import android.view.View
import com.fungo.baselib.base.page.BasePageFragment
import com.pinger.sample.R
import com.pinger.sample.alphabar.AlphaBarFragment
import com.pinger.sample.flowlayout.FlowLayoutFragment
import com.pinger.sample.pointnine.PointNineFragment
import com.pinger.sample.screenrotate.ScreenRotateFragment
import com.pinger.sample.screenshot.ScreenShotFragment
import com.pinger.sample.slidelayout.SlideLayoutFragment
import com.pinger.sample.splash.SplashActivity
import com.pinger.sample.toast.ToastFragment
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : BasePageFragment() {

    override fun getContentResId(): Int {
        return R.layout.fragment_main
    }

    override fun initPageView() {
        setOnClick(onSplash)
        setOnClick(onScreenShot)
        setOnClick(onShowToast)
        setOnClick(onPointNine)
        setOnClick(onFlowLayout)
        setOnClick(onScreenRotate)
        setOnClick(onSlideLayout)
        setOnClick(onAlphaBar)
    }


    override fun onClick(view: View) {
        when (view) {
            onScreenShot -> start(ScreenShotFragment())
            onShowToast -> start(ToastFragment())
            onPointNine -> start(PointNineFragment())
            onFlowLayout -> start(FlowLayoutFragment())
            onScreenRotate -> start(ScreenRotateFragment())
            onSlideLayout -> start(SlideLayoutFragment())
            onAlphaBar -> start(AlphaBarFragment())
            onSplash -> startActivity(Intent(context, SplashActivity::class.java))
        }
    }


    override fun isBackEnable(): Boolean = false

    override fun getPageTitle(): String? {
        return getString(R.string.app_name)
    }

    override fun isSwipeBackEnable(): Boolean = false
}