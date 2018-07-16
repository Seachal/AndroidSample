package com.pinger.sample.toast

import android.view.View
import com.fungo.baselib.base.basic.BaseActivity
import com.pinger.sample.R

/**
 * @author Pinger
 * @since 18-7-16 下午2:25
 *
 */

class ToastActivity : BaseActivity() {

    private var toastUtils: ToastUtils? = null

    override val layoutResID: Int
        get() = R.layout.activity_toast

    override fun initView() {
        setToolBar(getString(R.string.sample_toast), true)
        toastUtils = ToastUtils(this)
    }

    fun popToast(view: View) {
        toastUtils?.popToast("发射成功")
    }

    fun hideToast(view: View) {
        toastUtils?.hideToast()
    }
}