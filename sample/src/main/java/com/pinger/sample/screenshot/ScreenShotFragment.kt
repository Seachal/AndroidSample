package com.pinger.sample.screenshot

import android.content.Intent
import android.graphics.Bitmap
import android.support.v4.content.FileProvider.getUriForFile
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ScrollView
import com.fungo.baselib.base.page.BasePageFragment
import com.fungo.baselib.utils.ToastUtils
import com.pinger.sample.R
import com.pinger.sample.data.DataProvider
import kotlinx.android.synthetic.main.fragment_screen_shot.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 * @author Pinger
 * @since 2018/7/1 上午1:43
 *　截图Demo
 */
class ScreenShotFragment : BasePageFragment() {

    private var mBitmap: Bitmap? = null

    override fun getContentResId(): Int {
        return R.layout.fragment_screen_shot
    }


    override fun getPageTitle(): String? {
        return getString(R.string.sample_screen_shot)
    }


    override fun isShareEnable(): Boolean {
        return true
    }

    override fun doShareAction() {
        shareImage()
    }

    override fun initEvent() {
        setOnClick(onScreenShot)
        setOnClick(onScreenView)
        setOnClick(onScreenShotNoStatus)
        setOnClick(onScreenScrollView)
        setOnClick(onScreenListView)
        setOnClick(onScreenRecyclerView)
        setOnClick(onScreenWebView)
        setOnClick(onScreenDemo)
    }


    override fun onClick(view: View) {
        when (view) {
            onScreenShot -> onScreenShot()
            onScreenView -> onScreenView()
            onScreenShotNoStatus -> onScreenShotNoStatus()
            onScreenScrollView -> onScreenScrollView()
            onScreenListView -> onScreenListView()
            onScreenRecyclerView -> onScreenRecyclerView()
            onScreenWebView -> onScreenWebView()
            onScreenDemo -> onScreenDemo()
        }
    }


    /**
     * 截取当前窗体
     */
    fun onScreenShot() {
        mBitmap = ScreenShotUtils.captureWindow(getPageActivity()!!)
        showView?.setImageBitmap(mBitmap)
    }


    /**
     * 截取当前窗体，不带状态栏,状态栏是空白的
     */
    fun onScreenShotNoStatus() {
        mBitmap = ScreenShotUtils.captureWindow(getPageActivity()!!, false)
        showView?.setImageBitmap(mBitmap)
    }


    /**
     * 常用的View截图，包括TextView，ImageView，FrameLayout，LinearLayout，RelativeLayout等
     */
    fun onScreenView() {
        showPageLoadingDialog()
        imageContainer.removeAllViews()
        val rootView = layoutInflater.inflate(R.layout.screen_shot_view, imageContainer)
        val layout = rootView.findViewById<LinearLayout>(R.id.layout)

        layout.post {
            mBitmap = ScreenShotUtils.captureView(layout)
            showView?.setImageBitmap(mBitmap)
            hidePageLoadingDialog()
        }
    }

    /**
     * 截取ScrollView，包括未展示的内容
     */
    fun onScreenScrollView() {
        showPageLoadingDialog()
        imageContainer.removeAllViews()
        val rootView = layoutInflater.inflate(R.layout.screen_shot_scroll_view, imageContainer)
        val scrollView = rootView.findViewById<ScrollView>(R.id.scroll_view)
        scrollView.post {
            mBitmap = ScreenShotUtils.captureScrollView(scrollView)
            showView?.setImageBitmap(mBitmap)
            hidePageLoadingDialog()
        }
    }


    /**
     * 截取ListView，包括未展示的内容
     */
    fun onScreenListView() {
        showPageLoadingDialog()
        imageContainer.removeAllViews()
        val rootView = layoutInflater.inflate(R.layout.screen_shot_list_view, imageContainer)
        val listView = rootView.findViewById<ListView>(R.id.listView)
        val adapter = ArrayAdapter<String>(context!!,
                android.R.layout.simple_list_item_1, DataProvider.getData())
        listView.adapter = adapter

        listView.post {
            mBitmap = ScreenShotUtils.captureListView(listView)
            showView?.setImageBitmap(mBitmap)
            hidePageLoadingDialog()
        }
    }


    /**
     * 截取RecyclerView，包括未展示的内容
     */
    fun onScreenRecyclerView() {
        showPageLoadingDialog()
        imageContainer.removeAllViews()
        val rootView = layoutInflater.inflate(R.layout.screen_recycler_view, imageContainer)
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = RecyclerAdapter(context!!, DataProvider.getData())

        recyclerView.post {
            mBitmap = ScreenShotUtils.captureRecyclerView(recyclerView)
            showView?.setImageBitmap(mBitmap)
            hidePageLoadingDialog()
        }
    }


    /**
     * 跳转到WebView截图页面
     */
    fun onScreenWebView() {
        start(ScreenShotWebViewFragment())
    }


    /**
     * 实战案例，点击分享，生成一张自定义图片进行分享,如果有图片加载需要监听图片是否加载完成
     */
    fun onScreenDemo() {
        showPageLoadingDialog()
        imageContainer.removeAllViews()
        val rootView = layoutInflater.inflate(R.layout.screen_shot_demo, imageContainer)
        val scrollView = rootView.findViewById<ScrollView>(R.id.scrollView)
        scrollView.post {
            mBitmap = ScreenShotUtils.captureScrollView(scrollView)
            showView?.setImageBitmap(mBitmap)
            hidePageLoadingDialog()
        }

    }


    /**
     * 调用系统API分享图片
     */
    private fun shareImage() {
        if (mBitmap == null) {
            ToastUtils.showToast("请先生成截图")
            return
        }
        val imageIntent = Intent(Intent.ACTION_SEND)
        imageIntent.type = "image/jpeg"

        val file = saveBitmap(mBitmap)

        if (file != null) {
            val contentUri = getUriForFile(context!!, "${context!!.packageName}.fileprovider", file)

            imageIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
            startActivity(Intent.createChooser(imageIntent, "分享图片"))
        } else {
            ToastUtils.showToast("获取本地图片失败，请查看是否有存储权限")
        }
    }


    /**
     * 保存图片到本地文件
     * @param bitmap 图片对象
     * @return 保存的文件
     */
    private fun saveBitmap(bitmap: Bitmap?): File? {
        val appDir = File(getPageActivity()?.cacheDir, "images")
        if (!appDir.exists()) {
            appDir.mkdir()
        }
        val fileName = System.currentTimeMillis().toString() + ".jpg"
        val file = File(appDir, fileName)
        try {
            val fos = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }


}