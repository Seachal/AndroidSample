package com.pinger.sample.app

import android.app.Application
import android.util.Log
import com.pinger.sample.splash.InitializeService


/**
 * @author Pinger
 * @since 2017/3/26 11:06
 */

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initSDK()
    }

    /**
     * 初始化第三方SDK，耗时操作
     */
    private fun initSDK() {
        println("==========start--初始化第三方SDK--start==========")
        // 启动服务去做耗时操作
        InitializeService.start(this)
        // 加载MainActivity初始化后就要用到的SDK
        // TODO
    }
}
