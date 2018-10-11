package com.pinger.sample.toast

import android.view.View
import com.fungo.baselib.base.page.BasePageFragment
import com.pinger.sample.R
import kotlinx.android.synthetic.main.fragment_toast.*

/**
 * @author Pinger
 * @since 18-7-16 下午2:25
 *
 */

class ToastFragment : BasePageFragment() {

    private var toastUtils: ToastUtils? = null

    override fun getContentResId(): Int {
        return R.layout.fragment_toast
    }

    override fun getPageTitle(): String? {
        return getString(R.string.sample_toast)
    }

    override fun initPageView() {
        toastUtils = ToastUtils(context!!)
    }

    override fun initEvent() {
        setOnClick(popToast)
        setOnClick(hideToast)
    }

    override fun onClick(view: View) {
        when (view) {
            popToast -> toastUtils?.popToast("发射成功")
            hideToast -> toastUtils?.hideToast()
        }
    }

}