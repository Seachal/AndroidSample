package com.pinger.sample.app

import com.fungo.baselib.base.page.BasePageActivity
import com.fungo.baselib.base.page.BasePageFragment


/**
 * @author Pinger
 * @since 2018/7/1 下午4:56
 *
 */
class MainActivity : BasePageActivity() {

    override fun getPageFragment(): BasePageFragment {
        return MainFragment()
    }


    override fun isSwipeBackEnable(): Boolean = false

}