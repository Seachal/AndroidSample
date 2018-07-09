package com.pinger.sample.screenshot

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ScrollView
import com.fungo.baselib.base.basic.BaseActivity
import com.fungo.baselib.utils.ToastUtils
import com.pinger.sample.R
import com.pinger.sample.data.DataProvider
import kotlinx.android.synthetic.main.activity_screen_shot.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 * @author Pinger
 * @since 2018/7/1 上午1:43
 *　截图Demo
 */
class ScreenShotActivity : BaseActivity() {

    private var mBitmap: Bitmap? = null

    override val layoutResID: Int
        get() = R.layout.activity_screen_shot

    override fun initView() {
        setToolBar(getString(R.string.sample_screen_shot), true)
    }


    override fun getMenuResID(): Int {
        return R.menu.menu_share
    }


    override fun onMenuItemSelected(itemId: Int) {
        if (itemId == R.id.action_share) {
            shareImage()
        }
    }

    /**
     * 截取当前窗体
     */
    fun onScreenShot(view: View) {
        mBitmap = ScreenShotUtils.captureWindow(this)
        showView.setImageBitmap(mBitmap)
    }


    /**
     * 截取当前窗体，不带状态栏,状态栏是空白的
     */
    fun onScreenShotNoStatus(view: View) {
        mBitmap = ScreenShotUtils.captureWindow(this, false)
        showView.setImageBitmap(mBitmap)
    }


    /**
     * 常用的View截图，包括TextView，ImageView，FrameLayout，LinearLayout，RelativeLayout等
     */
    fun onScreenView(view: View) {
        showProgress()
        container.removeAllViews()
        val rootView = layoutInflater.inflate(R.layout.screen_shot_view, container)
        val layout = rootView.findViewById<LinearLayout>(R.id.layout)
        layout.post({
            mBitmap = ScreenShotUtils.captureView(layout)
            showView.setImageBitmap(mBitmap)
            dismissProgress()
        })
    }

    /**
     * 截取ScrollView，包括未展示的内容
     */
    fun onScreenScrollView(view: View) {
        showProgress()
        container.removeAllViews()
        val rootView = layoutInflater.inflate(R.layout.screen_shot_scroll_view, container)
        val scrollView = rootView.findViewById<ScrollView>(R.id.scroll_view)
        scrollView.post({
            mBitmap = ScreenShotUtils.captureScrollView(scrollView)
            showView.setImageBitmap(mBitmap)
            dismissProgress()
        })
    }


    /**
     * 截取ListView，包括未展示的内容
     */
    fun onScreenListView(view: View) {
        showProgress()
        container.removeAllViews()
        val rootView = layoutInflater.inflate(R.layout.screen_shot_list_view, container)
        val listView = rootView.findViewById<ListView>(R.id.listView)
        val adapter = ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, DataProvider.getData())
        listView.adapter = adapter

        listView.post({
            mBitmap = ScreenShotUtils.captureListView(listView)
            showView.setImageBitmap(mBitmap)
            dismissProgress()
        })
    }


    /**
     * 截取RecyclerView，包括未展示的内容
     */
    fun onScreenRecyclerView(view: View) {
        showProgress()
        container.removeAllViews()
        val rootView = layoutInflater.inflate(R.layout.screen_recycler_view, container)
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RecyclerAdapter(this, DataProvider.getData())

        recyclerView.post({
            mBitmap = ScreenShotUtils.captureRecyclerView(recyclerView)
            showView.setImageBitmap(mBitmap)
            dismissProgress()
        })
    }


    /**
     * 跳转到WebView截图页面
     */
    fun onScreenWebView(view: View) {
        startActivity(ScreenShotWebViewActivity::class.java)
    }


    /**
     * 实战案例，点击分享，生成一张自定义图片进行分享,如果有图片加载需要监听图片是否加载完成
     */
    fun onScreenDemo(view: View) {
        showProgress()
        container.removeAllViews()
        val rootView = layoutInflater.inflate(R.layout.screen_shot_demo, container)
        val scrollView = rootView.findViewById<ScrollView>(R.id.scrollView)
        scrollView.post({
            mBitmap = ScreenShotUtils.captureScrollView(scrollView)
            showView.setImageBitmap(mBitmap)
            dismissProgress()
        })

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
        imageIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(saveBitmap(mBitmap)))
        startActivity(Intent.createChooser(imageIntent, "分享图片"))
    }


    /**
     * 保存图片到本地文件
     * @param bitmap 图片对象
     * @return 保存的文件
     */
    private fun saveBitmap(bitmap: Bitmap?): File? {
        val appDir = File(Environment.getExternalStorageDirectory(), "Images")
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