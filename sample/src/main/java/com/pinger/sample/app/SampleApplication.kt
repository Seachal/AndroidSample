package com.pinger.sample.app

import com.fungo.baselib.app.BaseApplication


/**
 * @author Pinger
 * @since 2017/3/26 11:06
 */

class SampleApplication : BaseApplication() {

    override fun getCurrentEnvModel(): Int {
        return 0
    }

    override fun initSDK() {
    }

    override fun isInnerUseModel(): Boolean {
        return false
    }


}
