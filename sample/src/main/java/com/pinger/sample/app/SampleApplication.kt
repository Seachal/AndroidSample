package com.pinger.sample.app

import com.fungo.baselib.app.BaseApplication
import com.fungo.imagego.ImageManager
import com.fungo.imagego.glide.GlideImageGoFactory
import com.pinger.sample.splash.InitializeService


/**
 * @author Pinger
 * @since 2017/3/26 11:06
 */

class SampleApplication : BaseApplication() {


    override fun initSDK() {
        super.initSDK()
        ImageManager.instance.setImageGoFactory(GlideImageGoFactory())
        //println("==========start--初始化第三方SDK--start==========")
        // 启动服务去做耗时操作
        //InitializeService.start(this)
        // 加载MainActivity初始化后就要用到的SDK
        // TODO
    }
}
