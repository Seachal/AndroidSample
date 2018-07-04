package com.pinger.sample.screenshot

import android.graphics.Bitmap
import android.os.Build
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import com.fungo.baselib.base.basic.BaseActivity
import com.fungo.imagego.ImageManager
import com.pinger.sample.R
import com.pinger.sample.data.DataProvider
import kotlinx.android.synthetic.main.activity_screen_shot.*


/**
 * @author Pinger
 * @since 2018/7/1 上午1:43
 *
 */
class ScreenShotActivity : BaseActivity() {

    private var mBitmap:Bitmap?=null
    private var mWebUrl = "https://blog.csdn.net/billy_zuo/article/details/71077681"

    override val layoutResID: Int
        get() = R.layout.activity_screen_shot

    override fun initView() {
        setToolBar(getString(R.string.sample_screen_shot), true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw()
        }
    }


    private fun performShotView(view: View) {
        view.postDelayed({
            mBitmap = ScreenShotUtils.getScreenShotFromView(view)
            showView.setImageBitmap(mBitmap)
            dismissProgress()
        },500)
    }


    fun onScreenShot(view: View) {
        view.postDelayed({
            mBitmap = ScreenShotUtils.getScreenShotFromWindow(this)
            showView.setImageBitmap(mBitmap)
            dismissProgress()
        },500)
    }

    fun onScreenScrollView(view: View) {
        showProgress()
        container.removeAllViews()
        val rootView = layoutInflater.inflate(R.layout.screen_shot_scroll_view, container)
        val scrollView = rootView.findViewById<ScrollView>(R.id.scroll_view)
        scrollView.postDelayed({
            mBitmap = ScreenShotUtils.getScreenShotFromScrollView(scrollView)
            showView.setImageBitmap(mBitmap)
            dismissProgress()
        },500)
    }


    fun onScreenListView(view: View){
        showProgress()
        container.removeAllViews()
        val rootView = layoutInflater.inflate(R.layout.screen_shot_list_view, container)
        val listView = rootView.findViewById<ListView>(R.id.listView)
        val adapter = ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, DataProvider.getData())
        listView.adapter = adapter

        listView.postDelayed({
            mBitmap = ScreenShotUtils.getScreenShotFromListView(listView)
            showView.setImageBitmap(mBitmap)
            dismissProgress()
        },500)
    }


    fun onScreenWebView(view: View) {
        showProgress()
        container.removeAllViews()
        val rootView = layoutInflater.inflate(R.layout.screen_shot_web_view, container)
        val webView = rootView.findViewById<WebView>(R.id.webView)

        webView.webViewClient = object :WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                view?.loadUrl(mWebUrl)
                return true
            }
        }
        webView.webChromeClient = object :WebChromeClient(){
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress == 100) {
                    webView.postDelayed({
                        mBitmap = ScreenShotUtils.getScreenShotFromWebView(webView)
                        showView.setImageBitmap(mBitmap)
                        dismissProgress()
                    },5000)
                }
            }
        }
        webView.loadUrl(mWebUrl)
    }


    fun onScreenRecyclerView(view: View) {

    }



    /**
     * ImageView截图
     */
    fun onScreenImageView(view: View) {
        showProgress()
        container.removeAllViews()
        val url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1530447508136&di=a78c9b30f2fbc1acfd1d0228b72f793b&imgtype=0&src=http%3A%2F%2Fh.hiphotos.baidu.com%2Fzhidao%2Fpic%2Fitem%2Ffaedab64034f78f018a4ae837f310a55b3191c16.jpg"
        val rootView = layoutInflater.inflate(R.layout.screen_shot_image, container)
        val imageView = rootView.findViewById<ImageView>(R.id.imageView)
        ImageManager.instance.loadImage(url, imageView)
        performShotView(imageView)
    }


    /**
     * TextView截图
     */
    fun onScreenTextView(view: View) {
        showProgress()
        container.removeAllViews()
        val rootView = layoutInflater.inflate(R.layout.screen_shot_text, container)
        val textView = rootView.findViewById<TextView>(R.id.textView)
        performShotView(textView)
    }

    /**
     * FrameLayout截图
     */
    fun onScreenFrameLayout(view: View) {
        showProgress()
        container.removeAllViews()
        val rootView = layoutInflater.inflate(R.layout.screen_shot_frame_layout, container)
        val frameLayout = rootView.findViewById<FrameLayout>(R.id.frame_layout)
        performShotView(frameLayout)
    }


    /**
     * LinearLayout截图
     */
    fun onScreenLinearLayout(view: View) {
        showProgress()
        container.removeAllViews()
        val rootView = layoutInflater.inflate(R.layout.screen_shot_linear_layout, container)
        val linearLayout = rootView.findViewById<LinearLayout>(R.id.linear_layout)
        performShotView(linearLayout)
    }

    /**
     * RelativeLayout截图
     */
    fun onScreenRelativeLayout(view: View) {
        showProgress()
        container.removeAllViews()
        val rootView = layoutInflater.inflate(R.layout.screen_shot_relative_layout, container)
        val relativeLayout = rootView.findViewById<RelativeLayout>(R.id.relative_layout)
        performShotView(relativeLayout)
    }
}