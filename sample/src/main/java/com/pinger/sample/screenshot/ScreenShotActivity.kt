package com.pinger.sample.screenshot

import android.view.View
import android.widget.ImageView
import com.fungo.baselib.base.basic.BaseActivity
import com.fungo.imagego.ImageManager
import com.pinger.sample.R
import kotlinx.android.synthetic.main.activity_screen_shot.*

/**
 * @author Pinger
 * @since 2018/7/1 上午1:43
 *
 */
class ScreenShotActivity : BaseActivity() {


    override val layoutResID: Int
        get() = R.layout.activity_screen_shot

    override fun initView() {
        setActionBar(getString(R.string.sample_screen_shot), true)
    }

    fun onScreenShot(view: View) {

    }

    fun onScreenScrollView(view: View) {

    }

    fun onScreenImageView(view: View) {
        container.removeAllViews()
        val imageView = ImageView(this)
        val url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1530447508136&di=a78c9b30f2fbc1acfd1d0228b72f793b&imgtype=0&src=http%3A%2F%2Fh.hiphotos.baidu.com%2Fzhidao%2Fpic%2Fitem%2Ffaedab64034f78f018a4ae837f310a55b3191c16.jpg"
        ImageManager.instance.loadImage(url,imageView)
        container.addView(imageView)

        //val bitmap = ScreenShotUtils.screenViewToBitmap(imageView)

    }








}