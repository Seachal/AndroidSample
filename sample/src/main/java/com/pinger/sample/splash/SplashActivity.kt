package com.pinger.sample.splash
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.ViewStub
import android.widget.TextView
import android.widget.Toast

import com.pinger.sample.R

import java.lang.ref.WeakReference

class SplashActivity : AppCompatActivity() {
    private val mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val viewStub = findViewById<ViewStub>(R.id.viewStub)

        println("==========start--填充SplashFragment--start==========")
        // 1.初始化SplashFragment，填充Splash
        val splashFragment = SplashFragment()
        fragmentManager.beginTransaction().replace(R.id.container, splashFragment).commitAllowingStateLoss()

        // 2.窗体加载完毕的时候,填充主页布局
        window.decorView.post {
            // 填充布局
            viewStub.inflate()
            // 初始化布局
            initView()
            // 2秒后移除Splash
            mHandler.postDelayed(DelayRunnable(this@SplashActivity, splashFragment), 3000)
        }

        // 3. 加载主页数据
        initData()
    }

    private fun initView() {
        println( "==========start--初始化MainActivity布局--start==========")
        val text = findViewById<TextView>(R.id.text)
        text.text = "我是主页内容，我终于显示出来了"
    }


    private fun initData() {
        println("==========start--加载MainActivity数据--start==========")
        Toast.makeText(this, "数据加载完毕", Toast.LENGTH_SHORT).show()

    }

    /**
     * 延时关闭
     */
    private class DelayRunnable internal constructor(context: Context, splashFragment: SplashFragment) : Runnable {
        private val contextRef: WeakReference<Context> = WeakReference(context)
        private val fragmentRef: WeakReference<SplashFragment> = WeakReference(splashFragment)

        override fun run() {
            val context = contextRef.get() as Activity
            val splashFragment = fragmentRef.get() ?: return
            val transaction = context.fragmentManager.beginTransaction()
            transaction.remove(splashFragment)
            transaction.commit()

            println("==========start--移除SplashFragment--start==========")
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
    }
}
