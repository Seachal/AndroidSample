package com.pinger.sample.screenshot

import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.fungo.baselib.base.page.BasePageFragment
import com.pinger.sample.R
import kotlinx.android.synthetic.main.fragment_screen_shot_web_view.*


class ScreenShotWebViewFragment : BasePageFragment() {

    override fun getContentResId(): Int {
        return R.layout.fragment_screen_shot_web_view
    }

    private var mWebUrl = "https://www.jianshu.com/p/1acf015f1f96"

    override fun onCreate(savedInstanceState: Bundle?) {
        // WebView渲染开始之前加上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw()
        }
        super.onCreate(savedInstanceState)
    }

    override fun initPageView() {
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
                tvStatus?.text = "加载中 ${newProgress}%"
                if (newProgress == 100) {
                    tvStatus?.text = "加载完成,当前为WebView页面"
                }
            }
        }
        onLoad()
    }

    override fun initEvent() {
        setOnClick(btnWebView)
        setOnClick(btnScreen)
    }

    override fun onClick(view: View) {
        when (view) {
            btnScreen -> onCapture()
            btnWebView -> onLoad()
        }
    }


    override fun getPageTitle(): String? {
        return getString(R.string.sample_screen_shot_web_view)
    }


    /**
     * 截图WebView图片
     */
    private fun onCapture() {
        showPageLoadingDialog()
        webView.visibility = View.GONE
        scrollView.visibility = View.VISIBLE
        val bitmap = ScreenShotUtils.captureWebView(webView)
        ivShow.setImageBitmap(bitmap)
        hidePageLoadingDialog()

        tvStatus.text = "截图成功,当前为截图页面"
    }


    /**
     * 加载WebView
     */
    private fun onLoad() {
        webView.visibility = View.VISIBLE
        scrollView.visibility = View.GONE
        webView.loadUrl(mWebUrl)
    }


}
