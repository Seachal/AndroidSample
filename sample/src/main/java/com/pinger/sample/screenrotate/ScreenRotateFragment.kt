package com.pinger.sample.screenrotate

import android.content.res.Configuration
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import com.fungo.baselib.base.page.BasePageFragment
import com.pinger.sample.R
import kotlinx.android.synthetic.main.fragment_screen_rotate.*


/**
 * @author Pinger
 * @since 18-7-16 下午5:48
 */

class ScreenRotateFragment : BasePageFragment() {

    override fun getContentResId(): Int {
        return R.layout.fragment_screen_rotate
    }


    override fun getPageTitle(): String? {
        return getString(R.string.sample_screen_rotate)
    }

    override fun onResume() {
        super.onResume()
        ScreenRotateUtils.getInstance(context!!).start(getPageActivity()!!)
    }


    override fun initPageView() {
        setOnClick(scaleFull)
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

        if (ScreenRotateUtils.getInstance(context!!).isLandscape()) {
            getPageActivity()!!.supportActionBar?.hide()
            getPageActivity()!!.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            Toast.makeText(context!!, "当前为横屏", Toast.LENGTH_SHORT).show()

            val params = imageView.layoutParams
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            imageView.layoutParams = params
        } else {
            getPageActivity()!!.supportActionBar?.show()
            getPageActivity()!!.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            Toast.makeText(context!!, "当前为竖屏", Toast.LENGTH_SHORT).show()

            val params = imageView.layoutParams
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = resources.displayMetrics.widthPixels * 9 / 16
            imageView.layoutParams = params
        }
    }

    override fun onClick(view: View) {
        if (view == scaleFull) {
            ScreenRotateUtils.getInstance(context!!).toggleRotate()
        }
    }

    override fun onPause() {
        super.onPause()
        ScreenRotateUtils.getInstance(context!!).stop()
    }

    override fun onBackPressedSupport(): Boolean {
        return if (ScreenRotateUtils.getInstance(context!!).isLandscape()) {
            ScreenRotateUtils.getInstance(context!!).setOrientation(true)
            false
        } else {
            super.onBackPressedSupport()
        }
    }
}