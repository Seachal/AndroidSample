package com.pinger.sample.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fungo.baselib.base.basic.BaseFragment
import com.pinger.sample.R


/**
 * @author Pinger
 * @since 2017/3/26 11:08
 */

class SplashFragment : BaseFragment() {
    override fun getLayoutResId(): Int {
        return R.layout.fragment_splash

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_splash, null)
    }
}
