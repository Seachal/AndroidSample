package com.pinger.sample.splash

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.fungo.baselib.base.basic.BaseActivity
import com.pinger.sample.R
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.layout_main_content.*
import java.lang.ref.WeakReference

class SplashActivity(override val layoutResID: Int = R.layout.activity_splash) : BaseActivity() {

    private val mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        println("==========start--填充SplashFragment--start==========")
        // 1.初始化SplashFragment，填充Splash
        val splashFragment = SplashFragment()
        loadRootFragment(R.id.splashContainer, splashFragment)

        // 2.窗体加载完毕的时候,填充主页布局
        window.decorView.post {

            // 开启2秒后移除Splash倒计时
            mHandler.postDelayed(DelayRunnable(this), 2000)

            // 填充布局
            viewStub.inflate()
            // 初始化布局
            initMainView()

            // 3 加载数据
            initMainData()
        }
    }

    private fun initMainView() {
        println("==========start--初始化MainActivity布局--start==========")
        tvContent.text = "我是主页内容，我终于显示出来了"
    }


    private fun initMainData() {
        println("==========start--加载MainActivity数据--start==========")
        Toast.makeText(this, "数据加载完毕", Toast.LENGTH_SHORT).show()

    }

    /**
     * 延时关闭
     */
    private class DelayRunnable internal constructor(context: Context) : Runnable {
        private val contextRef: WeakReference<Context> = WeakReference(context)

        override fun run() {
            val context = contextRef.get() as SplashActivity
            context.pop()
            println("==========start--移除SplashFragment--start==========")
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
    }
}
