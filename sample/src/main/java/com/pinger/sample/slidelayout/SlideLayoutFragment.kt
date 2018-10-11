package com.pinger.sample.slidelayout

import com.fungo.baselib.base.page.BasePageFragment
import com.pinger.sample.R

/**
 * @author Pinger
 * @since 18-7-17 上午11:22
 *
 */

class SlideLayoutFragment : BasePageFragment() {

    override fun getContentResId(): Int {
        return R.layout.fragment_slide_layout
    }

    override fun getPageTitle(): String? {
        return getString(R.string.sample_slide_layout)
    }


    override fun isShowToolBar(): Boolean  = false

}