package com.pinger.sample.screenshot

import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import com.fungo.baselib.base.basic.BaseActivity
import com.pinger.sample.R
import kotlinx.android.synthetic.main.activity_screen_shot_web_view.*


class ScreenShotWebViewActivity : BaseActivity() {


    private var mWebUrl = "https://cloudfra.com/ssr-service.html"

    override fun onCreate(savedInstanceState: Bundle?) {
        // WebView渲染开始之前加上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw()
        }
        super.onCreate(savedInstanceState)
    }

    override val layoutResID: Int
        get() = R.layout.activity_screen_shot_web_view

    override fun initView() {
        setToolBar(getString(R.string.sample_screen_shot_web_view), true)

        val settings = webView.settings
        settings.javaScriptEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                tvStatus.text = "加载中 ${newProgress}%"
                if (newProgress == 100) {
                    tvStatus.text = "加载完成,当前为WebView页面"
                }
            }
        }
        onLoad(TextView(this))
    }


    fun onCapture(view: View) {
        showProgress()
        webView.visibility = View.GONE
        scrollView.visibility = View.VISIBLE
        val bitmap = ScreenShotUtils.captureWebView(webView)
        ivShow.setImageBitmap(bitmap)
        dismissProgress()

        tvStatus.text = "截图成功,当前为截图页面"
    }


    fun onLoad(view: View) {
        webView.visibility = View.VISIBLE
        scrollView.visibility = View.GONE
        webView.loadUrl(mWebUrl)
    }


}
