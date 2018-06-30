package com.pinger.sample.splash

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.widget.Toast


/**
 * @author Pinger
 * @since 2017/3/26 11:27
 */

class InitializeService : IntentService(InitializeService::class.java.simpleName) {

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (ACTION_APP_LAUNCHER == action) {
                performInit()
            }
        }
    }


    /**
     * 启动初始化耗时操作
     */
    private fun performInit() {

        // 模拟延时加载
        SystemClock.sleep(10000)
        println("==========初始化第三方SDK结束==========")
    }

    companion object {

       const val ACTION_APP_LAUNCHER = "action.app.launcher"

        /**
         * 启动调用
         *
         * @param context
         */
        fun start(context: Context) {
            val intent = Intent(context, InitializeService::class.java)
            intent.action = ACTION_APP_LAUNCHER
            context.startService(intent)
        }
    }


}
