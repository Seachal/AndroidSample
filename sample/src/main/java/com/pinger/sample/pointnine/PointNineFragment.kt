package com.pinger.sample.pointnine

import com.fungo.baselib.base.page.BasePageFragment
import com.pinger.sample.R

/**
 * @author Pinger
 * @since 18-7-16 下午3:52
 *
 */

class PointNineFragment : BasePageFragment() {

    override fun getContentResId(): Int {
        return R.layout.fragment_point_nine
    }

    override fun getPageTitle(): String? {
        return getString(R.string.sample_point_nine)
    }

}