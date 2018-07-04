package com.pinger.sample.screenshot

import android.app.Activity
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.util.LruCache
import android.view.View
import android.webkit.WebView
import android.widget.ListView
import android.widget.ScrollView


/**
 * @author Pinger
 * @since 2018/7/1 下午5:27
 * 实现截屏和截图的工具类
 * 参考：[https://blog.csdn.net/billy_zuo/article/details/71077681]
 */
object ScreenShotUtils {


    /**
     * 截取当前窗体的截图，包括当前窗体的状态栏
     */
    fun getScreenShotFromWindow(activity: Activity): Bitmap?{
        return getScreenShotFromWindow(activity,true)
    }


    /**
     * 截取当前窗体的截图，根据[isShowStatusBar]判断是否包含当前窗体的状态栏
     */
    fun getScreenShotFromWindow(activity: Activity,isShowStatusBar:Boolean): Bitmap?{
        val view = activity.window.decorView
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()

        val bitmap = if (isShowStatusBar) {
            // 获取状态栏高度
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val display = activity.windowManager.defaultDisplay

            Bitmap.createBitmap(view.drawingCache, 0,
                    rect.top, display.width, display.height - rect.top)
        }else{
            Bitmap.createBitmap(view.drawingCache, 0, 0, view.measuredWidth, view.measuredHeight)
        }

        view.isDrawingCacheEnabled = false
        view.destroyDrawingCache()
        return bitmap
    }



    /**
     * View已经在界面上展示了，可以直接获取View的缓存
     * 对View进行量测，布局后截图
     * View为固定大小的View，包括TextView,ImageView,LinearLayout,FrameLayout,RelativeLayout等
     * @param view 截取的View,View必须有固定的大小，不然drawingCache返回null
     * @return 生成的Bitmap
     */
    fun getScreenShotFromView(view: View): Bitmap? {
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        view.measure(View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(view.height, View.MeasureSpec.EXACTLY))
        view.layout(view.x.toInt(), view.y.toInt(), view.x.toInt() + view.measuredWidth,
                view.y.toInt() + view.measuredHeight)
        val bitmap = Bitmap.createBitmap(view.drawingCache, 0, 0, view.measuredWidth,
                view.measuredHeight)
        view.isDrawingCacheEnabled = false
        view.destroyDrawingCache()
        return bitmap
    }


    /**
     * 截取ScrollerView
     */
    fun getScreenShotFromScrollView(scrollView: ScrollView): Bitmap?{
        // 获取子View的高度
        var h = 0
        for (i in 0 until scrollView.childCount) {
            h += scrollView.getChildAt(i).height
            // 设置子View的背景颜色，不设置默认是黑色的
            scrollView.getChildAt(i).setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        val bitmap = Bitmap.createBitmap(scrollView.width, h, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        scrollView.draw(canvas)
        return bitmap
    }



    /**
     * 截取ListView
     */
    fun getScreenShotFromListView(listView: ListView): Bitmap?{
        val adapter = listView.adapter
        val itemCount = adapter.count
        var allitemsheight = 0
        val bitmaps = ArrayList<Bitmap>()

        for (i in 0 until itemCount) {
            val childView = adapter.getView(i, null, listView)
            childView.measure(
                    View.MeasureSpec.makeMeasureSpec(listView.width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))

            childView.layout(0, 0, childView.measuredWidth, childView.measuredHeight)
            childView.isDrawingCacheEnabled = true
            childView.buildDrawingCache()
            bitmaps.add(childView.drawingCache)
            allitemsheight += childView.measuredHeight
        }

        val bitmap = Bitmap.createBitmap(listView.measuredWidth, allitemsheight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint()
        var iHeight = 0f

        for (i in bitmaps.indices) {
            val bmp: Bitmap = bitmaps[i]
            canvas.drawBitmap(bmp, 0f, iHeight, paint)
            iHeight += bmp.height

            bmp.recycle()
        }
        return bitmap
    }



    /**
     * 截取WebView，包含WebView的整个长度
     */
    fun getScreenShotFromWebView(webView: WebView): Bitmap?{
        val capture = webView.capturePicture()

        val bitmap = Bitmap.createBitmap(capture.width, capture.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        capture.draw(canvas)
        return bitmap
    }


    /**
     * 截取RecyclerView
     */
    fun getScreenShotFromRecyclerView(recyclerView: RecyclerView): Bitmap? {
        val adapter = recyclerView.adapter
        var bigBitmap: Bitmap? = null
        if (adapter != null) {
            val size = adapter.itemCount
            var height = 0
            val paint = Paint()
            var iHeight = 0
            val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

            // Use 1/8th of the available memory for this memory cache.
            val cacheSize = maxMemory / 8
            val bitmapCache = LruCache<String,Bitmap>(cacheSize)
            for (i in 0 until size) {
                val holder = adapter.createViewHolder(recyclerView, adapter.getItemViewType(i))
                adapter.onBindViewHolder(holder, i)
                holder.itemView.measure(
                        View.MeasureSpec.makeMeasureSpec(recyclerView.width, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
                holder.itemView.layout(0, 0, holder.itemView.measuredWidth,
                        holder.itemView.measuredHeight)
                holder.itemView.isDrawingCacheEnabled = true
                holder.itemView.buildDrawingCache()
                val drawingCache = holder.itemView.drawingCache
                if (drawingCache != null) {
                    bitmapCache.put(i.toString(), drawingCache)
                }
                height += holder.itemView.measuredHeight
            }

            bigBitmap = Bitmap.createBitmap(recyclerView.measuredWidth, height, Bitmap.Config.ARGB_8888)
            val bigCanvas = Canvas(bigBitmap!!)
            val lBackground = recyclerView.background
            if (lBackground is ColorDrawable) {
                val lColor = lBackground.color
                bigCanvas.drawColor(lColor)
            }

            for (i in 0 until size) {
                val bitmap = bitmapCache.get(i.toString())
                bigCanvas.drawBitmap(bitmap, 0f, iHeight.toFloat(), paint)
                iHeight += bitmap.height
                bitmap.recycle()
            }
        }
        return bigBitmap
    }


}