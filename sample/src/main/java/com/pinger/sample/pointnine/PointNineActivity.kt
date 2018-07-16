package com.pinger.sample.pointnine

import com.fungo.baselib.base.basic.BaseActivity
import com.pinger.sample.R

/**
 * @author Pinger
 * @since 18-7-16 下午3:52
 *
 */

class PointNineActivity : BaseActivity() {

    override val layoutResID: Int
        get() = R.layout.activity_point_nine

    override fun initView() {
        setToolBar(getString(R.string.sample_point_nine), true)
    }
}