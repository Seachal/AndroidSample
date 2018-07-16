package com.pinger.sample.screenrotate

import android.content.res.Configuration
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.fungo.baselib.base.basic.BaseActivity
import com.pinger.sample.R


/**
 * @author Pinger
 * @since 18-7-16 下午5:48
 */

class ScreenRotateActivity : BaseActivity() {

    override val layoutResID: Int
        get() = R.layout.activity_screen_rotate


    override fun initView() {
        setToolBar(getString(R.string.sample_screen_rotate), true)
    }


    override fun onResume() {
        super.onResume()
        ScreenRotateUtils.getInstance(this).start(this)
    }

    /**
     * 横竖屏切换或者输入法等事件触发时调用
     * 需要在清单文件中配置权限
     * 需要在当前Activity配置configChanges属性
     *
     * @param newConfig
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (ScreenRotateUtils.getInstance(this).isLandscape()) {
            Toast.makeText(this, "当前为横屏", Toast.LENGTH_SHORT).show()
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        } else {
            Toast.makeText(this, "当前为竖屏", Toast.LENGTH_SHORT).show()
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    fun scaleFull(view: View) {
        ScreenRotateUtils.getInstance(this).toggleRotate()
    }

    override fun onPause() {
        super.onPause()
        ScreenRotateUtils.getInstance(this).stop()
    }

}