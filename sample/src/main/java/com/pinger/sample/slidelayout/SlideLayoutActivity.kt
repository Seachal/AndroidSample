package com.pinger.sample.slidelayout

import android.os.Bundle
import android.view.WindowManager
import com.fungo.baselib.base.basic.BaseActivity
import com.pinger.sample.R

/**
 * @author Pinger
 * @since 18-7-17 上午11:22
 *
 */

class SlideLayoutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
    }

    override val layoutResID: Int
        get() = R.layout.activity_slide_layout


    override fun initView() {
        setToolBar(getString(R.string.sample_slide_layout), true)
    }


}